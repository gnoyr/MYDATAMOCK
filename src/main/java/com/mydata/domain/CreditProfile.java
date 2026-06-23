package com.mydata.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "MYDATA_CREDIT_PROFILE")
public class CreditProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CREDIT_PROFILE_ID")
    private Long creditProfileId;

    @Column(name = "CREDIT_APP_ID", nullable = false)
    private Long creditAppId;

    @Column(name = "CREDIT_SCORE", nullable = false)
    private Integer creditScore;

    @Column(name = "ESTIMATED_INCOME", nullable = false)
    private Long estimatedIncome;

    @Column(name = "CAR_COUNT", nullable = false)
    private Integer carCount;

    @Column(name = "LOAN_BALANCE", nullable = false)
    private Long loanBalance;

    @Column(name = "DELINQUENCY_RATE", nullable = false)
    private Double delinquencyRate;

    @Column(name = "MULTI_DEBT_COUNT", nullable = false)
    private Integer multiDebtCount;

    @Column(name = "JOB_TYPE", nullable = false, length = 50)
    private String jobType;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    protected CreditProfile() {
    }

    public CreditProfile(Long creditAppId,
                         Integer creditScore,
                         Long estimatedIncome,
                         Integer carCount,
                         Long loanBalance,
                         Double delinquencyRate,
                         Integer multiDebtCount,
                         String jobType) {
        this.creditAppId = creditAppId;
        this.creditScore = creditScore;
        this.estimatedIncome = estimatedIncome;
        this.carCount = carCount;
        this.loanBalance = loanBalance;
        this.delinquencyRate = delinquencyRate;
        this.multiDebtCount = multiDebtCount;
        this.jobType = jobType;
        this.createdAt = LocalDateTime.now();
    }

    public Long getCreditProfileId() {
        return creditProfileId;
    }

    public Long getCreditAppId() {
        return creditAppId;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public Long getEstimatedIncome() {
        return estimatedIncome;
    }

    public Integer getCarCount() {
        return carCount;
    }

    public Long getLoanBalance() {
        return loanBalance;
    }

    public Double getDelinquencyRate() {
        return delinquencyRate;
    }

    public Integer getMultiDebtCount() {
        return multiDebtCount;
    }

    public String getJobType() {
        return jobType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}