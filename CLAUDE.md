# MYDATAMOCK 프로젝트

## 서버 구성
- MYDATAMOCK 서버: `http://192.168.50.30:8081` (Spring Boot, Oracle DB `mydatamock_medium`)
- DB: Oracle Cloud (`Wallet_MYDATAMOCK`), username=ADMIN
- BNKcard 서버: `http://192.168.50.30:8088` (`bnkcard.api.base-url`)
- CI salt: `ci.salt=BNKcard-CI-Salt-2025!@#$%` (BNKcard와 반드시 동일해야 함)
- 콜백 인증: `internal.callback.secret` (BNKcard와 동일 값)

## 주요 테이블 (mydatamock DB - ADMIN 스키마)
- `MYDATA_IDENTITY_MASTER` — 신원 원장 (본인확인 대조 기준)
  - CI_VALUE(UNIQUE), ID_TYPE, ID_NAME, ID_RESIDENT_NO(7자리), ID_ADDRESS, ID_ISSUE_DATE, STATUS('ACTIVE')
- `MYDATA_ID_VERIFICATIONS` — 본인확인 이력 (신용/체크 공용)
  - APP_ID, GENERATED_CI_VALUE, MATCHED_IDENTITY_ID, ID_VERIFIED_YN(Y/N)
- `MYDATA_CREDIT_PROFILE` — 신용 프로파일
  - CI_VALUE, CREDIT_SCORE(0~1000), ESTIMATED_INCOME, LOAN_BALANCE, DELINQUENCY_RATE, MULTI_DEBT_COUNT
  - 600점 이하 즉시 거절
- `MYDATA_FIRST_SCREENING` — 1차 심사 결과
  - SCREENING_RESULT(PASS/REJECTED), APPLICATION_STATUS, DOC_VERIFIED_YN
- `MYDATA_ADDITIONAL_REVIEW` — 추가 심사 (신용카드 REVIEWING 케이스)
  - STATUS(PENDING/COMPLETED)
- `HOMETAX_INCOME` — 홈택스 소득 이력
  - CI_VALUE, TAX_YEAR, INCOME_TYPE(EMPLOYMENT/BUSINESS/PENSION/RENTAL), ANNUAL_INCOME
  - 월추정소득 = SUM(annual_income) ÷ 12

## 주요 API
- `POST /api/mydata/id-verification` — 본인확인 (BNKcard에서 호출)
  - 요청: appId, idType, idName, idResidentNo(7자리), idAddress, idIssueDate
  - CI 생성 후 MYDATA_IDENTITY_MASTER에서 조회 → Y/N 반환
- `POST /api/mydata/first-screening` — 1차 심사 (BNKcard submit 후 자동 호출)

## 본인확인 로직
1. `idResidentNo`에서 앞6자리(생년월일) + 성별코드 1자리 추출
2. CI = CiValueGenerator.generate(이름, 생년월일, 성별코드, 주소)
3. `MYDATA_IDENTITY_MASTER`에서 `CI_VALUE = 생성된CI AND STATUS = 'ACTIVE'` 조회
4. 있으면 Y, 없으면 N

## CI값 주의사항
- BNKcard의 CI 생성 로직과 MYDATAMOCK의 CI 생성 로직이 **반드시 동일**해야 함
- 로직 변경 시 `MYDATA_IDENTITY_MASTER` 데이터도 새 로직으로 재생성 필요
- `ci.salt` 값이 양쪽 서버에서 동일해야 함

## 테스트 데이터 (MYDATA_IDENTITY_MASTER)
| identity_id | id_name | id_resident_no | status |
|------------|---------|----------------|--------|
| 1 | 홍길동 | 9012251 | ACTIVE |
| 37 | 홍길동 | 9001011 | ACTIVE |
| 38 | 김영희 | 9203152 | ACTIVE |
- 김현길(9501141) 데이터 없음 → CI 로직 동기화 후 추가 필요
