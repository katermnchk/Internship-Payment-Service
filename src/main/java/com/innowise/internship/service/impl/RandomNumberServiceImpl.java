package com.innowise.internship.service.impl;

import com.innowise.internship.client.RandomNumberClient;
import com.innowise.internship.service.RandomNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RandomNumberServiceImpl implements RandomNumberService {

    private final RandomNumberClient randomNumberClient;

    @Override
    public boolean isNumberEven() {
        Optional<Integer> randomNumber = randomNumberClient.getRandomNumber();
        return randomNumber.map(integer -> integer % 2 == 0).orElse(false);
    }

}