package com.abhijit.transactioncoordinator.scheduler;

import com.abhijit.transactioncoordinator.config.clients.BaseClient;
import com.abhijit.transactioncoordinator.domain.TransactionLog;
import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.enums.Status;
import com.abhijit.transactioncoordinator.repository.TransactionLogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionRecoveryScheduler {

    private final TransactionLogRepository logRepository;
    private final List<BaseClient> participants;

    /**
     * Executes once during service startup to recover PREPARE state transactions
     */
    @PostConstruct
    @Transactional
    public void retryStuckTransactions() {
        List<Integer> stuckTransactionIds = logRepository.findStuckPreparedTransactions();
        log.info("Stucked Transaction Ids "+stuckTransactionIds.size());
        for (int txnId : stuckTransactionIds) {
            log.info("Checking recovery for transaction ID: {}", txnId);

            List<TransactionLog> logs = logRepository.findAllByTransactionId(txnId);
            Map<String, Status> participantStatusMap = logs.stream()
                    .collect(Collectors.toMap(TransactionLog::getParticipant, TransactionLog::getStatus));

            boolean allPrepared = participantStatusMap.size() == participants.size()
                    && participantStatusMap.values().stream().allMatch(status -> status == Status.PREPARE);

            if (allPrepared) {
                log.info("Retrying COMMIT for transaction {}", txnId);
                retryCommit(txnId, participantStatusMap);
            } else {
                log.info("Retrying ROLLBACK for transaction {}", txnId);
                retryRollback(txnId, participantStatusMap);
            }
        }
    }

    private void retryCommit(int txnId, Map<String, Status> participantStatusMap) {
        OrderRequest request = new OrderRequest();
        request.setTransactionId(txnId);

        for (BaseClient client : participants) {
            String name = client.getClass().getSimpleName();

            if (participantStatusMap.getOrDefault(name, null) != Status.COMMIT) {
                try {
                    client.commit(request);
                    persistLog(txnId, name, Status.COMMIT);
                    log.info("Recovered COMMIT for {}", name);
                } catch (Exception e) {
                    log.warn("Retry COMMIT failed for {}: {}", name, e.getMessage());
                }
            }
        }
    }

    private void retryRollback(int txnId, Map<String, Status> participantStatusMap) {
        OrderRequest request = new OrderRequest();
        request.setTransactionId(txnId);

        for (BaseClient client : participants) {
            String name = client.getClass().getSimpleName();

            if (participantStatusMap.getOrDefault(name, null) != Status.ROLLBACK) {
                try {
                    client.rollback(request);
                    persistLog(txnId, name, Status.ROLLBACK);
                    log.info("Recovered ROLLBACK for {}", name);
                } catch (Exception e) {
                    log.warn("Retry ROLLBACK failed for {}: {}", name, e.getMessage());
                }
            }
        }
    }

    private void persistLog(int txnId, String participant, Status status) {
        Optional<TransactionLog> transactionLog = logRepository.findByTransactionIdAndParticipant(txnId, participant);
        if(transactionLog.isPresent()){
            transactionLog.get().setStatus(status);
            logRepository.save(transactionLog.get());
        }
    }
}

