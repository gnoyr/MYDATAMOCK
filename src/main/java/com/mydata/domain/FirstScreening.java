package com.mydata.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MYDATA_FIRST_SCREENING")
public class FirstScreening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FIRST_SCREENING_ID")
    private Long firstScreeningId;

    @Column(name = "CREDIT_APP_ID", nullable = false)
    private Long creditAppId;

    @Column(name = "ANNUAL_INCOME_BAND", nullable = false, length = 20)
    private String annualIncomeBand;

    @Column(name = "CREDIT_SCORE_BAND", nullable = false, length = 20)
    private String creditScoreBand;

    @Column(name = "INCOME_DOC_KEY", length = 500)
    private String incomeDocKey;

    @Column(name = "ASSET_DOC_KEY", length = 500)
    private String assetDocKey;

    @Column(name = "JOB_DOC_KEY", length = 500)
    private String jobDocKey;

    @Column(name = "SCREENING_RESULT", nullable = false, length = 20)
    private String screeningResult;

    @Column(name = "DOC_VERIFIED_YN", nullable = false, length = 1)
    private String docVerifiedYn;

    @Column(name = "APPLICATION_STATUS", nullable = false, length = 20)
    private String applicationStatus;

    @Column(name = "REJECTION_REASON", length = 500)
    private String rejectionReason;

    @Column(name = "REVIEWED_BY", nullable = false, length = 100)
    private String reviewedBy;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "REVIEWED_AT", nullable = false)
    private LocalDateTime reviewedAt;

    protected FirstScreening() {
    }

    public FirstScreening(Long creditAppId,
                          String annualIncomeBand,
                          String creditScoreBand,
                          String incomeDocKey,
                          String assetDocKey,
                          String jobDocKey,
                          String screeningResult,
                          String docVerifiedYn,
                          String applicationStatus,
                          String rejectionReason,
                          String reviewedBy) {
        this.creditAppId = creditAppId;
        this.annualIncomeBand = annualIncomeBand;
        this.creditScoreBand = creditScoreBand;
        this.incomeDocKey = incomeDocKey;
        this.assetDocKey = assetDocKey;
        this.jobDocKey = jobDocKey;
        this.screeningResult = screeningResult;
        this.docVerifiedYn = docVerifiedYn;
        this.applicationStatus = applicationStatus;
        this.rejectionReason = rejectionReason;
        this.reviewedBy = reviewedBy;
        this.createdAt = LocalDateTime.now();
        this.reviewedAt = LocalDateTime.now();
    }

    public Long getFirstScreeningId() {
        return firstScreeningId;
    }

    public Long getCreditAppId() {
        return creditAppId;
    }

    public String getAnnualIncomeBand() {
        return annualIncomeBand;
    }

    public String getCreditScoreBand() {
        return creditScoreBand;
    }

    public String getIncomeDocKey() {
        return incomeDocKey;
    }

    public String getAssetDocKey() {
        return assetDocKey;
    }

    public String getJobDocKey() {
        return jobDocKey;
    }

    public String getScreeningResult() {
        return screeningResult;
    }

    public String getDocVerifiedYn() {
        return docVerifiedYn;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
}