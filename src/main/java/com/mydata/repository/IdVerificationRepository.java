package com.mydata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mydata.domain.IdVerification;

public interface IdVerificationRepository extends JpaRepository<IdVerification, Long> {

	List<IdVerification> findByAppId(Long appId);

	Optional<IdVerification> findTopByAppIdOrderByCreatedAtDesc(Long appId);
}
