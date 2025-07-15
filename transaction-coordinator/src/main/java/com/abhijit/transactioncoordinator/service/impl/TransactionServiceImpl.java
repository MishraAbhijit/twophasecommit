package com.abhijit.transactioncoordinator.service.impl;

import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final RestTemplate restTemplate;

    public TransactionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> paceOrder(OrderRequest orderRequest) {

        int transactionId = UUID.randomUUID().clockSequence();
        orderRequest.setTransactionId(transactionId);

        try {
            log.info("initiating transaction with id : {}", transactionId);

            //Phase 1: Initializing Transaction - Prepare Phase
            ResponseEntity<String> inventoryPrep = restTemplate.postForEntity("http://localhost:8082/inventory/prepare", orderRequest, String.class);
            ResponseEntity<String> paymentPrep = restTemplate.postForEntity("http://localhost:8083/payment/prepare", orderRequest, String.class);
            ResponseEntity<String> orderPrep = restTemplate.postForEntity("http://localhost:8084/order/prepare", orderRequest, String.class);

            if (inventoryPrep.getStatusCode().is2xxSuccessful() && paymentPrep.getStatusCode().is2xxSuccessful() && orderPrep.getStatusCode().is2xxSuccessful()) {
                // Phase 2: Commit Phase
                restTemplate.postForEntity("http://localhost:8082/inventory/commit", orderRequest, String.class);
                restTemplate.postForEntity("http://localhost:8083/payment/commit", orderRequest, String.class);
                restTemplate.postForEntity("http://localhost:8084/order/commit", orderRequest, String.class);
                return ResponseEntity.ok("Transaction committed successfully");
            } else {
                throw new IllegalStateException("Prepare phase failed... ");
            }
        } catch (Exception exception) {
            // Phase 3: Rollback Phase
            restTemplate.postForEntity("http://localhost:8082/inventory/rollback", orderRequest, String.class);
            restTemplate.postForEntity("http://localhost:8083/payment/rollback", orderRequest, String.class);
            restTemplate.postForEntity("http://localhost:8084/order/rollback", orderRequest, String.class);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction rolled back due to failure");
        }
    }
}
