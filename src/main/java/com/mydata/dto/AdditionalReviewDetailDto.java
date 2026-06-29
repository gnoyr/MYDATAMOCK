package com.mydata.dto;

import java.time.LocalDateTime;

public class AdditionalReviewDetailDto {

    private Long   creditAppId;
    private String incomeDocKey;
    private String assetDocKey;
    private String jobDocKey;
    private LocalDateTime createdAt;

    // CreditProfile 데이터 (읽기 전용 참고용)
    private Integer creditScore;
    private Long    loanBalance;
    private Double  delinquencyRate;
    private Integer multiDebtCount;
    private String  jobType;
    private Integer carCount;

    public AdditionalReviewDetailDto(Long creditAppId, String incomeDocKey, String assetDocKey,
                                     String jobDocKey, LocalDateTime createdAt,
                                     Integer creditScore, Long loanBalance, Double delinquencyRate,
                                     Integer multiDebtCount, String jobType, Integer carCount) {
        this.creditAppId     = creditAppId;
        this.incomeDocKey    = incomeDocKey;
        this.assetDocKey     = assetDocKey;
        this.jobDocKey       = jobDocKey;
        this.createdAt       = createdAt;
        this.creditScore     = creditScore;
        this.loanBalance     = loanBalance;
        this.delinquencyRate = delinquencyRate;
        this.multiDebtCount  = multiDebtCount;
        this.jobType         = jobType;
        this.carCount        = carCount;
    }

    public Long   getCreditAppId()     { return creditAppId; }
    public String getIncomeDocKey()    { return incomeDocKey; }
    public String getAssetDocKey()     { return assetDocKey; }
    public String getJobDocKey()       { return jobDocKey; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public Integer getCreditScore()    { return creditScore; }
    public Long    getLoanBalance()    { return loanBalance; }
    public Double  getDelinquencyRate(){ return delinquencyRate; }
    public Integer getMultiDebtCount() { return multiDebtCount; }
    public String  getJobType()        { return jobType; }
    public Integer getCarCount()       { return carCount; }
}
