package com.mydata.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mydata.domain.CreditProfile;
import com.mydata.domain.FirstScreening;
import com.mydata.domain.HometaxIncome;
import com.mydata.dto.FirstScreeningRequest;
import com.mydata.dto.FirstScreeningResponse;
import com.mydata.repository.CreditProfileRepository;
import com.mydata.repository.FirstScreeningRepository;
import com.mydata.repository.HometaxIncomeRepository;

import java.util.List;
import java.util.Optional;
@Service
public class FirstScreeningService {

    private static final String REVIEWED_BY         = "BNK심사센터";
    private static final int    MIN_CREDIT_SCORE     = 600;
    private static final long   MIN_DISPOSABLE_INCOME = 500000L;  // 월가처분소득 최소 50만원

    private final FirstScreeningRepository firstScreeningRepository;
    private final CreditProfileRepository  creditProfileRepository;
    private final HometaxIncomeRepository  hometaxIncomeRepository;

    public FirstScreeningService(FirstScreeningRepository firstScreeningRepository,
                                 CreditProfileRepository creditProfileRepository,
                                 HometaxIncomeRepository hometaxIncomeRepository) {
        this.firstScreeningRepository = firstScreeningRepository;
        this.creditProfileRepository  = creditProfileRepository;
        this.hometaxIncomeRepository  = hometaxIncomeRepository;
    }

    @Transactional
    public FirstScreeningResponse screen(FirstScreeningRequest request) {
        validateRequest(request);

        // ── 1단계. 신용프로필 조회 ─────────────────────────────────
        CreditProfile creditProfile = creditProfileRepository
                .findByCiValue(request.getCiValue())
                .orElseThrow(() -> new IllegalArgumentException("신용프로필을 찾을 수 없습니다."));

        FirstScreeningResponse result;

        // ── 2단계. 신용점수 체크 (600점 이하 즉시 거절) ───────────
        if (creditProfile.getCreditScore() <= MIN_CREDIT_SCORE) {
            result = saveAndReturn(request, creditProfile,
                    ScreeningDecision.rejected("Y", "신용점수가 심사 기준에 부합하지 않습니다."));

        // ── 3단계. 서류 검증 ──────────────────────────────────────
        } else if (!isDocumentVerified(request)) {
            result = saveAndReturn(request, creditProfile,
                    ScreeningDecision.rejected("N", "서류 키 형식이 올바르지 않습니다."));

        // ── 4단계. 홈택스 조회 → 월추정소득 계산 ─────────────────
        } else {
            Optional<Long> estimatedMonthlyIncomeOpt = calculateEstimatedMonthlyIncome(request);

            // 소득 없음 → BNK 서버에서 MANUAL_REQUIRED로 처리
            if (estimatedMonthlyIncomeOpt.isEmpty()) {
                result = saveAndReturn(request, creditProfile, ScreeningDecision.pass());

            } else {
                long estimatedMonthlyIncome = estimatedMonthlyIncomeOpt.get();

                // ── 5단계. 월가처분소득 체크 ──────────────────────────────
                long disposableIncome = estimatedMonthlyIncome - creditProfile.getMonthlyPayment();
                if (disposableIncome <= MIN_DISPOSABLE_INCOME) {
                    result = saveAndReturn(request, creditProfile,
                            ScreeningDecision.rejected("Y", "월 가처분소득이 심사 기준에 부합하지 않습니다."));

                // ── 6단계. CREDIT_PROFILE ESTIMATED_INCOME 업데이트 ──────
                } else {
                    creditProfile.setEstimatedIncome(estimatedMonthlyIncome);
                    creditProfileRepository.updateEstimatedIncome(request.getCiValue(), estimatedMonthlyIncome);

                    // ── 7단계. PASS → 신용프로필 전체 응답 ───────────────────
                    result = saveAndReturn(request, creditProfile, ScreeningDecision.pass());
                }
            }
        }

        return result;
    }

    // ── 홈택스 조회 및 월추정소득 계산 ────────────────────────────
    private Optional<Long> calculateEstimatedMonthlyIncome(FirstScreeningRequest request) {
        List<HometaxIncome> incomeList = hometaxIncomeRepository
                .findLatestYearByCiValue(request.getCiValue());

        if (incomeList == null || incomeList.isEmpty()) {
            return Optional.empty();  // 소득 없음 → BNK에서 MANUAL_REQUIRED 처리
        }

        long annualTotal = incomeList.stream()
                .filter(h -> "SUCCESS".equals(h.getQueryStatus()))
                .mapToLong(HometaxIncome::getAnnualIncome)
                .sum();

        return Optional.of(annualTotal / 12);
    }

