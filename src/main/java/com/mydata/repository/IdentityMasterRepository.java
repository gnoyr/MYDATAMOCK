package com.mydata.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mydata.domain.IdentityMaster;

public interface IdentityMasterRepository extends JpaRepository<IdentityMaster, Long> {

    Optional<IdentityMaster> findFirstByCiValueAndIdTypeAndIdIssueDateAndStatus(
            String ciValue,
            String idType,
            String idIssueDate,
            String status
    );
}
