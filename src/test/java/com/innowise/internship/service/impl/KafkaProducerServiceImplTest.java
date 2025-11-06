package com.innowise.internship.service.impl;

import com.innowise.internship.dto.kafka.PaymentCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceImplTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerServiceImpl kafkaProducerService;

    private final String testTopic = "test-create-payment-topic";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaProducerService, "createPaymentTopic", testTopic);
    }

    @Test
    void givenPaymentEvent_whenSendPaymentEvent_thenKafkaTemplateSendIsCalled() {
        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent("order12", "SUCCESS");
        kafkaProducerService.sendPaymentCreatedEvent(paymentCreatedEvent);
        verify(kafkaTemplate).send(testTopic, paymentCreatedEvent.getOrderId(), paymentCreatedEvent);
    }

    @Test
    void givenKafkaError_whenSendPaymentCreatedEvent_thenLogsError() {
        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent("order12", "FAILED");
        doThrow(new RuntimeException()).when(kafkaTemplate).send(anyString(), anyString(), any());

        kafkaProducerService.sendPaymentCreatedEvent(paymentCreatedEvent);
        verify(kafkaTemplate).send(testTopic, paymentCreatedEvent.getOrderId(), paymentCreatedEvent);
    }

}
