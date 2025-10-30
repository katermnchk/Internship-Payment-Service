package com.innowise.internship.service.impl;

import com.innowise.internship.service.RandomNumberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
public class RandomNumberServiceImpl implements RandomNumberService {

    private final WebClient webClient = WebClient.create();
    private final String apiGatewayUrl;
    private final String randomNumberPath;

    public RandomNumberServiceImpl (
            @Value("${services.api-gateway.url}") String apiGatewayUrl,
            @Value("${services.random-number-api.path}" ) String randomNumberPath
    ) {
        this.apiGatewayUrl = apiGatewayUrl;
        this.randomNumberPath = randomNumberPath;
    }

    @Override
    public Optional<Integer> getRandomNumber() {

        String fullUrl = apiGatewayUrl + randomNumberPath;

        try {
            Mono<Integer> response = webClient.get().uri(fullUrl).retrieve().bodyToMono(Integer.class);
            Integer number = response.block(Duration.ofSeconds(10));

            return Optional.ofNullable(number);

        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isNumberEven() {

        Optional<Integer> randomNumber = getRandomNumber();

        return randomNumber.map(integer -> integer % 2 == 0).orElse(false);
    }

}