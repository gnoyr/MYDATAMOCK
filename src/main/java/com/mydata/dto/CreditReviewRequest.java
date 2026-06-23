package com.mydata.dto;

public class CreditReviewRequest {

    private Long creditAppId;
    private String ciValue;
    private Long requestedLimit;
    private String creditScoreBand;
    private String annualIncomeBand;
    private String incomeDocKey;
    private String assetDocKey;
    private String jobDocKey;

    public Long getCreditAppId() {
        return creditAppId;
    }

    public void setCreditAppId(Long creditAppId) {
        this.creditAppId = creditAppId;
    }

    public String getCiValue() {
        return ciValue;
    }

    public void setCiValue(String ciValue) {
        this.ciValue = ciValue;
    }

    public Long getRequestedLimit() {
        return requestedLimit;
    }

    public void setRequestedLimit(Long requestedLimit) {
        this.requestedLimit = requestedLimit;
    }

    public String getCreditScoreBand() {
        return creditScoreBand;
    }

    public void setCreditScoreBand(String creditScoreBand) {
        this.creditScoreBand = creditScoreBand;
    }

    public String getAnnualIncomeBand() {
        return annualIncomeBand;
    }

    public void setAnnualIncomeBand(String annualIncomeBand) {
        this.annualIncomeBand = annualIncomeBand;
    }

    public String getIncomeDocKey() {
        return incomeDocKey;
    }

    public void setIncomeDocKey(String incomeDocKey) {
        this.incomeDocKey = incomeDocKey;
    }

    public String getAssetDocKey() {
        return assetDocKey;
    }

    public void setAssetDocKey(String assetDocKey) {
        this.assetDocKey = assetDocKey;
    }

    public String getJobDocKey() {
        return jobDocKey;
    }

    public void setJobDocKey(String jobDocKey) {
        this.jobDocKey = jobDocKey;
    }
}