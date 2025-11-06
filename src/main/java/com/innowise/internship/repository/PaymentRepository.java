package com.innowise.internship.repository;

import com.innowise.internship.entity.Payment;
import com.innowise.internship.entity.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByOrderId(String orderId);

    List<Payment> findByUserId(String userId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByStatusIn(List<PaymentStatus> statuses);

    List<Payment> findByTimestampBetween(Instant start, Instant end);
}
