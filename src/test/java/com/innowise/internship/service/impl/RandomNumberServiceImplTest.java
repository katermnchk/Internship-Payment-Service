package com.innowise.internship.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RandomNumberServiceImplTest {

    private RandomNumberServiceImpl randomNumberService;

    @BeforeEach
    void setUp() {
        RandomNumberServiceImpl realService =
                new RandomNumberServiceImpl("http://localhost:8080", "/api/random");

        randomNumberService = Mockito.spy(realService);
    }

    private static Stream<Arguments> provideRandomNumbers() {
        return Stream.of(
                Arguments.of(Optional.of(2), true),
                Arguments.of(Optional.of(3), false),
                Arguments.of(Optional.empty(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRandomNumbers")
    void givenRandomNumbers_whenCheckIsNumberEven_thenReturnBoolean(Optional<Integer> number, boolean expected) {
        doReturn(number).when(randomNumberService).getRandomNumber();
        boolean isEven = randomNumberService.isNumberEven();
        assertEquals(expected, isEven);
    }
}
