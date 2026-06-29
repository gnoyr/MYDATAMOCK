package com.mydata.global.init;

import com.mydata.domain.CreditProfile;
import com.mydata.domain.HometaxIncome;
import com.mydata.domain.IdentityMaster;
import com.mydata.global.util.CiValueGenerator;
import com.mydata.repository.CreditProfileRepository;
import com.mydata.repository.HometaxIncomeRepository;
import com.mydata.repository.IdentityMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 앱 시작 시 테스트용 신원 마스터·신용 프로필·홈택스 소득 데이터를 seed한다.
 *
 * [테스트 계정 목록]
 * ┌────────┬──────────────┬──────────┬───────────────────────────────────────┬────────────┐
 * │ 이름   │ 주민번호(앞7) │ 신분증종류│ 주소                                  │ 발급일     │
 * ├────────┼──────────────┼──────────┼───────────────────────────────────────┼────────────┤
 * │ 홍길동 │ 9001011      │ RESIDENT │ 서울특별시 강남구 테헤란로 123         │ 2020-01-01 │
 * │ 김영희 │ 9203152      │ RESIDENT │ 부산광역시 해운대구 해운대로 456       │ 2019-06-01 │
 * └────────┴──────────────┴──────────┴───────────────────────────────────────┴────────────┘
 * CI값은 CiValueGenerator(이름+주민번호앞7+주소)로 자동 계산되므로
 * 실제 본인인증 입력값이 위 표와 정확히 일치해야 심사가 통과된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CiValueGenerator         ciValueGenerator;
    private final IdentityMasterRepository  identityMasterRepository;
    private final CreditProfileRepository  creditProfileRepository;
    private final HometaxIncomeRepository  hometaxIncomeRepository;

    // ── 테스트 사용자 정의 ────────────────────────────────────────────
    private static final List<TestUser> TEST_USERS = List.of(
        new TestUser(
            "홍길동", "900101", "1",
            "서울특별시 강남구 테헤란로 123",
            "20200101", "RESIDENT_ID",
            780, 3_000_000L, 1,
            8_000_000L, 0.5, 0,
            "EMPLOYED", 700_000L,
            48_000_000L   // 연소득 (홈택스)
        ),
        new TestUser(
            "김영희", "920315", "2",
            "부산광역시 해운대구 해운대로 456",
            "20190601", "RESIDENT_ID",
            650, 1_800_000L, 0,
            3_000_000L, 2.0, 1,
            "SELF_EMPLOYED", 600_000L,
            21_600_000L   // 연소득 (홈택스)
        )
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (TestUser u : TEST_USERS) {
            String ciValue = ciValueGenerator.generate(
                    u.name, u.residentFront, u.genderCode, u.address);

            seedIdentityMaster(u, ciValue);
            seedCreditProfile(u, ciValue);
            seedHometaxIncome(u, ciValue);
        }
    }

    // ── MYDATA_IDENTITY_MASTER ────────────────────────────────────────
    private void seedIdentityMaster(TestUser u, String ciValue) {
        if (identityMasterRepository.findByCiValueAndStatus(ciValue, "ACTIVE").isPresent()) {
            log.debug("[DataInitializer] IDENTITY_MASTER 이미 존재: name={}", u.name);
            return;
        }

        identityMasterRepository.save(new IdentityMaster(
                u.idType,
                u.name,
                u.residentFront + u.genderCode,
                u.address,
                u.issueDate,
                ciValue,
                "ACTIVE"
        ));
        log.info("[DataInitializer] IDENTITY_MASTER seed 완료: name={}, ciValue={}", u.name, ciValue);
    }

    // ── MYDATA_CREDIT_PROFILE ─────────────────────────────────────────
    private void seedCreditProfile(TestUser u, String ciValue) {
        if (creditProfileRepository.findByCiValue(ciValue).isPresent()) {
            log.debug("[DataInitializer] CREDIT_PROFILE 이미 존재: name={}", u.name);
            return;
        }

        // creditAppId=0: 아직 카드 신청 전 상태를 나타내는 더미값
        CreditProfile profile = new CreditProfile(
                0L, ciValue,
                u.creditScore, u.estimatedMonthlyIncome,
                u.carCount, u.loanBalance, u.delinquencyRate,
                u.multiDebtCount, u.jobType, u.monthlyPayment);

        creditProfileRepository.save(profile);
        log.info("[DataInitializer] CREDIT_PROFILE seed 완료: name={}", u.name);
    }

    // ── HOMETAX_INCOME ────────────────────────────────────────────────
    private void seedHometaxIncome(TestUser u, String ciValue) {
        // ciValue 기준으로 이미 있으면 skip (creditAppId=0 기준으로 조회)
        boolean exists = hometaxIncomeRepository
                .findLatestYearByCiValue(ciValue)
                .stream()
                .anyMatch(h -> "SUCCESS".equals(h.getQueryStatus()));
        if (exists) {
            log.debug("[DataInitializer] HOMETAX_INCOME 이미 존재: name={}", u.name);
            return;
        }

        int currentYear = LocalDateTime.now().getYear();
        HometaxIncome income = new HometaxIncome(
                0L, ciValue, currentYear - 1,
                "EARNED_INCOME", u.annualIncome, "SUCCESS", null);
        hometaxIncomeRepository.save(income);
        log.info("[DataInitializer] HOMETAX_INCOME seed 완료: name={}, year={}", u.name, currentYear - 1);
    }

    // ── 테스트 사용자 레코드 ──────────────────────────────────────────
    private record TestUser(
        String name,
        String residentFront,
        String genderCode,
        String address,
        String issueDate,
        String idType,
        Integer creditScore,
        Long estimatedMonthlyIncome,
        Integer carCount,
        Long loanBalance,
        Double delinquencyRate,
        Integer multiDebtCount,
        String jobType,
        Long monthlyPayment,
        Long annualIncome
    ) {}
}
