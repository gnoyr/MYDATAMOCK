package com.mydata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mydata.domain.FirstScreening;

public interface FirstScreeningRepository extends JpaRepository<FirstScreening, Long> {

	Optional<FirstScreening> findTopByCreditAppIdOrderByFirstScreeningIdDesc(Long creditAppId);
    List<FirstScreening> findByCreditAppIdOrderByCreatedAtDesc(Long creditAppId);
}