package com.mydata.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "HOMETAX_INCOME")
public class HometaxIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HOMETAX_INCOME")
    @SequenceGenerator(name = "SEQ_HOMETAX_INCOME", sequenceName = "SEQ_HOMETAX_INCOME", allocationSize = 1)
    @Column(name = "HOMETAX_INCOME_ID")
    private Long hometaxIncomeId;

    @Column(name = "CREDIT_APP_ID", nullable = false)
    private Long creditAppId;

    @Column(name = "CI_VALUE", nullable = false)
    private String ciValue;

    @Column(name = "TAX_YEAR", nullable = false)
    private Integer taxYear;

    @Column(name = "INCOME_TYPE", nullable = false)
    private String incomeType;

    @Column(name = "ANNUAL_INCOME", nullable = false)
    private Long annualIncome;

    @Column(name = "QUERY_STATUS", nullable = false)
    private String queryStatus;

    @Column(name = "FAIL_REASON")
    private String failReason;

    @Column(name = "QUERIED_AT", nullable = false)
    private java.time.LocalDateTime queriedAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private java.time.LocalDateTime updatedAt;

    // 생성자 (INSERT용)
    public HometaxIncome(Long creditAppId, String ciValue, Integer taxYear,
                         String incomeType, Long annualIncome, String queryStatus,
                         String failReason) {
        this.creditAppId  = creditAppId;
        this.ciValue      = ciValue;
        this.taxYear      = taxYear;
        this.incomeType   = incomeType;
        this.annualIncome = annualIncome;
        this.queryStatus  = queryStatus;
        this.failReason   = failReason;
        this.queriedAt    = java.time.LocalDateTime.now();
        this.createdAt    = java.time.LocalDateTime.now();
    }

    protected HometaxIncome() {}

    public Long getHometaxIncomeId() { return hometaxIncomeId; }
    public Long getCreditAppId()     { return creditAppId; }
    public String getCiValue()       { return ciValue; }
    public Integer getTaxYear()      { return taxYear; }
    public String getIncomeType()    { return incomeType; }
    public Long getAnnualIncome()    { return annualIncome; }
    public String getQueryStatus()   { return queryStatus; }
    public String getFailReason()    { return failReason; }
}