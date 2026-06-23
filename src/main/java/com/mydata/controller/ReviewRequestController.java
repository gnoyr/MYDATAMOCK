package com.mydata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mydata.dto.AdditionalReviewResultCallbackRequest;
import com.mydata.dto.CreditReviewRequest;
import com.mydata.dto.ScreeningResultCallbackRequest;
import com.mydata.service.ReviewRequestService;

@RestController
@RequestMapping("/review/request")
public class ReviewRequestController {

    private final ReviewRequestService reviewRequestService;

    public ReviewRequestController(ReviewRequestService reviewRequestService) {
        this.reviewRequestService = reviewRequestService;
    }

    @PostMapping("/credit/{creditAppId}")
    public ResponseEntity<ScreeningResultCallbackRequest> requestCreditReview(
            @PathVariable(name = "creditAppId") Long creditAppId,
            @RequestBody CreditReviewRequest request
    ) {
        ScreeningResultCallbackRequest response =
                reviewRequestService.requestCreditReview(creditAppId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{creditAppId}")
    public ResponseEntity<AdditionalReviewResultCallbackRequest> requestAdditionalReview(
            @PathVariable(name = "creditAppId") Long creditAppId
    ) {
        AdditionalReviewResultCallbackRequest response =
                reviewRequestService.requestAdditionalReview(creditAppId);

        return ResponseEntity.ok(response);
    }
}