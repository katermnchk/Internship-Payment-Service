package com.innowise.internship.service;

import com.innowise.internship.dto.PaymentRequestDto;
import com.innowise.internship.dto.PaymentResponseDto;
import com.innowise.internship.entity.Payment;
import com.innowise.internship.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto paymentDto);

    List<PaymentResponseDto> getPaymentsByOrderId(String orderId);

    List<PaymentResponseDto> getPaymentsByUserId(String userId);

    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);

    List<PaymentResponseDto> getPaymentsByStatuses(List<PaymentStatus> statuses);

    BigDecimal getTotalSum(Instant start, Instant end);

}