    // ── 심사 결과 저장 + 응답 생성 ────────────────────────────────
    private FirstScreeningResponse saveAndReturn(FirstScreeningRequest request,
                                                 CreditProfile creditProfile,
                                                 ScreeningDecision decision) {
        FirstScreening entity = new FirstScreening(
                request.getCreditAppId(),
                normalize(request.getAnnualIncomeBand()),
                normalize(request.getCreditScoreBand()),
                normalizeNullable(request.getIncomeDocKey()),
                normalizeNullable(request.getAssetDocKey()),
                normalizeNullable(request.getJobDocKey()),
                decision.screeningResult,
                decision.docVerifiedYn,
                decision.applicationStatus,
                decision.rejectionReason,
                REVIEWED_BY
        );
        firstScreeningRepository.save(entity);

        // REJECTED면 신용프로필 null로 응답
        if ("REJECTED".equals(decision.screeningResult)) {
            return new FirstScreeningResponse(
                    request.getCreditAppId(),
                    decision.screeningResult,
                    decision.docVerifiedYn,
                    decision.applicationStatus,
                    decision.rejectionReason,
                    REVIEWED_BY
            );
        }

        // PASS면 신용프로필 전체 포함
        return new FirstScreeningResponse(
                request.getCreditAppId(),
                decision.screeningResult,
                decision.docVerifiedYn,
                decision.applicationStatus,
                decision.rejectionReason,
                REVIEWED_BY,
                creditProfile.getEstimatedIncome(),
                creditProfile.getCreditScore(),
                creditProfile.getCarCount(),
                creditProfile.getLoanBalance(),
                creditProfile.getDelinquencyRate(),
                creditProfile.getMultiDebtCount(),
                creditProfile.getJobType()
        );
    }

    // ── 서류 검증 ─────────────────────────────────────────────────
    private boolean isDocumentVerified(FirstScreeningRequest request) {
        return isValidDocKeyIfPresent(request.getIncomeDocKey())
                && isValidDocKeyIfPresent(request.getAssetDocKey())
                && isValidDocKeyIfPresent(request.getJobDocKey());
    }

    private boolean isValidDocKeyIfPresent(String docKey) {
        if (!hasText(docKey)) return true;
        return docKey.trim().toLowerCase().startsWith("oci/");
    }

    // ── 유효성 검증 ───────────────────────────────────────────────
    private void validateRequest(FirstScreeningRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("1차 심사 요청값이 없습니다.");
        }
        if (request.getCreditAppId() == null) {
            throw new IllegalArgumentException("신청 ID가 없습니다.");
        }
        if (!hasText(request.getCiValue())) {
            throw new IllegalArgumentException("CI값이 없습니다.");
        }
        if (!hasText(request.getAnnualIncomeBand())) {
            throw new IllegalArgumentException("연소득 구간이 없습니다.");
        }
        if (!hasText(request.getCreditScoreBand())) {
            throw new IllegalArgumentException("신용점수 구간이 없습니다.");
        }
        if (!normalize(request.getAnnualIncomeBand()).matches("LV\\d+")) {
            throw new IllegalArgumentException("연소득 구간은 LV1, LV2 형식이어야 합니다.");
        }
    }

    // ── 유틸 ──────────────────────────────────────────────────────
    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private String normalizeNullable(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // ── 심사 결과 ─────────────────────────────────────────────────
    private static class ScreeningDecision {

        private final String screeningResult;
        private final String docVerifiedYn;
        private final String applicationStatus;
        private final String rejectionReason;

        private ScreeningDecision(String screeningResult,
                                  String docVerifiedYn,
                                  String applicationStatus,
                                  String rejectionReason) {
            this.screeningResult   = screeningResult;
            this.docVerifiedYn     = docVerifiedYn;
            this.applicationStatus = applicationStatus;
            this.rejectionReason   = rejectionReason;
        }

        private static ScreeningDecision pass() {
            return new ScreeningDecision("PASS", "Y", "APPROVED", null);
        }

        private static ScreeningDecision rejected(String docVerifiedYn, String rejectionReason) {
            return new ScreeningDecision("REJECTED", docVerifiedYn, "REJECTED", rejectionReason);
        }
    }
}