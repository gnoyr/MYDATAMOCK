package com.mydata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mydata.service.AdditionalScreeningService;

@RestController
@RequestMapping("/review/request")
public class AdditionalScreeningController {

    private final AdditionalScreeningService additionalScreeningService;

    public AdditionalScreeningController(AdditionalScreeningService additionalScreeningService) {
        this.additionalScreeningService = additionalScreeningService;
    }

    // 추가 심사
    @PostMapping("/{creditAppId}")
    public ResponseEntity<Void> requestAdditionalReview(
            @PathVariable Long creditAppId) {
        additionalScreeningService.screen(creditAppId);
        return ResponseEntity.ok().build();  // 결과는 콜백으로 전달하니까 Void
    }
}