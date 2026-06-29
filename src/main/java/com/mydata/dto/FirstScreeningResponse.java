package com.mydata.dto;

public class FirstScreeningResponse {

    private Long appId;
    private String screeningResult;       // PASS / REJECTED
    private String docVerifiedYn;         // Y / N
    private String applicationStatus;     // APPROVED / REJECTED
    private String rejectionReason;       // 거절 사유 (PASS면 null)
    private String reviewedBy;            // 심사 기관명

    // 신용 프로필 (PASS일 때만 값 있음, REJECTED면 null)
    private Long estimatedMonthlyIncome;  // 월추정소득 (홈택스 계산값)
    private Integer creditScore;          // 신용점수
    private Integer vehicleCount;         // 차량 보유 수
    private Long loanBalance;             // 대출 잔액
    private Double delinquencyRate;       // 연체율
    private Integer multipleDebtCount;    // 다중채무 건수
    private String jobType;               // 직업 유형

    // REJECTED 생성자
    public FirstScreeningResponse(Long appId,
                                  String screeningResult,
                                  String docVerifiedYn,
                                  String applicationStatus,
                                  String rejectionReason,
                                  String reviewedBy) {
        this.appId = appId;
        this.screeningResult = screeningResult;
        this.docVerifiedYn = docVerifiedYn;
        this.applicationStatus = applicationStatus;
        this.rejectionReason = rejectionReason;
        this.reviewedBy = reviewedBy;
    }

    // PASS 생성자
    public FirstScreeningResponse(Long appId,
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

    public Long getAppId() { return appId; }
    public String getScreeningResult() { return screeningResult; }
    public String getDocVerifiedYn() { return docVerifiedYn; }
    public String getApplicationStatus() { return applicationStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public String getReviewedBy() { return reviewedBy; }
    public Long getEstimatedMonthlyIncome() { return estimatedMonthlyIncome; }
    public Integer getCreditScore() { return creditScore; }
    public Integer getVehicleCount() { return vehicleCount; }
    public Long getLoanBalance() { return loanBalance; }
    public Double getDelinquencyRate() { return delinquencyRate; }
    public Integer getMultipleDebtCount() { return multipleDebtCount; }
    public String getJobType() { return jobType; }
}