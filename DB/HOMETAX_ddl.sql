-- ================================================================
-- BNK 부산은행 — 홈택스 신고소득 테이블 DDL
-- Oracle 21c
-- 작성일: 2026-06-26
--
-- [개요]
--   신용카드 심사 시 홈택스 확인소득(근로/사업/연금/임대)을 조회하여
--   월추정소득 산출을 위한 연간 신고소득 원본을 저장하는 테이블.
--   고객 동의 기반 마이데이터/스크래핑으로 수신한 데이터를 적재.
--   신청마다 새로 조회하여 INSERT (이력 누적 방식).
--   소득 유형별 + 귀속연도별 1건씩 적재. (신청 1건당 최대 8건)
--   월추정소득 계산 = SUM(annual_income) ÷ 12 → CREDIT_CARD_APPLICATIONS.estimated_monthly_income 저장
--
-- [연관 테이블]
--   USERS(user_id)
--   CREDIT_CARD_APPLICATIONS(credit_app_id)
-- ================================================================


-- ================================================================
-- [SECTION 1] DROP (재실행 대비)
-- ================================================================

BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER TRG_HOMETAX_INCOME_BU'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER TRG_HOMETAX_INCOME_BI'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP INDEX IDX_HOMETAX_STATUS';  EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP INDEX IDX_HOMETAX_USER_ID'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP INDEX IDX_HOMETAX_APP_ID';  EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE HOMETAX_INCOME CASCADE CONSTRAINTS PURGE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_HOMETAX_INCOME'; EXCEPTION WHEN OTHERS THEN NULL; END;
/


-- ================================================================
-- [SECTION 2] CREATE
-- ================================================================

-- ── 시퀀스 ────────────────────────────────────────────────────────
CREATE SEQUENCE SEQ_HOMETAX_INCOME START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- ── 테이블 ────────────────────────────────────────────────────────
CREATE TABLE HOMETAX_INCOME (

    -- ── PK / 연관키 ──────────────────────────────────────────────
    hometax_income_id        NUMBER(19)                            NOT NULL,  -- PK
    ci_value                 VARCHAR2(500)                         NOT NULL,  -- 사용자 식별 (BNK에서 넘어온 AES 암호화값)
    credit_app_id            NUMBER(19)                            NOT NULL,

    -- ── 소득 정보 ─────────────────────────────────────────────────
    tax_year                 NUMBER(4)                             NOT NULL,  -- 귀속연도 (예: 2025)
    income_type              VARCHAR2(20)                          NOT NULL
        CHECK (income_type IN ('EMPLOYMENT', 'BUSINESS', 'PENSION', 'RENTAL')),
                                                                              -- 소득 유형
                                                                              -- EMPLOYMENT : 근로소득 (직장인)
                                                                              -- BUSINESS   : 사업소득 (자영업자, 프리랜서)
                                                                              -- PENSION    : 연금소득 (국민연금, 공무원연금 등)
                                                                              -- RENTAL     : 임대소득 (부동산 월세 등 반복 수익)
    annual_income            NUMBER(15)       DEFAULT 0            NOT NULL,  -- 연간 신고소득 (원). 홈택스 원본값

    -- ── 조회 메타 ─────────────────────────────────────────────────
    query_status             VARCHAR2(20)     DEFAULT 'SUCCESS'    NOT NULL
        CHECK (query_status IN ('SUCCESS', 'FAILED', 'NO_DATA')),            -- 홈택스 API 조회 결과
                                                                              -- SUCCESS : 정상 수신
                                                                              -- FAILED  : API 오류 등 조회 실패 → 서류 제출 요청
                                                                              -- NO_DATA : 해당 소득 유형 신고 내역 없음 → 서류 제출 요청
    fail_reason              VARCHAR2(500),                                   -- query_status = FAILED 시 실패 사유
    queried_at               TIMESTAMP        DEFAULT SYSTIMESTAMP NOT NULL,  -- 홈택스 API 실제 호출 일시
    created_at               TIMESTAMP        DEFAULT SYSTIMESTAMP NOT NULL,  -- 레코드 생성 일시
    updated_at               TIMESTAMP,                                       -- BU 트리거 자동 갱신

    -- ── 제약 ─────────────────────────────────────────────────────
    CONSTRAINT PK_HOMETAX_INCOME         PRIMARY KEY (hometax_income_id),
    CONSTRAINT UQ_HOMETAX_APP_YEAR_TYPE  UNIQUE (credit_app_id, tax_year, income_type)
    -- 동일 신청 건에 같은 귀속연도 + 소득유형 중복 INSERT 방지
);


