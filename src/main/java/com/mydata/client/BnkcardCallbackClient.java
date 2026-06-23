package com.mydata.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mydata.dto.AdditionalReviewResultCallbackRequest;
import com.mydata.dto.ScreeningResultCallbackRequest;

@Component
public class BnkcardCallbackClient {

    private final RestTemplate restTemplate;
    private final String bnkcardBaseUrl;

    public BnkcardCallbackClient(RestTemplate restTemplate,
                                 @Value("${bnkcard.api.base-url}") String bnkcardBaseUrl) {
        this.restTemplate = restTemplate;
        this.bnkcardBaseUrl = bnkcardBaseUrl;
    }

    public void sendScreeningResult(ScreeningResultCallbackRequest request) {
        restTemplate.postForEntity(
                bnkcardBaseUrl + "/screening-result",
                request,
                Void.class
        );
    }

    public void sendAdditionalReviewResult(AdditionalReviewResultCallbackRequest request) {
        restTemplate.postForEntity(
                bnkcardBaseUrl + "/review-result",
                request,
                Void.class
        );
    }
}