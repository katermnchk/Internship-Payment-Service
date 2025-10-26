package com.innowise.internship.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    @NotBlank(message = "Order ID can't be empty")
    private String orderId;

    @NotBlank(message = "User ID can't be empty")
    private String userId;

    @NotNull(message = "Payment amount can't be null")
    @Positive(message = "Payment amount must be positive")
    private BigDecimal amount;
}
