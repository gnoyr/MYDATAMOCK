package com.mydata.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mydata.domain.CreditProfile;
import com.mydata.domain.FirstScreening;
import com.mydata.dto.AdditionalReviewResultCallbackRequest;
import com.mydata.dto.CreditReviewRequest;
import com.mydata.dto.ScreeningResultCallbackRequest;
import com.mydata.repository.CreditProfileRepository;
import com.mydata.repository.FirstScreeningRepository;

@Service
public class ReviewRequestService {

    private static final String REVIEWED_BY = "BNK심사센터";
    private static final long MAX_APPROVED_LIMIT = 2_000_000L;

    private final FirstScreeningRepository firstScreeningRepository;
    private final CreditProfileRepository creditProfileRepository;

    public ReviewRequestService(FirstScreeningRepository firstScreeningRepository,
                                CreditProfileRepository creditProfileRepository) {
        this.firstScreeningRepository = firstScreeningRepository;
        this.creditProfileRepository = creditProfileRepository;
    }

    @Transactional
    public ScreeningResultCallbackRequest requestCreditReview(Long pathCreditAppId,
                                                              CreditReviewRequest request) {
        validateCreditReviewRequest(pathCreditAppId, request);

        String docVerifiedYn = isDocumentVerified(request) ? "Y" : "N";

        Long estimatedMonthlyIncome = estimateMonthlyIncome(request.getAnnualIncomeBand());
        Integer creditScore = estimateCreditScore(request.getCreditScoreBand());
        Integer vehicleCount = 1;
        Long loanBalance = 5_000_000L;
        Double delinquencyRate = 0.0;
        Integer multipleDebtCount = 2;
        String jobType = "EMPLOYED";

        String screeningResult;
        String applicationStatus;
        String rejectionReason;

        if ("N".equals(docVerifiedYn)) {
            screeningResult = "REJECTED";
            applicationStatus = "REJECTED";
            rejectionReason = "서류 키 형식이 올바르지 않습니다.";
        } else if (creditScore < 600) {
            screeningResult = "REJECTED";
            applicationStatus = "REJECTED";
            rejectionReason = "신용점수 기준에 부합하지 않습니다.";
        } else if (estimatedMonthlyIncome < 2_000_000L) {
            screeningResult = "REJECTED";
            applicationStatus = "REJECTED";
            rejectionReason = "추정 월소득 기준에 부합하지 않습니다.";
        } else {
            screeningResult = "PASS";
            applicationStatus = "REQUESTED";
            rejectionReason = null;
        }

        FirstScreening firstScreening = new FirstScreening(
                request.getCreditAppId(),
                normalize(request.getAnnualIncomeBand()),
                normalize(request.getCreditScoreBand()),
                normalizeNullable(request.getIncomeDocKey()),
                normalizeNullable(request.getAssetDocKey()),
                normalizeNullable(request.getJobDocKey()),
                screeningResult,
                docVerifiedYn,
                applicationStatus,
                rejectionReason,
                REVIEWED_BY
        );

        firstScreeningRepository.save(firstScreening);

        CreditProfile creditProfile = new CreditProfile(
                request.getCreditAppId(),
                creditScore,
                estimatedMonthlyIncome,
                vehicleCount,
                loanBalance,
                delinquencyRate,
                multipleDebtCount,
                jobType
        );

        creditProfileRepository.save(creditProfile);

        return new ScreeningResultCallbackRequest(
                request.getCreditAppId(),
                screeningResult,
                docVerifiedYn,
                applicationStatus,
                rejectionReason,
                REVIEWED_BY,
                estimatedMonthlyIncome,
                creditScore,
                vehicleCount,
                loanBalance,
                delinquencyRate,
                multipleDebtCount,
                jobType
        );
    }

