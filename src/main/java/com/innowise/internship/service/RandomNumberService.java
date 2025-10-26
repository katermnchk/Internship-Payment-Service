package com.innowise.internship.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RandomNumberService {

    private final WebClient webClient = WebClient.create();
    private final String apiGatewayUrl;
    private final String randomNumberPath;

    public RandomNumberService (
            @Value("${services.api-gateway.url}") String apiGatewayUrl,
            @Value("${services.random-number-api.path}" ) String randomNumberPath
    ) {
        this.apiGatewayUrl = apiGatewayUrl;
        this.randomNumberPath = randomNumberPath;
    }

    public boolean isNumberEven() {
        String fullUrl = apiGatewayUrl + randomNumberPath;

        try {
            Mono<Integer> response = webClient.get().uri(fullUrl).retrieve().bodyToMono(Integer.class);
            Integer number = response.block(Duration.ofSeconds(10));

            if (number == null) {
                return false;
            }

            return number % 2 == 0;
        } catch (Exception ex) {
            return false;
        }
    }



}
