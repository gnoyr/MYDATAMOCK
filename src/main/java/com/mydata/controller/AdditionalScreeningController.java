package com.mydata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mydata.service.AdditionalScreeningService;

import java.util.Map;

@RestController
@RequestMapping("/review/request")
public class AdditionalScreeningController {

    private final AdditionalScreeningService additionalScreeningService;

    public AdditionalScreeningController(AdditionalScreeningService additionalScreeningService) {
        this.additionalScreeningService = additionalScreeningService;
    }

    // 추가 심사 요청
    @PostMapping("/{creditAppId}")
    public ResponseEntity<Void> requestAdditionalReview(
            @PathVariable Long creditAppId,
            @RequestBody(required = false) Map<String, Object> body) {
        String ciValue = body != null ? (String) body.get("ciValue") : null;
        additionalScreeningService.screen(creditAppId, ciValue);
        return ResponseEntity.ok().build();
    }

    // 서류 재제출 — PENDING 상태 AdditionalReview의 docKey 갱신
    @PutMapping("/{creditAppId}/docs")
    public ResponseEntity<Void> resubmitDocs(
            @PathVariable Long creditAppId,
            @RequestBody Map<String, String> body) {
        additionalScreeningService.updateDocs(
                creditAppId,
                body.get("incomeDocKey"),
                body.get("assetDocKey"),
                body.get("jobDocKey")
        );
        return ResponseEntity.ok().build();
    }
}