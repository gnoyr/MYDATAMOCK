package com.mydata.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.mydata.domain.CreditProfile;
import com.mydata.domain.FirstScreening;
import com.mydata.dto.AdditionalReviewResultCallbackRequest;
import com.mydata.repository.CreditProfileRepository;
import com.mydata.repository.FirstScreeningRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdditionalScreeningService {

    private static final String REVIEWED_BY       = "BNK심사센터";
    private static final long   MAX_APPROVED_LIMIT = 2_000_000L;

    private final FirstScreeningRepository firstScreeningRepository;
    private final CreditProfileRepository  creditProfileRepository;
    private final RestTemplate             restTemplate;

    @Value("${bnk.server.url}")
    private String bnkServerUrl;

    public AdditionalScreeningService(FirstScreeningRepository firstScreeningRepository,
            CreditProfileRepository creditProfileRepository,
            RestTemplate restTemplate) {
    	this.firstScreeningRepository = firstScreeningRepository;
		this.creditProfileRepository  = creditProfileRepository;
		this.restTemplate             = restTemplate;
	}

    @Transactional
    public void screen(Long creditAppId) {
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
        Long   approvedLimit;
        String rejectionReason;

        if (firstScreening == null) {
            applicationStatus = "REJECTED";
            approvedLimit     = null;
            rejectionReason   = "1차 심사 내역이 없습니다.";
        } else if (!"PASS".equals(firstScreening.getScreeningResult())) {
            applicationStatus = "REJECTED";
            approvedLimit     = null;
            rejectionReason   = "1차 심사 결과가 PASS가 아닙니다.";
        } else if (creditProfile == null) {
            applicationStatus = "REJECTED";
            approvedLimit     = null;
            rejectionReason   = "마이데이터 신용 프로필이 없습니다.";
        } else if (creditProfile.getDelinquencyRate() != null
                && creditProfile.getDelinquencyRate() > 0) {
            applicationStatus = "REJECTED";
            approvedLimit     = null;
            rejectionReason   = "연체 이력이 존재합니다.";
        } else if (creditProfile.getMultiDebtCount() != null
                && creditProfile.getMultiDebtCount() >= 5) {
            applicationStatus = "REJECTED";
            approvedLimit     = null;
            rejectionReason   = "다중채무건수가 기준을 초과했습니다.";
        } else {
            applicationStatus = "APPROVED";
            approvedLimit     = MAX_APPROVED_LIMIT;
            rejectionReason   = null;
        }

        AdditionalReviewResultCallbackRequest result = new AdditionalReviewResultCallbackRequest(
                creditAppId,
                applicationStatus,
                approvedLimit,
                rejectionReason,
                REVIEWED_BY
        );
        sendCallbackToBnk(result);
    }
    
    private void sendCallbackToBnk(AdditionalReviewResultCallbackRequest result) {
        try {
            restTemplate.postForEntity(
                bnkServerUrl + "/api/callback/credit/review-result",
                result,
                Void.class
            );
        } catch (Exception e) {
            log.error("[추가 심사] BNK 콜백 실패: creditAppId={}", result.getAppId(), e);
        }
    }
}