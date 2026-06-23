package com.mydata.dto;

public class IdVerificationResponse {

    private String idVerifiedYn;
    private String ciValue;

    public IdVerificationResponse(String idVerifiedYn, String ciValue) {
        this.idVerifiedYn = idVerifiedYn;
        this.ciValue = ciValue;
    }

    public String getIdVerifiedYn() {
        return idVerifiedYn;
    }

    public String getCiValue() {
        return ciValue;
    }
    
    
    private String extractResidentKey(String value) {
        if (value == null) {
            return null;
        }

        String digits = value.replaceAll("[^0-9]", "");

        if (digits.length() == 13) {
            return digits.substring(0, 7);
        }

        if (digits.length() == 7) {
            return digits;
        }

        throw new IllegalArgumentException("주민등록번호 정보는 7자리 또는 13자리 형식이어야 합니다.");
    }

    private String normalizeIssueDate(String value) {
        return value == null ? null : value.replaceAll("[^0-9]", "");
    }
}

