package com.abhijit.paymentservice.service.impl;

import com.abhijit.paymentservice.domain.PaymentLog;
import com.abhijit.paymentservice.dto.OrderRequest;
import com.abhijit.paymentservice.enums.PaymentStatus;
import com.abhijit.paymentservice.enums.Status;
import com.abhijit.paymentservice.repository.PaymentLogRepository;
import com.abhijit.paymentservice.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentLogRepository paymentLogRepository;

    @Override
    @Transactional
    public void preparePayment(OrderRequest orderRequest) {
        paymentLogRepository.findByTransactionId(orderRequest.getTransactionId())
                .ifPresentOrElse(existing -> {
                    if (existing.getStatus() == Status.PREPARED) {
                        log.info("Idempotent PREPARE detected for Txn {}", orderRequest.getTransactionId());
                    } else {
                        log.warn("Invalid state PREPARE attempted for Txn {}. State: {}", orderRequest.getTransactionId(), existing.getStatus());
                    }
                }, () -> {
                    PaymentLog logEntry = PaymentLog.builder()
                            .transactionId(orderRequest.getTransactionId())
                            .productId(orderRequest.getProductId())
                            .quantity(orderRequest.getQuantity())
                            .amount(orderRequest.getAmount())
                            .paymentStatus(PaymentStatus.CAPTURED)
                            .status(Status.PREPARED)
                            .build();
                    paymentLogRepository.save(logEntry);
                    log.info("PREPARE phase done for Txn {}", orderRequest.getTransactionId());
                });
    }

    @Override
    @Transactional
    public void commitPayment(OrderRequest orderRequest) {
        PaymentLog paymentLog = paymentLogRepository.findByTransactionId(orderRequest.getTransactionId())
                .orElseThrow(() -> new RuntimeException("No prepared payment found for Txn: " + orderRequest.getTransactionId()));

        if (paymentLog.getStatus() == Status.COMMITTED) {
            log.info("Idempotent COMMIT for Txn {}", orderRequest.getTransactionId());
            return;
        }

        if (paymentLog.getStatus() != Status.PREPARED) {
            log.warn("Invalid COMMIT. Txn {} is in state: {}", orderRequest.getTransactionId(), paymentLog.getStatus());
            return;
        }

        paymentLog.setPaymentStatus(PaymentStatus.SUCCESSFUL);
        paymentLog.setStatus(Status.COMMITTED);
        paymentLogRepository.save(paymentLog);
        log.info("COMMIT phase done for Txn {}", orderRequest.getTransactionId());
    }

    @Override
    @Transactional
    public void rollbackPayment(OrderRequest orderRequest) {
        PaymentLog paymentLog = paymentLogRepository.findByTransactionId(orderRequest.getTransactionId())
                .orElseThrow(() -> new RuntimeException("No prepared payment found for Txn: " + orderRequest.getTransactionId()));

        if (paymentLog.getStatus() == Status.ROLLBACK) {
            log.info("Idempotent ROLLBACK for Txn {}", orderRequest.getTransactionId());
            return;
        }

        if (paymentLog.getStatus() != Status.PREPARED) {
            log.warn("Invalid ROLLBACK. Txn {} is in state: {}", orderRequest.getTransactionId(), paymentLog.getStatus());
            return;
        }

        paymentLog.setPaymentStatus(PaymentStatus.FAILED);
        paymentLog.setStatus(Status.ROLLBACK);
        paymentLogRepository.save(paymentLog);
        log.info("ROLLBACK phase done for Txn {}", orderRequest.getTransactionId());
    }
}
