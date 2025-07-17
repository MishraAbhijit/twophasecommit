package com.abhijit.paymentservice.repository;

import com.abhijit.paymentservice.domain.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    Optional<PaymentLog> findByTransactionId(Integer transactionId);
}