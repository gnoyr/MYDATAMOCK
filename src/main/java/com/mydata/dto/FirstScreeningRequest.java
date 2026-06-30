package com.mydata.dto;

public class FirstScreeningRequest {

    private Long appId;
    private String annualIncomeBand;
    private String creditScoreBand;
    private String incomeDocKey;
    private String assetDocKey;
    private String jobDocKey;
    private String ciValue;
    private Long requestedLimit;

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

	public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAnnualIncomeBand() {
        return annualIncomeBand;
    }

    public void setAnnualIncomeBand(String annualIncomeBand) {
        this.annualIncomeBand = annualIncomeBand;
    }

    public String getCreditScoreBand() {
        return creditScoreBand;
    }

    public void setCreditScoreBand(String creditScoreBand) {
        this.creditScoreBand = creditScoreBand;
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
