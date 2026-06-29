package com.mydata.service;

import com.mydata.domain.AdditionalReview;
import com.mydata.domain.CreditProfile;
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
    private final CreditProfileRepository    creditProfileRepository;

    public AdditionalScreeningService(AdditionalReviewRepository additionalReviewRepository,
                                      FirstScreeningRepository firstScreeningRepository,
                                      CreditProfileRepository creditProfileRepository) {
        this.additionalReviewRepository = additionalReviewRepository;
        this.firstScreeningRepository   = firstScreeningRepository;
        this.creditProfileRepository    = creditProfileRepository;
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

        String ciValue      = creditProfile != null ? creditProfile.getCiValue() : "";
        String incomeDocKey = firstScreening != null ? firstScreening.getIncomeDocKey() : null;
        String assetDocKey  = firstScreening != null ? firstScreening.getAssetDocKey()  : null;
        String jobDocKey    = firstScreening != null ? firstScreening.getJobDocKey()    : null;

        AdditionalReview review = new AdditionalReview(
                creditAppId, ciValue, incomeDocKey, assetDocKey, jobDocKey
        );
        additionalReviewRepository.save(review);

        log.info("[추가심사] PENDING 저장 완료: creditAppId={}", creditAppId);
    }
}
