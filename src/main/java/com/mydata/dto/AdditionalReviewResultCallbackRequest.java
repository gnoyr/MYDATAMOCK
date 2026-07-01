package com.mydata.dto;

public class AdditionalReviewResultCallbackRequest {

    private Long    appId;
    private String  applicationStatus; // APPROVED / REJECTED
    private String  rejectionReason;
    private String  reviewedBy;

    // 스코어링용 — BNKcard checkLimitAdditional() 에서 사용
    private Long    estimatedMonthlyIncome;
    private Integer creditScore;
    private Integer vehicleCount;
    private Long    loanBalance;
    private Double  delinquencyRate;
    private Integer multipleDebtCount;
    private String  jobType;
    private Long    monthlyPayment;

    public AdditionalReviewResultCallbackRequest() {}

    // 거절용 생성자
    public AdditionalReviewResultCallbackRequest(Long appId, String rejectionReason, String reviewedBy) {
        this.appId             = appId;
        this.applicationStatus = "REJECTED";
        this.rejectionReason   = rejectionReason;
        this.reviewedBy        = reviewedBy;
    }

    // 승인 심사용 생성자 (BNKcard가 한도 산정)
    public AdditionalReviewResultCallbackRequest(Long appId, String reviewedBy,
                                                 Long estimatedMonthlyIncome,
                                                 Integer creditScore, Integer vehicleCount,
                                                 Long loanBalance, Double delinquencyRate,
                                                 Integer multipleDebtCount, String jobType,
                                                 Long monthlyPayment) {
        this.appId                  = appId;
        this.applicationStatus      = "PENDING_LIMIT";  // BNKcard가 한도 판정
        this.reviewedBy             = reviewedBy;
        this.estimatedMonthlyIncome = estimatedMonthlyIncome;
        this.creditScore            = creditScore;
        this.vehicleCount           = vehicleCount;
        this.loanBalance            = loanBalance;
        this.delinquencyRate        = delinquencyRate;
        this.multipleDebtCount      = multipleDebtCount;
        this.jobType                = jobType;
        this.monthlyPayment         = monthlyPayment;
    }

    public Long    getAppId()                  { return appId; }
    public String  getApplicationStatus()      { return applicationStatus; }
    public String  getRejectionReason()        { return rejectionReason; }
    public String  getReviewedBy()             { return reviewedBy; }
    public Long    getEstimatedMonthlyIncome() { return estimatedMonthlyIncome; }
    public Integer getCreditScore()            { return creditScore; }
    public Integer getVehicleCount()           { return vehicleCount; }
    public Long    getLoanBalance()            { return loanBalance; }
    public Double  getDelinquencyRate()        { return delinquencyRate; }
    public Integer getMultipleDebtCount()      { return multipleDebtCount; }
    public String  getJobType()                { return jobType; }
    public Long    getMonthlyPayment()         { return monthlyPayment; }
}
