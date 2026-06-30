package com.mydata.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mydata.domain.CreditProfile;

public interface CreditProfileRepository extends JpaRepository<CreditProfile, Long> {

	 Optional<CreditProfile> findByCiValue(String ciValue);
	 
	 Optional<CreditProfile> findTopByCreditAppIdOrderByCreditProfileIdDesc(Long creditAppId);

    @Modifying
    @Query("UPDATE CreditProfile c SET c.estimatedIncome = :estimatedIncome WHERE c.ciValue = :ciValue")
    void updateEstimatedIncome(@Param("ciValue") String ciValue,
                               @Param("estimatedIncome") Long estimatedIncome);

    // 시드 더미(credit_app_id=0) 정리용 — 재시드 멱등성 확보
    void deleteByCreditAppId(Long creditAppId);
}