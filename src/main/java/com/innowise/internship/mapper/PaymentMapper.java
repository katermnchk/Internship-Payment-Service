package com.innowise.internship.mapper;

import com.innowise.internship.dto.PaymentRequestDto;
import com.innowise.internship.dto.PaymentResponseDto;
import com.innowise.internship.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    Payment paymentDtoToEntity(PaymentRequestDto dto);

    PaymentResponseDto entityToPaymentDto(Payment entity);
}
