package com.innowise.internship.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
public class RandomNumberClient {

    private final WebClient webClient;
    private final String apiGatewayUrl;
    private final String randomNumberPath;

    public RandomNumberClient (
            @Value("${services.api-gateway.url}") String apiGatewayUrl,
            @Value("${services.random-number-api.path}" ) String randomNumberPath
    ) {
        this.apiGatewayUrl = apiGatewayUrl;
        this.randomNumberPath = randomNumberPath;
        this.webClient = WebClient.builder().baseUrl(apiGatewayUrl).build();
    }

    public Optional<Integer> getRandomNumber() {

        String fullUrl = apiGatewayUrl + randomNumberPath;

        try {

            String responseBody = webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (responseBody != null && !responseBody.trim().isEmpty()) {
                Integer number = Integer.parseInt(responseBody.trim());
                return Optional.of(number);
            } else {
                return Optional.empty();
            }

        } catch (Exception ex) {
            log.error("Failed to get random number", ex);
            return Optional.empty();
        }
    }
}
