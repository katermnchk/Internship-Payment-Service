package com.innowise.internship.dto;

import com.innowise.internship.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private String id;
    private String orderId;
    private String userId;
    private PaymentStatus status;
    private Instant timestamp;
    private BigDecimal paymentAmount;

}
