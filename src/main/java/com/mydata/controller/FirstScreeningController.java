package com.mydata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mydata.dto.FirstScreeningRequest;
import com.mydata.dto.FirstScreeningResponse;
import com.mydata.service.FirstScreeningService;
@RestController
@RequestMapping("/review/request")
public class FirstScreeningController {

    private final FirstScreeningService firstScreeningService;

    public FirstScreeningController(FirstScreeningService firstScreeningService) {
        this.firstScreeningService = firstScreeningService;
    }

    // 신용카드 1차 심사
    @PostMapping("/credit/{creditAppId}")
    public ResponseEntity<FirstScreeningResponse> creditScreening(
            @PathVariable Long creditAppId,
            @RequestBody FirstScreeningRequest request) {
        request.setAppId(creditAppId);
        FirstScreeningResponse result = firstScreeningService.screen(request);
        return ResponseEntity.ok(result);
    }

    // 체크카드 심사
    @PostMapping("/check/{checkAppId}")
    public ResponseEntity<FirstScreeningResponse> checkScreening(
            @PathVariable Long checkAppId,
            @RequestBody FirstScreeningRequest request) {
        request.setAppId(checkAppId);
        FirstScreeningResponse result = firstScreeningService.screenCheck(request);
        return ResponseEntity.ok(result);
    }
}