package com.mydata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mydata.dto.IdVerificationRequest;
import com.mydata.dto.IdVerificationResponse;
import com.mydata.service.IdVerificationService;

@RestController
@RequestMapping("/api/mydata")
public class IdVerificationController {

    private final IdVerificationService idVerificationService;

    public IdVerificationController(IdVerificationService idVerificationService) {
        this.idVerificationService = idVerificationService;
    }

    @PostMapping("/id-verification")
    public ResponseEntity<IdVerificationResponse> verifyIdentity(
            @RequestBody IdVerificationRequest request
    ) {
        IdVerificationResponse response = idVerificationService.verifyIdentity(request);
        return ResponseEntity.ok(response);
    }
}
