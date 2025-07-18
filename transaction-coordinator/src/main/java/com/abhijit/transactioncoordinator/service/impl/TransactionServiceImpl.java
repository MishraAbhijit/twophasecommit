package com.abhijit.transactioncoordinator.service.impl;

import com.abhijit.transactioncoordinator.config.clients.BaseClient;
import com.abhijit.transactioncoordinator.domain.TransactionLog;
import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.enums.Status;
import com.abhijit.transactioncoordinator.repository.TransactionLogRepository;
import com.abhijit.transactioncoordinator.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final List<BaseClient> participants;
    private final TransactionLogRepository logRepository;
    private final AtomicInteger transactionIdGenerator = new AtomicInteger(1);

    @Override
    public ResponseEntity<String> placeOrder(OrderRequest orderRequest) {
        int txnId = transactionIdGenerator.getAndIncrement();
        orderRequest.setTransactionId(txnId);
        log.info("Starting transaction ID: {}", txnId);

        List<BaseClient> preparedParticipants = new ArrayList<>();

        try {
            // PHASE 1: PREPARE
            for (BaseClient participant : participants) {
                String participantName = participant.getClass().getSimpleName();


                if (participant.prepare(orderRequest)) {
                    preparedParticipants.add(participant);
                    persistLog(txnId, participantName, Status.PREPARE);
                    log.info("Prepared: {}", participantName);
                } else {
                    throw new IllegalStateException("PREPARE failed for " + participantName);
                }
            }
            log.info("All prepared.....");
            Thread.sleep(400000);
            // PHASE 2: COMMIT
            for (BaseClient participant : preparedParticipants) {
                String participantName = participant.getClass().getSimpleName();

                if (logRepository.findByTransactionIdAndParticipant(txnId, participantName)
                        .map(log -> log.getStatus() == Status.COMMIT).orElse(false)) {
                    log.info("Already COMMITTED: {}", participantName);
                    continue;
                }

                participant.commit(orderRequest);
                persistLog(txnId, participantName, Status.COMMIT);
                log.info("Committed: {}", participantName);
            }

            return ResponseEntity.ok("Transaction committed successfully");

        } catch (Exception ex) {
            log.error("Transaction FAILED. Starting rollback. Reason: {}", ex.getMessage());

            // ROLLBACK already prepared
            for (BaseClient participant : preparedParticipants) {
                String participantName = participant.getClass().getSimpleName();

                if (logRepository.findByTransactionIdAndParticipant(txnId, participantName)
                        .map(log -> log.getStatus() == Status.ROLLBACK).orElse(false)) {
                    log.info("Already rolled back: {}", participantName);
                    continue;
                }

                try {
                    participant.rollback(orderRequest);
                    persistLog(txnId, participantName, Status.ROLLBACK);
                    log.info("Rolled back: {}", participantName);
                } catch (Exception e) {
                    log.warn("Rollback failed for {}: {}", participantName, e.getMessage());
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Transaction rolled back. Reason: " + ex.getMessage());
        }
    }

    private void persistLog(int txnId, String participant, Status status) {
        Optional<TransactionLog> byTransactionIdAndParticipant = logRepository.findByTransactionIdAndParticipant(txnId, participant);


        if(byTransactionIdAndParticipant.isPresent())
        {
            byTransactionIdAndParticipant.get().setStatus(status);
            logRepository.save(byTransactionIdAndParticipant.get());
        }else {
            TransactionLog logEntry = TransactionLog.builder()
                    .transactionId(txnId)
                    .participant(participant)
                    .status(status)
                    .timestamp(LocalDateTime.now())
                    .build();

            logRepository.save(logEntry);
        }

    }
}
