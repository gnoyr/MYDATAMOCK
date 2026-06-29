package com.mydata.repository;

import com.mydata.domain.AdditionalReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdditionalReviewRepository extends JpaRepository<AdditionalReview, Long> {
    List<AdditionalReview> findByStatusOrderByCreatedAtAsc(String status);
    Optional<AdditionalReview> findTopByCreditAppIdOrderByAdditionalReviewIdDesc(Long creditAppId);
}
