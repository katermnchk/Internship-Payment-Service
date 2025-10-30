package com.innowise.internship.service;

import java.util.Optional;

public interface RandomNumberService {

    Optional<Integer> getRandomNumber();

    boolean isNumberEven();
}
