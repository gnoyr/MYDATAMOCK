package com.mydata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mydata.domain.IdVerification;

public interface IdVerificationRepository extends JpaRepository<IdVerification, Long> {

    List<IdVerification> findByCreditAppId(Long creditAppId);
}
