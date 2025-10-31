package com.innowise.internship.service.impl;

import com.innowise.internship.dto.kafka.PaymentCreatedEvent;
import com.innowise.internship.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Value("${app.kafka.topic.create-payment}")
    private String createPaymentTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendPaymentCreatedEvent(PaymentCreatedEvent paymentCreatedEvent) {
        try {
            kafkaTemplate.send(createPaymentTopic, paymentCreatedEvent.getOrderId(),  paymentCreatedEvent);
            log.info("Sent PaymentCreatedEvent created for orderId: {}", paymentCreatedEvent.getOrderId());
        } catch (Exception e) {
            log.error("Error sending PaymentCreatedEvent for orderId: {}", paymentCreatedEvent.getOrderId(), e);
        }
    }
}