    @Transactional(readOnly = true)
    public AdditionalReviewResultCallbackRequest requestAdditionalReview(Long creditAppId) {
        if (creditAppId == null) {
            throw new IllegalArgumentException("신청 ID가 없습니다.");
        }

        FirstScreening firstScreening = firstScreeningRepository
                .findTopByCreditAppIdOrderByFirstScreeningIdDesc(creditAppId)
                .orElse(null);

        CreditProfile creditProfile = creditProfileRepository
                .findTopByCreditAppIdOrderByCreditProfileIdDesc(creditAppId)
                .orElse(null);

        String applicationStatus;
        Long approvedLimit;
        String rejectionReason;

        if (firstScreening == null) {
            applicationStatus = "REJECTED";
            approvedLimit = null;
            rejectionReason = "1차 심사 내역이 없습니다.";
        } else if (!"PASS".equals(firstScreening.getScreeningResult())) {
            applicationStatus = "REJECTED";
            approvedLimit = null;
            rejectionReason = "1차 심사 결과가 PASS가 아닙니다.";
        } else if (creditProfile == null) {
            applicationStatus = "REJECTED";
            approvedLimit = null;
            rejectionReason = "마이데이터 신용 프로필이 없습니다.";
        } else if (creditProfile.getDelinquencyRate() != null
                && creditProfile.getDelinquencyRate() > 0) {
            applicationStatus = "REJECTED";
            approvedLimit = null;
            rejectionReason = "연체 이력이 존재합니다.";
        } else if (creditProfile.getMultiDebtCount() != null
                && creditProfile.getMultiDebtCount() >= 5) {
            applicationStatus = "REJECTED";
            approvedLimit = null;
            rejectionReason = "다중채무건수가 기준을 초과했습니다.";
        } else {
            applicationStatus = "APPROVED";
            approvedLimit = MAX_APPROVED_LIMIT;
            rejectionReason = null;
        }

        return new AdditionalReviewResultCallbackRequest(
                creditAppId,
                applicationStatus,
                approvedLimit,
                rejectionReason,
                REVIEWED_BY
        );
    }

    private void validateCreditReviewRequest(Long pathCreditAppId, CreditReviewRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("1차 심사 요청값이 없습니다.");
        }

        if (pathCreditAppId == null) {
            throw new IllegalArgumentException("경로의 신청 ID가 없습니다.");
        }

        if (request.getCreditAppId() == null) {
            throw new IllegalArgumentException("본문의 신청 ID가 없습니다.");
        }

        if (!pathCreditAppId.equals(request.getCreditAppId())) {
            throw new IllegalArgumentException("경로의 신청 ID와 본문의 신청 ID가 일치하지 않습니다.");
        }

        if (!hasText(request.getCiValue())) {
            throw new IllegalArgumentException("CI 값이 없습니다.");
        }

        if (request.getRequestedLimit() == null || request.getRequestedLimit() <= 0) {
            throw new IllegalArgumentException("요청 한도가 올바르지 않습니다.");
        }

        if (!hasText(request.getCreditScoreBand())) {
            throw new IllegalArgumentException("신용점수 구간이 없습니다.");
        }

        if (!hasText(request.getAnnualIncomeBand())) {
            throw new IllegalArgumentException("연소득 구간이 없습니다.");
        }
    }

    private boolean isDocumentVerified(CreditReviewRequest request) {
        return isValidDocKeyIfPresent(request.getIncomeDocKey())
                && isValidDocKeyIfPresent(request.getAssetDocKey())
                && isValidDocKeyIfPresent(request.getJobDocKey());
    }

    private boolean isValidDocKeyIfPresent(String docKey) {
        if (!hasText(docKey)) {
            return true;
        }

        return docKey.trim().toLowerCase().startsWith("oci/");
    }

    private Long estimateMonthlyIncome(String annualIncomeBand) {
        String band = normalize(annualIncomeBand);

        if ("LV1".equals(band)) {
            return 1_500_000L;
        }

        if ("LV2".equals(band)) {
            return 3_000_000L;
        }

        if ("LV3".equals(band)) {
            return 4_000_000L;
        }

        if ("LV4".equals(band)) {
            return 5_000_000L;
        }

        return 2_000_000L;
    }

    private Integer estimateCreditScore(String creditScoreBand) {
        String band = normalize(creditScoreBand);

        if ("HIGH".equals(band)) {
            return 750;
        }

        if ("MID".equals(band)) {
            return 650;
        }

        if ("LOW".equals(band)) {
            return 550;
        }

        return 600;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private String normalizeNullable(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}