package com.abhijit.transactioncoordinator.repository;

import com.abhijit.transactioncoordinator.domain.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    List<TransactionLog> findByTransactionId(int transactionId);
    Optional<TransactionLog> findByTransactionIdAndParticipant(int transactionId, String participant);
}
