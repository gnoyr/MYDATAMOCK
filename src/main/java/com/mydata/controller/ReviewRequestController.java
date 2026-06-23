package com.mydata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mydata.dto.CreditReviewRequest;
import com.mydata.service.ReviewRequestService;

@RestController
@RequestMapping("/review/request")
public class ReviewRequestController {

    private final ReviewRequestService reviewRequestService;

    public ReviewRequestController(ReviewRequestService reviewRequestService) {
        this.reviewRequestService = reviewRequestService;
    }

    @PostMapping("/credit/{creditAppId}")
    public ResponseEntity<Void> requestCreditReview(
            @PathVariable(name = "creditAppId") Long creditAppId,
            @RequestBody CreditReviewRequest request
    ) {
        reviewRequestService.requestCreditReview(creditAppId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{creditAppId}")
    public ResponseEntity<Void> requestAdditionalReview(
            @PathVariable(name = "creditAppId") Long creditAppId
    ) {
        reviewRequestService.requestAdditionalReview(creditAppId);
        return ResponseEntity.ok().build();
    }
}