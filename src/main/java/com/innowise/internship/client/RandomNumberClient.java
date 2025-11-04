package com.innowise.internship.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
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

            Integer number = webClient.get()
                    .uri(randomNumberPath)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return Optional.ofNullable(number);

        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
