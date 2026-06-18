package com.mydata.test;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mydata")
public class Controller {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "MYDATA_TEST_API",
                "message", "MyData API 통신 성공",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @PostMapping("/id-verification")
    public ResponseEntity<Map<String, Object>> verifyIdentity(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
                "creditAppId", request.get("creditAppId"),
                "idVerifiedYn", "Y"
        ));
    }

    @PostMapping("/primary-screening")
    public ResponseEntity<Map<String, Object>> primaryScreening(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
                "appId", request.get("creditAppId"),
                "screeningResult", "PASS",
                "docVerifiedYn", "Y",
                "applicationStatus", "APPROVED",
                "rejectionReason", null,
                "reviewedBy", "BNK심사센터"
        ));
    }

    @PostMapping("/additional-screening")
    public ResponseEntity<Map<String, Object>> additionalScreening(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
                "appId", request.get("creditAppId"),
                "applicationStatus", "APPROVED",
                "approvedLimit", 2000000,
                "rejectionReason", null,
                "reviewedBy", "BNK심사센터"
        ));
    }
}