-- ── 코멘트 ────────────────────────────────────────────────────────
COMMENT ON TABLE  HOMETAX_INCOME                  IS '홈택스 신고소득 조회 이력. 신용카드 신청마다 직전 2개년 × 소득유형별 INSERT. 월추정소득은 SUM(annual_income)÷12 → CREDIT_CARD_APPLICATIONS.estimated_monthly_income에 저장';

COMMENT ON COLUMN HOMETAX_INCOME.hometax_income_id IS 'PK';
COMMENT ON COLUMN HOMETAX_INCOME.ci_value          IS 'BNK 서버에서 넘어온 사용자 식별값. AES 암호화 저장';
COMMENT ON COLUMN HOMETAX_INCOME.credit_app_id     IS 'BNK 신청 ID. 참조용 (FK 없음, 다른 DB)';
COMMENT ON COLUMN HOMETAX_INCOME.tax_year          IS '귀속연도. 직전 2개년 조회. 예: 2026년 신청 시 2024, 2025';
COMMENT ON COLUMN HOMETAX_INCOME.income_type       IS '소득 유형: EMPLOYMENT=근로 / BUSINESS=사업 / PENSION=연금 / RENTAL=임대';
COMMENT ON COLUMN HOMETAX_INCOME.annual_income     IS '연간 신고소득(원). 홈택스 원본값. 근로=총급여 / 사업=종합소득세 신고소득 / 연금=연금소득 / 임대=임대소득';
COMMENT ON COLUMN HOMETAX_INCOME.query_status      IS '홈택스 API 조회 결과: SUCCESS=정상 / FAILED=조회실패 / NO_DATA=신고내역없음. FAILED·NO_DATA 시 서류 제출 요청';
COMMENT ON COLUMN HOMETAX_INCOME.fail_reason       IS 'query_status = FAILED 시 실패 사유';
COMMENT ON COLUMN HOMETAX_INCOME.queried_at        IS '홈택스 API 실제 호출 일시';
COMMENT ON COLUMN HOMETAX_INCOME.created_at        IS '레코드 생성 일시';
COMMENT ON COLUMN HOMETAX_INCOME.updated_at        IS '수정 일시. BU 트리거 자동 갱신';


-- ── 트리거 ────────────────────────────────────────────────────────
CREATE OR REPLACE TRIGGER TRG_HOMETAX_INCOME_BI
BEFORE INSERT ON HOMETAX_INCOME
FOR EACH ROW
WHEN (NEW.hometax_income_id IS NULL)
BEGIN
    :NEW.hometax_income_id := SEQ_HOMETAX_INCOME.NEXTVAL;
END TRG_HOMETAX_INCOME_BI;
/

CREATE OR REPLACE TRIGGER TRG_HOMETAX_INCOME_BU
BEFORE UPDATE ON HOMETAX_INCOME
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_HOMETAX_INCOME_BU;
/


-- ── 인덱스 ────────────────────────────────────────────────────────
-- 심사 서버 주요 조회 패턴: credit_app_id + tax_year + query_status
CREATE INDEX IDX_HOMETAX_APP_ID  ON HOMETAX_INCOME (credit_app_id);
CREATE INDEX IDX_HOMETAX_CI_VALUE ON HOMETAX_INCOME (ci_value);
CREATE INDEX IDX_HOMETAX_STATUS  ON HOMETAX_INCOME (query_status);


COMMIT;


