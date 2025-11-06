package com.innowise.internship.service;

import com.innowise.internship.dto.kafka.PaymentCreatedEvent;

public interface KafkaProducerService {

    void sendPaymentCreatedEvent(PaymentCreatedEvent paymentCreatedEvent);

}
