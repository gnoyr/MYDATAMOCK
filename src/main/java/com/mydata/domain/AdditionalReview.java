package com.mydata.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MYDATA_ADDITIONAL_REVIEW")
public class AdditionalReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDITIONAL_REVIEW_ID")
    private Long additionalReviewId;

    @Column(name = "CREDIT_APP_ID", nullable = false)
    private Long creditAppId;

    @Column(name = "CI_VALUE", nullable = false, length = 200)
    private String ciValue;

    @Column(name = "INCOME_DOC_KEY", length = 500)
    private String incomeDocKey;

    @Column(name = "ASSET_DOC_KEY", length = 500)
    private String assetDocKey;

    @Column(name = "JOB_DOC_KEY", length = 500)
    private String jobDocKey;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status; // PENDING / COMPLETED

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    protected AdditionalReview() {}

    public AdditionalReview(Long creditAppId, String ciValue,
                            String incomeDocKey, String assetDocKey, String jobDocKey) {
        this.creditAppId  = creditAppId;
        this.ciValue      = ciValue;
        this.incomeDocKey = incomeDocKey;
        this.assetDocKey  = assetDocKey;
        this.jobDocKey    = jobDocKey;
        this.status       = "PENDING";
        this.createdAt    = LocalDateTime.now();
    }

    public void complete() {
        this.status      = "COMPLETED";
        this.completedAt = LocalDateTime.now();
    }

    // 서류 재제출 시 docKey 갱신 (PENDING 상태에서만 호출)
    public void updateDocs(String incomeDocKey, String assetDocKey, String jobDocKey) {
        this.incomeDocKey = incomeDocKey;
        this.assetDocKey  = assetDocKey;
        this.jobDocKey    = jobDocKey;
    }

    public Long getAdditionalReviewId() { return additionalReviewId; }
    public Long getCreditAppId()        { return creditAppId; }
    public String getCiValue()          { return ciValue; }
    public String getIncomeDocKey()     { return incomeDocKey; }
    public String getAssetDocKey()      { return assetDocKey; }
    public String getJobDocKey()        { return jobDocKey; }
    public String getStatus()           { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
