package com.mydata.dto;

public class AdditionalReviewResultCallbackRequest {

    private Long appId;
    private String applicationStatus;
    private Long approvedLimit;
    private String rejectionReason;
    private String reviewedBy;

    public AdditionalReviewResultCallbackRequest(Long appId,
                                                 String applicationStatus,
                                                 Long approvedLimit,
                                                 String rejectionReason,
                                                 String reviewedBy) {
        this.appId = appId;
        this.applicationStatus = applicationStatus;
        this.approvedLimit = approvedLimit;
        this.rejectionReason = rejectionReason;
        this.reviewedBy = reviewedBy;
    }

    public Long getAppId() {
        return appId;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public Long getApprovedLimit() {
        return approvedLimit;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }
}