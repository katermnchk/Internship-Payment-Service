package com.innowise.internship.service.impl;

import com.innowise.internship.dto.PaymentRequestDto;
import com.innowise.internship.dto.PaymentResponseDto;
import com.innowise.internship.dto.kafka.PaymentCreatedEvent;
import com.innowise.internship.entity.Payment;
import com.innowise.internship.entity.PaymentStatus;
import com.innowise.internship.mapper.PaymentMapper;
import com.innowise.internship.repository.PaymentRepository;
import com.innowise.internship.service.KafkaProducerService;
import com.innowise.internship.service.PaymentService;
import com.innowise.internship.service.RandomNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomNumberService randomNumberService;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentDto) {
        Payment payment = paymentMapper.paymentDtoToEntity(paymentDto);

        payment.setTimestamp(Instant.now());

        boolean isEven = randomNumberService.isNumberEven();
        payment.setStatus(isEven ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

        Payment savedPayment = paymentRepository.save(payment);

        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent(
                String.valueOf(savedPayment.getOrderId()),
                String.valueOf(savedPayment.getStatus().toString())
        );
        log.info("Sending PaymentCreatedEvent for orderId {}", savedPayment.getOrderId());
        kafkaProducerService.sendPaymentCreatedEvent(paymentCreatedEvent);

        return paymentMapper.entityToPaymentDto(savedPayment);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::entityToPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(paymentMapper::entityToPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(paymentMapper::entityToPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByStatuses(List<PaymentStatus> statuses) {
        return paymentRepository.findByStatusIn(statuses).stream()
                .map(paymentMapper::entityToPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalSum(Instant start, Instant end) {
        List<Payment> payments = paymentRepository.findByTimestampBetween(start, end);

        return payments.stream()
                .map(Payment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
