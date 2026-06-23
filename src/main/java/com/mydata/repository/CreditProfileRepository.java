package com.mydata.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mydata.domain.CreditProfile;

public interface CreditProfileRepository extends JpaRepository<CreditProfile, Long> {

    Optional<CreditProfile> findTopByCreditAppIdOrderByCreditProfileIdDesc(Long creditAppId);
}