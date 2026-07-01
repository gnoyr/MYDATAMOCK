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
import java.util.Objects;
import java.util.Optional;

import com.mydata.domain.IdVerification;
import com.mydata.repository.IdVerificationRepository;

@Service
public class FirstScreeningService {

    private static final String REVIEWED_BY         = "BNK심사센터";

    private final FirstScreeningRepository firstScreeningRepository;
    private final CreditProfileRepository  creditProfileRepository;
    private final HometaxIncomeRepository  hometaxIncomeRepository;
    private final IdVerificationRepository idVerificationRepository;

    public FirstScreeningService(FirstScreeningRepository firstScreeningRepository,
                                 CreditProfileRepository creditProfileRepository,
                                 HometaxIncomeRepository hometaxIncomeRepository,
                                 IdVerificationRepository idVerificationRepository) {
        this.firstScreeningRepository  = firstScreeningRepository;
        this.creditProfileRepository   = creditProfileRepository;
        this.hometaxIncomeRepository   = hometaxIncomeRepository;
        this.idVerificationRepository  = idVerificationRepository;
    }

    @Transactional
    public FirstScreeningResponse screen(FirstScreeningRequest request) {
        validateRequest(request);

        // ── 0단계. CI값 검증 — 본인인증 기록과 대조 ─────────────────
        IdVerification idVerification = idVerificationRepository
                .findTopByAppIdOrderByCreatedAtDesc(request.getAppId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "본인인증 이력이 없습니다. appId=" + request.getAppId()));

        if (!"Y".equals(idVerification.getIdVerifiedYn())) {
            throw new SecurityException("본인인증이 완료되지 않은 신청입니다.");
        }
        if (!Objects.equals(idVerification.getGeneratedCiValue(), request.getCiValue())) {
            throw new SecurityException("CI값 불일치: 심사 요청의 CI값이 본인인증 기록과 다릅니다.");
        }

        // ── 1단계. 신용프로필 조회 ─────────────────────────────────
        CreditProfile creditProfile = creditProfileRepository
                .findByCiValue(request.getCiValue())
                .orElseThrow(() -> new IllegalArgumentException("신용프로필을 찾을 수 없습니다."));

        // ── 2단계. 서류 검증 ──────────────────────────────────────
        // 서류 키 형식만 검증 (OCI/ 시작 여부)
        // 신용점수·소득 판단은 BNK 서버에서 처리
        if (!isDocumentVerified(request)) {
        	throw new IllegalArgumentException("서류 키 형식이 올바르지 않습니다.");
        }

        // ── 3단계. 홈택스 조회 → 월추정소득 계산 ─────────────────
        Optional<Long> estimatedMonthlyIncomeOpt = calculateEstimatedMonthlyIncome(request);

        // ── 4단계. 소득 있으면 CREDIT_PROFILE 업데이트 ───────────
        // 소득 없어도 거절하지 않고 BNK 서버로 전달 (BNK에서 MANUAL_REQUIRED 처리)
        if (estimatedMonthlyIncomeOpt.isPresent()) {
            long estimatedMonthlyIncome = estimatedMonthlyIncomeOpt.get();
            creditProfile.setEstimatedIncome(estimatedMonthlyIncome);
            creditProfileRepository.updateEstimatedIncome(request.getCiValue(), estimatedMonthlyIncome);
        }

        // ── 5단계. 신용프로필 전체를 BNK로 전달 ──────────────────
        // 신용점수·가처분소득·연체율 등 모든 판단은 BNK 서버에서 수행
        return saveAndReturn(request, creditProfile, ScreeningDecision.pass());
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
                request.getAppId(),
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
                    request.getAppId(),
                    decision.screeningResult,
                    decision.docVerifiedYn,
                    decision.applicationStatus,
                    decision.rejectionReason,
                    REVIEWED_BY
            );
        }

        // PASS면 신용프로필 전체 포함
        return new FirstScreeningResponse(
                request.getAppId(),
                decision.screeningResult,
                decision.docVerifiedYn,
                decision.applicationStatus,
                decision.rejectionReason,
                REVIEWED_BY,
                creditProfile.getEstimatedIncome(),
                creditProfile.getMonthlyPayment(),
                creditProfile.getCreditScore(),
                creditProfile.getCarCount(),
                creditProfile.getLoanBalance(),
                creditProfile.getDelinquencyRate(),
                creditProfile.getMultiDebtCount(),
                creditProfile.getJobType()
        );
    }

    /**
     * 체크카드 심사.
     * 체크카드(직불)는 신용평가·연소득·서류가 불필요하므로 신용카드용 screen()을 쓰지 않는다.
     * 본인인증(CI) 일치만 확인되면 APPROVED를 반환하고, 실제 발급 가부(나이 조건 등)는
     * BNKcard issueCard 단계에서 판정한다.
     */
    @Transactional
    public FirstScreeningResponse screenCheck(FirstScreeningRequest request) {
        if (request == null || request.getAppId() == null) {
            throw new IllegalArgumentException("체크카드 심사 요청값이 없습니다.");
        }
        if (!hasText(request.getCiValue())) {
            throw new IllegalArgumentException("CI값이 없습니다.");
        }

        IdVerification idVerification = idVerificationRepository
                .findTopByAppIdOrderByCreatedAtDesc(request.getAppId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "본인인증 이력이 없습니다. appId=" + request.getAppId()));

        if (!"Y".equals(idVerification.getIdVerifiedYn())) {
            throw new SecurityException("본인인증이 완료되지 않은 신청입니다.");
        }
        if (!Objects.equals(idVerification.getGeneratedCiValue(), request.getCiValue())) {
            throw new SecurityException("CI값 불일치: 심사 요청의 CI값이 본인인증 기록과 다릅니다.");
        }

        ScreeningDecision decision = ScreeningDecision.pass();
        return new FirstScreeningResponse(
                request.getAppId(),
                decision.screeningResult,
                decision.docVerifiedYn,
                decision.applicationStatus,
                decision.rejectionReason,
                REVIEWED_BY
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
        // BNKcard ObjectStorageService가 저장하는 실제 키 형식: docs/income, docs/asset, docs/job
        return docKey.trim().toLowerCase().startsWith("docs/");
    }

    // ── 유효성 검증 ───────────────────────────────────────────────
    private void validateRequest(FirstScreeningRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("1차 심사 요청값이 없습니다.");
        }
        if (request.getAppId() == null) {
            throw new IllegalArgumentException("appId가 없습니다.");
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
            return new ScreeningDecision("PASS", "Y", "REVIEWING", null);
        }
    }
}