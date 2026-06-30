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

    Optional<IdentityMaster> findByCiValueAndStatus(String ciValue, String status);

    // 테스트 신원 재시드 멱등성 확보용 (시드 전용 테이블)
    void deleteByIdResidentNo(String idResidentNo);
}
