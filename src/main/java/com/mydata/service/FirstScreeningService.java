package com.mydata.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mydata.domain.FirstScreening;
import com.mydata.dto.FirstScreeningRequest;
import com.mydata.dto.FirstScreeningResponse;
import com.mydata.repository.FirstScreeningRepository;

@Service
public class FirstScreeningService {

    private static final String REVIEWED_BY = "BNK심사센터";

    private final FirstScreeningRepository firstScreeningRepository;

    public FirstScreeningService(FirstScreeningRepository firstScreeningRepository) {
        this.firstScreeningRepository = firstScreeningRepository;
    }

    @Transactional
    public FirstScreeningResponse screen(FirstScreeningRequest request) {
        validateRequest(request);

        ScreeningDecision decision = judgeScreening(request);

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

        return new FirstScreeningResponse(
                request.getCreditAppId(),
                decision.screeningResult,
                decision.docVerifiedYn,
                decision.applicationStatus,
                decision.rejectionReason,
                REVIEWED_BY
        );
    }

    private ScreeningDecision judgeScreening(FirstScreeningRequest request) {
        boolean docVerified = isDocumentVerified(request);

        if (!docVerified) {
            return ScreeningDecision.rejected(
                    "N",
                    "서류 키 형식이 올바르지 않습니다."
            );
        }

        int incomeLevel = parseIncomeLevel(request.getAnnualIncomeBand());
        String creditScoreBand = normalize(request.getCreditScoreBand());

        if (incomeLevel < 2) {
            return ScreeningDecision.rejected(
                    "Y",
                    "연소득 구간이 심사 기준에 부합하지 않습니다."
            );
        }

        if ("LOW".equals(creditScoreBand)) {
            return ScreeningDecision.rejected(
                    "Y",
                    "신용점수 구간이 심사 기준에 부합하지 않습니다."
            );
        }

        return ScreeningDecision.pass();
    }

    private void validateRequest(FirstScreeningRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("1차 심사 요청값이 없습니다.");
        }

        if (request.getCreditAppId() == null) {
            throw new IllegalArgumentException("신청 ID가 없습니다.");
        }

        if (!hasText(request.getAnnualIncomeBand())) {
            throw new IllegalArgumentException("연소득 구간이 없습니다.");
        }

        if (!hasText(request.getCreditScoreBand())) {
            throw new IllegalArgumentException("신용점수 구간이 없습니다.");
        }

        String annualIncomeBand = normalize(request.getAnnualIncomeBand());
        if (!annualIncomeBand.matches("LV\\d+")) {
            throw new IllegalArgumentException("연소득 구간은 LV1, LV2 형식이어야 합니다.");
        }
    }

    private boolean isDocumentVerified(FirstScreeningRequest request) {
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

    private int parseIncomeLevel(String annualIncomeBand) {
        String normalized = normalize(annualIncomeBand);
        return Integer.parseInt(normalized.replace("LV", ""));
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

    private static class ScreeningDecision {

        private final String screeningResult;
        private final String docVerifiedYn;
        private final String applicationStatus;
        private final String rejectionReason;

        private ScreeningDecision(String screeningResult,
                                  String docVerifiedYn,
                                  String applicationStatus,
                                  String rejectionReason) {
            this.screeningResult = screeningResult;
            this.docVerifiedYn = docVerifiedYn;
            this.applicationStatus = applicationStatus;
            this.rejectionReason = rejectionReason;
        }

        private static ScreeningDecision pass() {
            return new ScreeningDecision(
                    "PASS",
                    "Y",
                    "APPROVED",
                    null
            );
        }

        private static ScreeningDecision rejected(String docVerifiedYn, String rejectionReason) {
            return new ScreeningDecision(
                    "REJECTED",
                    docVerifiedYn,
                    "REJECTED",
                    rejectionReason
            );
        }
    }
}