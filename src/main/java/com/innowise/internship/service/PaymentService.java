package com.innowise.internship.service;

import com.innowise.internship.entity.Payment;
import com.innowise.internship.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PaymentService {

    Payment createPayment(Payment payment);

    List<Payment> getPaymentsByOrderId(String orderId);

    List<Payment> getPaymentsByUserId(String userId);

    List<Payment> getPaymentsByStatus(PaymentStatus status);

    List<Payment> getPaymentsByStatuses(List<PaymentStatus> statuses);

    BigDecimal getTotalSum(Instant start, Instant end);

}
