package com.mydata.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MYDATA_IDENTITY_MASTER")
public class IdentityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDENTITY_ID")
    private Long identityId;

    @Column(name = "ID_TYPE", nullable = false, length = 30)
    private String idType;

    @Column(name = "ID_NAME", nullable = false, length = 100)
    private String idName;

    @Column(name = "ID_RESIDENT_NO", nullable = false, length = 7)
    private String idResidentNo;

    @Column(name = "ID_ADDRESS", nullable = false, length = 500)
    private String idAddress;

    @Column(name = "ID_ISSUE_DATE", nullable = false, length = 8)
    private String idIssueDate;

    @Column(name = "CI_VALUE", nullable = false, length = 200)
    private String ciValue;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    protected IdentityMaster() {
    }

    public Long getIdentityId() {
        return identityId;
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

    public String getCiValue() {
        return ciValue;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
