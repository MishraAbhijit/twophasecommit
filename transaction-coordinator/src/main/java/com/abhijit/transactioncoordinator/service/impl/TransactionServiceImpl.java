package com.abhijit.transactioncoordinator.service.impl;

import com.abhijit.transactioncoordinator.config.clients.BaseClient;
import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private List<BaseClient> participants;

    @Override
    public ResponseEntity<String> placeOrder(OrderRequest orderRequest) {
        AtomicInteger counter = new AtomicInteger(1);
        orderRequest.setTransactionId(counter.getAndIncrement());

        log.info("Initiating transaction with ID: {}", orderRequest.getTransactionId());

        // Keep track of participants that successfully PREPARE
        List<BaseClient> successfulPrepares = new ArrayList<>();

        try {
            // Phase 1: Prepare
            for (BaseClient client : participants) {
                if (client.prepare(orderRequest)) {
                    successfulPrepares.add(client);
                } else {
                    throw new IllegalStateException("Prepare phase failed for: " + client.getClass().getSimpleName());
                }
            }

            // Phase 2: Commit
            for (BaseClient client : successfulPrepares) {
                client.commit(orderRequest);
            }

            return ResponseEntity.ok("Transaction committed successfully");

        } catch (Exception e) {
            log.error("Transaction failed. Rolling back. Reason: {}", e.getMessage());

            // Phase 3: Rollback only successful prepares
            for (BaseClient client : successfulPrepares) {
                try {
                    client.rollback(orderRequest);
                } catch (Exception rollbackEx) {
                    log.warn("Rollback failed for {}: {}", client.getClass().getSimpleName(), rollbackEx.getMessage());
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Transaction rolled back due to failure: " + e.getMessage());
        }
    }
}
