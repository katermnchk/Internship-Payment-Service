package com.innowise.internship.service.impl;

import com.innowise.internship.dto.kafka.PaymentCreatedEvent;
import com.innowise.internship.mapper.PaymentMapper;
import com.innowise.internship.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Value("${app.kafka.topic.create-payment}")
    private String createPaymentTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendPaymentCreatedEvent(PaymentCreatedEvent paymentCreatedEvent) {
        kafkaTemplate.send(createPaymentTopic, paymentCreatedEvent.getOrderId(),  paymentCreatedEvent);
    }
}
