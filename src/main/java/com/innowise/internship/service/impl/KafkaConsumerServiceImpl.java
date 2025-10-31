package com.innowise.internship.service.impl;

import com.innowise.internship.dto.PaymentRequestDto;
import com.innowise.internship.dto.kafka.OrderCreatedEvent;
import com.innowise.internship.service.KafkaConsumerService;
import com.innowise.internship.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final PaymentService paymentService;

    @Override
    @KafkaListener(
            topics = "${app.kafka.topic.create-order}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        log.info("Received OrderCreatedEvent for orderId {}", orderCreatedEvent.getOrderId());
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setOrderId(orderCreatedEvent.getOrderId());
        paymentRequestDto.setUserId(orderCreatedEvent.getUserId());
        paymentRequestDto.setPaymentAmount(orderCreatedEvent.getAmount());

        paymentService.createPayment(paymentRequestDto);

    }

}
