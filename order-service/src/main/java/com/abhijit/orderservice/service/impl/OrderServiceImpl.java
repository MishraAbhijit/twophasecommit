package com.abhijit.orderservice.service.impl;

import com.abhijit.orderservice.domain.OrderLog;
import com.abhijit.orderservice.dto.OrderRequest;
import com.abhijit.orderservice.enums.Status;
import com.abhijit.orderservice.repository.OrderLogRepository;
import com.abhijit.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderLogRepository orderLogRepository;

    @Override
    @Transactional
    public void prepareOrder(OrderRequest request) {
        Optional<OrderLog> existingLog = orderLogRepository.findByTransactionId(request.getTransactionId());

        if (existingLog.isPresent()) {
            log.info("Prepare already done for transaction {}", request.getTransactionId());
            return; // Idempotent
        }

        OrderLog logEntry = OrderLog.builder()
                .transactionId(request.getTransactionId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .amount(request.getAmount())
                .status(Status.PREPARED)
                .build();

        orderLogRepository.save(logEntry);
        log.info("Prepared order for transaction {}", request.getTransactionId());
    }

    @Override
    @Transactional
    public void commitOrder(OrderRequest request) {
        OrderLog orderLog = orderLogRepository.findByTransactionId(request.getTransactionId())
                .orElseThrow(() -> new IllegalStateException("No PREPARE found to COMMIT"));

        if (orderLog.getStatus() == Status.COMMITTED) {
            log.info("Already committed transaction {}", request.getTransactionId());
            return; // Idempotent
        }

        if (orderLog.getStatus() != Status.PREPARED) {
            throw new IllegalStateException("Cannot commit unless PREPARED");
        }

        orderLog.setStatus(Status.COMMITTED);
        orderLogRepository.save(orderLog);
        log.info("Committed order for transaction {}", request.getTransactionId());
    }

    @Override
    @Transactional
    public void rollbackOrder(OrderRequest request) {
        Optional<OrderLog> optionalLog = orderLogRepository.findByTransactionId(request.getTransactionId());

        if (optionalLog.isEmpty()) {
            log.info("Nothing to rollback for transaction {}", request.getTransactionId());
            return; // No-op
        }

        OrderLog orderLog = optionalLog.get();

        if (orderLog.getStatus() == Status.ROLLBACK) {
            log.info("Already rolled back transaction {}", request.getTransactionId());
            return; // Idempotent
        }

        orderLog.setStatus(Status.ROLLBACK);
        orderLogRepository.save(orderLog);
        log.info("Rolled back order for transaction {}", request.getTransactionId());
    }
}