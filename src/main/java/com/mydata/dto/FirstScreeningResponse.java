package com.mydata.dto;

public class FirstScreeningResponse {

    private Long appId;
    private String screeningResult;
    private String docVerifiedYn;
    private String applicationStatus;
    private String rejectionReason;
    private String reviewedBy;

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
}