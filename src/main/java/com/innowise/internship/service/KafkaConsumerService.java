package com.innowise.internship.service;

import com.innowise.internship.dto.kafka.OrderCreatedEvent;

public interface KafkaConsumerService {

    void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent);

}
