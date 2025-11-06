package com.innowise.internship.service.impl;

import com.innowise.internship.dto.PaymentRequestDto;
import com.innowise.internship.dto.kafka.OrderCreatedEvent;
import com.innowise.internship.dto.kafka.PaymentCreatedEvent;
import com.innowise.internship.mapper.EventMapper;
import com.innowise.internship.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private KafkaConsumerServiceImpl kafkaConsumerService;

    @Test
    void givenOrderCreatedEvent_whenHandleOrderCreatedEvent_thenCreatePaymentIsCalled() {
        OrderCreatedEvent orderCreatedEvent =
                new OrderCreatedEvent("order34", "user56", new BigDecimal("250.00"));
        PaymentRequestDto expectedDto = new PaymentRequestDto(
                orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getUserId(),
                orderCreatedEvent.getAmount()
        );

        when(eventMapper.toPaymentRequestDto(orderCreatedEvent)).thenReturn(expectedDto);
        ArgumentCaptor<PaymentRequestDto> captor = ArgumentCaptor.forClass(PaymentRequestDto.class);

        kafkaConsumerService.handleOrderCreatedEvent(orderCreatedEvent);
        verify(paymentService).createPayment(captor.capture());
        PaymentRequestDto paymentRequestDto = captor.getValue();

        assertAll(
                () -> assertEquals(orderCreatedEvent.getOrderId(), paymentRequestDto.getOrderId()),
                () -> assertEquals(orderCreatedEvent.getUserId(), paymentRequestDto.getUserId()),
                () -> assertEquals(orderCreatedEvent.getAmount(), paymentRequestDto.getPaymentAmount())
        );
    }
}
