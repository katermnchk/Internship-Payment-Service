package com.innowise.internship.service.impl;

import com.innowise.internship.client.RandomNumberClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomNumberServiceImplTest {

    @Mock
    private RandomNumberClient randomNumberClient;

    @InjectMocks
    private RandomNumberServiceImpl randomNumberService;

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
        when(randomNumberClient.getRandomNumber()).thenReturn(number);
        boolean isEven = randomNumberService.isNumberEven();
        assertEquals(expected, isEven);
    }
}
