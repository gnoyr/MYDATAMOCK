package com.mydata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mydata.dto.FirstScreeningRequest;
import com.mydata.dto.FirstScreeningResponse;
import com.mydata.service.FirstScreeningService;

@RestController
@RequestMapping("/api/mydata")
public class FirstScreeningController {

    private final FirstScreeningService firstScreeningService;

    public FirstScreeningController(FirstScreeningService firstScreeningService) {
        this.firstScreeningService = firstScreeningService;
    }

    @PostMapping("/first-screening")
    public ResponseEntity<FirstScreeningResponse> firstScreening(
            @RequestBody FirstScreeningRequest request
    ) {
        FirstScreeningResponse response = firstScreeningService.screen(request);
        return ResponseEntity.ok(response);
    }
}