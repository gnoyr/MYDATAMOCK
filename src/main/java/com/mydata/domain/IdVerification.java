package com.mydata.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MYDATA_ID_VERIFICATIONS")
public class IdVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VERIFICATION_ID")
    private Long idVerificationId;

    @Column(name = "APP_ID", nullable = false)
    private Long appId;

    @Column(name = "ID_TYPE", nullable = false, length = 30)
    private String idType;

    @Column(name = "ID_NAME", nullable = false, length = 100)
    private String idName;

    // 주민번호 앞 6자리 + 성별코드 1자리 = 총 7자리
    @Column(name = "ID_RESIDENT_NO", nullable = false, length = 7)
    private String idResidentNo;

    @Column(name = "ID_ADDRESS", nullable = false, length = 500)
    private String idAddress;

    @Column(name = "ID_ISSUE_DATE", nullable = false, length = 8)
    private String idIssueDate;

    @Column(name = "GENERATED_CI_VALUE", length = 200)
    private String generatedCiValue;

    @Column(name = "MATCHED_IDENTITY_ID")
    private Long matchedIdentityId;

    @Column(name = "ID_VERIFIED_YN", nullable = false, length = 1)
    private String idVerifiedYn;

    @Column(name = "FAIL_REASON", length = 500)
    private String failReason;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    protected IdVerification() {
    }

    public IdVerification(Long appId,
                          String idType,
                          String idName,
                          String idResidentNo,
                          String idAddress,
                          String idIssueDate,
                          String generatedCiValue,
                          Long matchedIdentityId,
                          String idVerifiedYn,
                          String failReason) {
        this.appId = appId;
        this.idType = idType;
        this.idName = idName;
        this.idResidentNo = idResidentNo;
        this.idAddress = idAddress;
        this.idIssueDate = idIssueDate;
        this.generatedCiValue = generatedCiValue;
        this.matchedIdentityId = matchedIdentityId;
        this.idVerifiedYn = idVerifiedYn;
        this.failReason = failReason;
        this.createdAt = LocalDateTime.now();
    }

    public Long getIdVerificationId() {
        return idVerificationId;
    }

    public Long getAppId() {
        return appId;
    }

    public String getIdType() {
        return idType;
    }

    public String getIdName() {
        return idName;
    }

    public String getIdResidentNo() {
        return idResidentNo;
    }

    public String getIdAddress() {
        return idAddress;
    }

    public String getIdIssueDate() {
        return idIssueDate;
    }

    public String getGeneratedCiValue() {
        return generatedCiValue;
    }

    public Long getMatchedIdentityId() {
        return matchedIdentityId;
    }

    public String getIdVerifiedYn() {
        return idVerifiedYn;
    }

    public String getFailReason() {
        return failReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}