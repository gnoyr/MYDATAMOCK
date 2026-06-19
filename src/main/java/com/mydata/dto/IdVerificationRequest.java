package com.mydata.dto;

public class IdVerificationRequest {

    private Long creditAppId;
    private String idType;
    private String idName;

    private String idResidentNo;

    private String idAddress;
    private String idIssueDate;

    public Long getCreditAppId() {
        return creditAppId;
    }

    public void setCreditAppId(Long creditAppId) {
        this.creditAppId = creditAppId;
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

    public String getIdIssueDate() {
        return idIssueDate;
    }

    public void setIdIssueDate(String idIssueDate) {
        this.idIssueDate = idIssueDate;
    }
}