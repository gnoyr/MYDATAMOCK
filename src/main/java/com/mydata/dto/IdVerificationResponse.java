package com.mydata.dto;

public class IdVerificationResponse {

    private Long creditAppId;
    private String idVerifiedYn;

    public IdVerificationResponse(Long creditAppId, String idVerifiedYn) {
        this.creditAppId = creditAppId;
        this.idVerifiedYn = idVerifiedYn;
    }

    public Long getCreditAppId() {
        return creditAppId;
    }

    public String getIdVerifiedYn() {
        return idVerifiedYn;
    }
}