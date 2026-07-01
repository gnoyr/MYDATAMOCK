package com.mydata.service;

import com.mydata.domain.AdditionalReview;
import com.mydata.domain.FirstScreening;
import com.mydata.repository.AdditionalReviewRepository;
import com.mydata.repository.CreditProfileRepository;
import com.mydata.repository.FirstScreeningRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AdditionalScreeningService {

    private final AdditionalReviewRepository additionalReviewRepository;
    private final FirstScreeningRepository   firstScreeningRepository;

    public AdditionalScreeningService(AdditionalReviewRepository additionalReviewRepository,
                                      FirstScreeningRepository firstScreeningRepository,
                                      CreditProfileRepository creditProfileRepository) {
        this.additionalReviewRepository = additionalReviewRepository;
        this.firstScreeningRepository   = firstScreeningRepository;
    }

    @Transactional
    public void screen(Long creditAppId, String ciValueFromRequest) {
        if (creditAppId == null) {
            throw new IllegalArgumentException("신청 ID가 없습니다.");
        }

        FirstScreening firstScreening = firstScreeningRepository
                .findTopByCreditAppIdOrderByFirstScreeningIdDesc(creditAppId)
                .orElse(null);

        // ciValue: BNKcard 본인인증 시 생성된 값을 요청에서 직접 받음
        String ciValue = (ciValueFromRequest != null && !ciValueFromRequest.isBlank())
                ? ciValueFromRequest : "";
        if (ciValue.isBlank()) {
            log.warn("[추가심사] ciValue 없음, creditAppId={}", creditAppId);
        }

        String incomeDocKey = firstScreening != null ? firstScreening.getIncomeDocKey() : null;
        String assetDocKey  = firstScreening != null ? firstScreening.getAssetDocKey()  : null;
        String jobDocKey    = firstScreening != null ? firstScreening.getJobDocKey()    : null;

        AdditionalReview review = new AdditionalReview(
                creditAppId, ciValue, incomeDocKey, assetDocKey, jobDocKey
        );
        additionalReviewRepository.save(review);

        log.info("[추가심사] PENDING 저장 완료: creditAppId={}, ciValue존재={}", creditAppId, !ciValue.isBlank());
    }

    @Transactional
    public void updateDocs(Long creditAppId, String incomeDocKey, String assetDocKey, String jobDocKey) {
        AdditionalReview review = additionalReviewRepository
                .findTopByCreditAppIdOrderByAdditionalReviewIdDesc(creditAppId)
                .orElseThrow(() -> new IllegalArgumentException("추가심사 요청을 찾을 수 없습니다: " + creditAppId));

        if (!"PENDING".equals(review.getStatus())) {
            throw new IllegalStateException("이미 처리된 추가심사 요청입니다: " + creditAppId);
        }

        review.updateDocs(incomeDocKey, assetDocKey, jobDocKey);
        log.info("[추가심사] 서류 재제출 완료: creditAppId={}", creditAppId);
    }
}
