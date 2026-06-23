package com.mydata.dto;

public class ScreeningResultCallbackRequest {

    private Long appId;
    private String screeningResult;
    private String docVerifiedYn;
    private String applicationStatus;
    private String rejectionReason;
    private String reviewedBy;
    private Long estimatedMonthlyIncome;
    private Integer creditScore;
    private Integer vehicleCount;
    private Long loanBalance;
    private Double delinquencyRate;
    private Integer multipleDebtCount;
    private String jobType;

    public ScreeningResultCallbackRequest(Long appId,
                                          String screeningResult,
                                          String docVerifiedYn,
                                          String applicationStatus,
                                          String rejectionReason,
                                          String reviewedBy,
                                          Long estimatedMonthlyIncome,
                                          Integer creditScore,
                                          Integer vehicleCount,
                                          Long loanBalance,
                                          Double delinquencyRate,
                                          Integer multipleDebtCount,
                                          String jobType) {
        this.appId = appId;
        this.screeningResult = screeningResult;
        this.docVerifiedYn = docVerifiedYn;
        this.applicationStatus = applicationStatus;
        this.rejectionReason = rejectionReason;
        this.reviewedBy = reviewedBy;
        this.estimatedMonthlyIncome = estimatedMonthlyIncome;
        this.creditScore = creditScore;
        this.vehicleCount = vehicleCount;
        this.loanBalance = loanBalance;
        this.delinquencyRate = delinquencyRate;
        this.multipleDebtCount = multipleDebtCount;
        this.jobType = jobType;
    }

    public Long getAppId() {
        return appId;
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

    public Long getEstimatedMonthlyIncome() {
        return estimatedMonthlyIncome;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public Integer getVehicleCount() {
        return vehicleCount;
    }

    public Long getLoanBalance() {
        return loanBalance;
    }

    public Double getDelinquencyRate() {
        return delinquencyRate;
    }

    public Integer getMultipleDebtCount() {
        return multipleDebtCount;
    }

    public String getJobType() {
        return jobType;
    }
}