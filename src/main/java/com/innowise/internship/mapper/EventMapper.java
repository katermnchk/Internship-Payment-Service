package com.innowise.internship.mapper;

import com.innowise.internship.dto.PaymentRequestDto;
import com.innowise.internship.dto.kafka.OrderCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    @Mapping(source = "amount", target = "paymentAmount")
    PaymentRequestDto toPaymentRequestDto(OrderCreatedEvent event);

}
