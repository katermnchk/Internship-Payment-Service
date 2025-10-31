package com.innowise.internship.service.impl;

import com.innowise.internship.dto.PaymentRequestDto;
import com.innowise.internship.dto.PaymentResponseDto;
import com.innowise.internship.dto.kafka.PaymentCreatedEvent;
import com.innowise.internship.entity.Payment;
import com.innowise.internship.entity.PaymentStatus;
import com.innowise.internship.mapper.PaymentMapper;
import com.innowise.internship.repository.PaymentRepository;
import com.innowise.internship.service.KafkaProducerService;
import com.innowise.internship.service.RandomNumberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private RandomNumberService randomNumberService;
    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequestDto paymentRequestDto;
    private Payment payment;
    private PaymentResponseDto paymentResponseDto;

    @BeforeEach
    public void setUp() {
        paymentRequestDto = new PaymentRequestDto("order12", "payment12", new BigDecimal("100.00"));
        payment = new Payment(
                "mongoId12",
                "order12",
                "user12",
                null,
                Instant.now(),
                new BigDecimal("100.00")
        );
        paymentResponseDto = new PaymentResponseDto(
                "mongoId12",
                "order12",
                "user12",
                PaymentStatus.SUCCESS,
                payment.getTimestamp(),
                new BigDecimal("100.00")
        );
    }

    @Test
    void givenSuccessfulPayment_whenCreatePayment_thenStatusIsSuccess() {
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentMapper.paymentDtoToEntity(paymentRequestDto)).thenReturn(payment);
        when(randomNumberService.isNumberEven()).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        doNothing().when(kafkaProducerService).sendPaymentCreatedEvent(any(PaymentCreatedEvent.class));

        when(paymentMapper.entityToPaymentDto(payment)).thenReturn(paymentResponseDto);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        ArgumentCaptor<PaymentCreatedEvent> paymentCreatedEventCaptor = ArgumentCaptor.forClass(PaymentCreatedEvent.class);
        PaymentResponseDto result = paymentService.createPayment(paymentRequestDto);

        verify(paymentRepository).save(paymentCaptor.capture());
        verify(kafkaProducerService).sendPaymentCreatedEvent(paymentCreatedEventCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        PaymentCreatedEvent savedPaymentCreatedEvent = paymentCreatedEventCaptor.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(PaymentStatus.SUCCESS, savedPayment.getStatus()),
                () -> assertEquals("order12", savedPaymentCreatedEvent.getOrderId()),
                () -> assertEquals(paymentResponseDto, result)
        );
    }

    @Test
    void givenUnsuccessfulPayment_whenCreatePayment_thenStatusIsFailed() {
        payment.setStatus(PaymentStatus.FAILED);
        paymentResponseDto.setStatus(PaymentStatus.FAILED);
        when(paymentMapper.paymentDtoToEntity(paymentRequestDto)).thenReturn(payment);
        when(randomNumberService.isNumberEven()).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        doNothing().when(kafkaProducerService).sendPaymentCreatedEvent(any(PaymentCreatedEvent.class));

        when(paymentMapper.entityToPaymentDto(payment)).thenReturn(paymentResponseDto);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        ArgumentCaptor<PaymentCreatedEvent> paymentCreatedEventCaptor = ArgumentCaptor.forClass(PaymentCreatedEvent.class);
        PaymentResponseDto result = paymentService.createPayment(paymentRequestDto);

        verify(paymentRepository).save(paymentCaptor.capture());
        verify(kafkaProducerService).sendPaymentCreatedEvent(paymentCreatedEventCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        PaymentCreatedEvent savedPaymentCreatedEvent = paymentCreatedEventCaptor.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(PaymentStatus.FAILED, savedPayment.getStatus()),
                () -> assertEquals("order12", savedPaymentCreatedEvent.getOrderId()),
                () -> assertEquals("FAILED", savedPaymentCreatedEvent.getPaymentStatus()),
                () ->assertEquals(paymentResponseDto, result)
        );
    }

    @Test
    void givenOrderId_whenGetPayments_ThenReturnPaymentList() {
        String orderId = "order12";

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Collections.singletonList(payment));
        when(paymentMapper.entityToPaymentDto(payment)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result =  paymentService.getPaymentsByOrderId(orderId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(paymentResponseDto, result.get(0))
        );
        verify(paymentRepository).findByOrderId(orderId);
    }

    @Test
    void givenUserId_whenGetPayments_ThenReturnPaymentList() {
        String userId = "user12";

        when(paymentRepository.findByUserId(userId)).thenReturn(Collections.singletonList(payment));
        when(paymentMapper.entityToPaymentDto(payment)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result =  paymentService.getPaymentsByUserId(userId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(paymentResponseDto, result.get(0))
        );
        verify(paymentRepository).findByUserId(userId);
    }

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void givenPaymentStatus_whenGetPayments_ThenReturnPaymentList(PaymentStatus paymentStatus) {
        payment.setStatus(paymentStatus);
        paymentResponseDto.setStatus(paymentStatus);

        when(paymentRepository.findByStatus(paymentStatus)).thenReturn(Collections.singletonList(payment));
        when(paymentMapper.entityToPaymentDto(payment)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result =  paymentService.getPaymentsByStatus(paymentStatus);

        assertAll(
                () -> assertNotNull(result),
                () -> assertFalse(result.isEmpty()),
                () -> assertEquals(paymentStatus, result.get(0).getStatus())
        );
        verify(paymentRepository).findByStatus(paymentStatus);
    }

    @Test
    void givenStatuses_whenGetPayments_ThenReturnPaymentList() {
        List<PaymentStatus> paymentStatuses = List.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED);
        when(paymentRepository.findByStatusIn(paymentStatuses)).thenReturn(List.of(payment));
        when(paymentMapper.entityToPaymentDto(payment)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result = paymentService.getPaymentsByStatuses(paymentStatuses);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(paymentResponseDto, result.get(0))
        );
        verify(paymentRepository).findByStatusIn(paymentStatuses);
    }

    @Test
    void givenDateRange_whenGetTotalSum_thenReturnTotalSum() {
        Instant start = Instant.MIN;
        Instant end = Instant.MAX;

        Payment newPayment =
                new Payment(null, null, null, null, null, new BigDecimal("50.00"));

        List<Payment> payments = List.of(payment, newPayment);
        when(paymentRepository.findByTimestampBetween(start, end)).thenReturn(payments);

        BigDecimal totalSum = paymentService.getTotalSum(start, end);
        assertEquals(new BigDecimal("150.00"), totalSum);
        verify(paymentRepository).findByTimestampBetween(start, end);
    }

}
