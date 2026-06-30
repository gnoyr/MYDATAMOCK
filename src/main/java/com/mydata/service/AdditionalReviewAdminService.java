package com.mydata.service;

import com.mydata.domain.AdditionalReview;
import com.mydata.domain.CreditProfile;
import com.mydata.domain.HometaxIncome;
import com.mydata.dto.AdditionalReviewDetailDto;
import com.mydata.dto.AdditionalReviewResultCallbackRequest;
import com.mydata.repository.AdditionalReviewRepository;
import com.mydata.repository.CreditProfileRepository;
import com.mydata.repository.HometaxIncomeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class AdditionalReviewAdminService {

    private static final String REVIEWED_BY = "BNK서류심사";

    private final AdditionalReviewRepository additionalReviewRepository;
    private final CreditProfileRepository    creditProfileRepository;
    private final HometaxIncomeRepository    hometaxIncomeRepository;
    private final RestTemplate               restTemplate;

    @Value("${bnk.server.url}")
    private String bnkServerUrl;

    @Value("${internal.callback.secret}")
    private String callbackSecret;

    public AdditionalReviewAdminService(AdditionalReviewRepository additionalReviewRepository,
                                        CreditProfileRepository creditProfileRepository,
                                        HometaxIncomeRepository hometaxIncomeRepository,
                                        RestTemplate restTemplate) {
        this.additionalReviewRepository = additionalReviewRepository;
        this.creditProfileRepository    = creditProfileRepository;
        this.hometaxIncomeRepository    = hometaxIncomeRepository;
        this.restTemplate               = restTemplate;
    }

    public List<AdditionalReview> getPendingList() {
        return additionalReviewRepository.findByStatusOrderByCreatedAtAsc("PENDING");
    }

    public AdditionalReviewDetailDto getDetail(Long creditAppId) {
        AdditionalReview review = additionalReviewRepository
                .findTopByCreditAppIdOrderByAdditionalReviewIdDesc(creditAppId)
                .orElseThrow(() -> new IllegalArgumentException("추가심사 요청을 찾을 수 없습니다: " + creditAppId));

        CreditProfile profile = creditProfileRepository
                .findTopByCreditAppIdOrderByCreditProfileIdDesc(creditAppId)
                .orElse(null);

        return new AdditionalReviewDetailDto(
                creditAppId,
                review.getIncomeDocKey(),
                review.getAssetDocKey(),
                review.getJobDocKey(),
                review.getCreatedAt(),
                profile != null ? profile.getCreditScore()    : null,
                profile != null ? profile.getLoanBalance()    : null,
                profile != null ? profile.getDelinquencyRate(): null,
                profile != null ? profile.getMultiDebtCount() : null,
                profile != null ? profile.getJobType()        : null,
                profile != null ? profile.getCarCount()       : null
        );
    }

    @Transactional
    public void submitReview(Long creditAppId,
                             boolean incomeDocOk, boolean assetDocOk, boolean jobDocOk,
                             Long estimatedMonthlyIncome) {

        AdditionalReview review = additionalReviewRepository
                .findTopByCreditAppIdOrderByAdditionalReviewIdDesc(creditAppId)
                .orElseThrow(() -> new IllegalArgumentException("추가심사 요청을 찾을 수 없습니다: " + creditAppId));

        CreditProfile profile = creditProfileRepository
                .findTopByCreditAppIdOrderByCreditProfileIdDesc(creditAppId)
                .orElse(null);

        AdditionalReviewResultCallbackRequest callback;

        // 서류 진위여부 실패 시 즉시 거절
        if (!incomeDocOk || !assetDocOk || !jobDocOk) {
            String reason = buildDocRejectionReason(incomeDocOk, assetDocOk, jobDocOk);
            callback = new AdditionalReviewResultCallbackRequest(creditAppId, reason, REVIEWED_BY);
        } else {
            // 진위여부 통과 → MYDATA_CREDIT_PROFILE.ESTIMATED_INCOME 업데이트
            if (profile != null && estimatedMonthlyIncome != null) {
                creditProfileRepository.updateEstimatedIncome(profile.getCiValue(), estimatedMonthlyIncome);
                log.info("[추가심사] CREDIT_PROFILE 월추정소득 업데이트: ciValue={}, estimatedIncome={}",
                        profile.getCiValue(), estimatedMonthlyIncome);
            }

            // HOMETAX_INCOME: NO_DATA/FAILED 레코드를 서류 확인 소득으로 갱신
            List<HometaxIncome> unverified = hometaxIncomeRepository.findUnverifiedByCreditAppId(creditAppId);
            if (!unverified.isEmpty() && estimatedMonthlyIncome != null) {
                long annualIncome = estimatedMonthlyIncome * 12;
                for (HometaxIncome h : unverified) {
                    h.verifyWithDocumentIncome(annualIncome);
                }
                hometaxIncomeRepository.saveAll(unverified);
                log.info("[추가심사] HOMETAX_INCOME {}건 SUCCESS 업데이트: creditAppId={}, annualIncome={}",
                        unverified.size(), creditAppId, annualIncome);
            }

            // BNKcard에서 한도 산정
            callback = new AdditionalReviewResultCallbackRequest(
                    creditAppId,
                    REVIEWED_BY,
                    estimatedMonthlyIncome,
                    profile != null ? profile.getCreditScore()    : null,
                    profile != null ? profile.getCarCount()       : null,
                    profile != null ? profile.getLoanBalance()    : null,
                    profile != null ? profile.getDelinquencyRate(): null,
                    profile != null ? profile.getMultiDebtCount() : null,
                    profile != null ? profile.getJobType()        : null
            );
        }

        review.complete();
        sendCallback(callback);
    }

    private String buildDocRejectionReason(boolean incomeOk, boolean assetOk, boolean jobOk) {
        StringBuilder sb = new StringBuilder("서류 진위확인 실패 - ");
        if (!incomeOk) sb.append("소득서류 ");
        if (!assetOk)  sb.append("재산서류 ");
        if (!jobOk)    sb.append("직업서류 ");
        return sb.toString().trim();
    }

    private void sendCallback(AdditionalReviewResultCallbackRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Secret", callbackSecret);
            HttpEntity<AdditionalReviewResultCallbackRequest> entity = new HttpEntity<>(request, headers);
            restTemplate.postForEntity(
                    bnkServerUrl + "/api/callback/credit/review-result",
                    entity,
                    Void.class
            );
            log.info("[추가심사] BNK 콜백 전송 완료: creditAppId={}, status={}",
                    request.getAppId(), request.getApplicationStatus());
        } catch (Exception e) {
            log.error("[추가심사] BNK 콜백 실패: creditAppId={}", request.getAppId(), e);
            throw new RuntimeException("BNK 서버 콜백 전송 실패", e);
        }
    }
}
