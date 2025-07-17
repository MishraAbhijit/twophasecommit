package com.abhijit.orderservice.repository;

import com.abhijit.orderservice.domain.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    Optional<OrderLog> findByTransactionId(Integer transactionId);
}