-- ================================================================
-- [참고] 데이터 적재 예시 (2026년 신청 기준)
-- ================================================================
/*
── 일반 직장인 ─────────────────────────────────────────────────────
CREDIT_APP_ID | TAX_YEAR | INCOME_TYPE | ANNUAL_INCOME | QUERY_STATUS
101           | 2025     | EMPLOYMENT  | 48,000,000    | SUCCESS       ← 심사 사용
101           | 2024     | EMPLOYMENT  | 44,000,000    | SUCCESS       ← fallback

── 겸업자 (직장 + 임대) ────────────────────────────────────────────
CREDIT_APP_ID | TAX_YEAR | INCOME_TYPE | ANNUAL_INCOME | QUERY_STATUS
102           | 2025     | EMPLOYMENT  | 48,000,000    | SUCCESS
102           | 2025     | RENTAL      | 12,000,000    | SUCCESS
102           | 2024     | EMPLOYMENT  | 44,000,000    | SUCCESS
102           | 2024     | RENTAL      | 10,000,000    | SUCCESS

── 주부 / 학생 (소득 없음) ─────────────────────────────────────────
CREDIT_APP_ID | TAX_YEAR | INCOME_TYPE | ANNUAL_INCOME | QUERY_STATUS
103           | 2025     | EMPLOYMENT  | 0             | NO_DATA       ← 서류 제출 요청
103           | 2024     | EMPLOYMENT  | 0             | NO_DATA       ← 서류 제출 요청

── 심사 서버 월추정소득 계산 쿼리 ──────────────────────────────────
SELECT SUM(annual_income) / 12 AS estimated_monthly_income
FROM   HOMETAX_INCOME
WHERE  credit_app_id = :credit_app_id
AND    tax_year      = (SELECT MAX(tax_year)
                        FROM   HOMETAX_INCOME
                        WHERE  credit_app_id = :credit_app_id
                        AND    query_status  = 'SUCCESS')
AND    query_status  = 'SUCCESS';
*/


-- ================================================================
-- MYDATA_CREDIT_PROFILE 컬럼 추가
-- ================================================================

ALTER TABLE MYDATA_CREDIT_PROFILE ADD (
    MONTHLY_PAYMENT NUMBER(15, 0) DEFAULT 0 NOT NULL
);
ALTER TABLE MYDATA_CREDIT_PROFILE ADD (
    CI_VALUE VARCHAR2(500) NOT NULL
);

-- ── 테이블 코멘트 ─────────────────────────────────────────────────
COMMENT ON TABLE MYDATA_CREDIT_PROFILE IS '마이데이터 신용 프로필. 신용카드 심사 시 활용하는 차주의 신용·부채·소득 정보. BNK 서버 심사 요청 시 ESTIMATED_INCOME 업데이트 후 전체 응답';

-- ── 컬럼 코멘트 ──────────────────────────────────────────────────
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.CREDIT_PROFILE_ID IS 'PK. Identity Column 자동채번';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.CI_VALUE          IS '사용자 식별값. BNK에서 넘어온 AES 암호화 CI값. HOMETAX_INCOME.CI_VALUE와 연결';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.CREDIT_APP_ID     IS 'BNK 신청 ID. 참조용 (FK 없음, 다른 DB)';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.CREDIT_SCORE      IS '개인신용점수 (0~1000). 600점 이하 즉시 거절';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.ESTIMATED_INCOME  IS '월추정소득(원). 홈택스 신고소득 기반으로 계산. SUM(annual_income) ÷ 12. 심사 요청 시 업데이트';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.CAR_COUNT         IS '차량 보유 수. 자산 보유 현황 참고용';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.LOAN_BALANCE      IS '전체 대출 잔액(원). 주택담보·신용·카드론 등 모든 대출 합산';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.DELINQUENCY_RATE  IS '연체율(%). 소수점 2자리. 예: 5.25 = 5.25%';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.MULTI_DEBT_COUNT  IS '다중채무 건수. 3건 이상이면 고위험 차주로 분류';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.JOB_TYPE          IS '직업 유형. EMPLOYED=직장인 / SELF_EMPLOYED=자영업자 / STUDENT=학생 / UNEMPLOYED=무직·전업주부 / OTHER=기타';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.MONTHLY_PAYMENT   IS '월 대출 원리금 상환액(원). 모든 대출 합산 월상환액. 월가처분소득 = ESTIMATED_INCOME - MONTHLY_PAYMENT 계산에 사용';
COMMENT ON COLUMN MYDATA_CREDIT_PROFILE.CREATED_AT        IS '레코드 생성 일시';
commit;