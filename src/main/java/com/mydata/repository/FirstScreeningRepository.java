package com.mydata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mydata.domain.FirstScreening;

public interface FirstScreeningRepository extends JpaRepository<FirstScreening, Long> {

    List<FirstScreening> findByCreditAppIdOrderByCreatedAtDesc(Long creditAppId);
}