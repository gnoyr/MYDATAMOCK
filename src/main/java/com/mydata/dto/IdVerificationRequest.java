package com.mydata.dto;

public class IdVerificationRequest {

    private Long appId;
    private String idType;
    private String idName;

    private String idResidentNo;

    private String idAddress;
    private String idPhone;
    private String idIssueDate;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getIdResidentNo() {
        return idResidentNo;
    }

    public void setIdResidentNo(String idResidentNo) {
        this.idResidentNo = idResidentNo;
    }

    public String getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(String idAddress) {
        this.idAddress = idAddress;
    }

    public String getIdPhone() {
        return idPhone;
    }

    public void setIdPhone(String idPhone) {
        this.idPhone = idPhone;
    }

    public String getIdIssueDate() {
        return idIssueDate;
    }

    public void setIdIssueDate(String idIssueDate) {
        this.idIssueDate = idIssueDate;
    }
}