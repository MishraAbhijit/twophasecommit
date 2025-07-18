package com.abhijit.transactioncoordinator.repository;

import com.abhijit.transactioncoordinator.domain.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    List<TransactionLog> findByTransactionId(int transactionId);
    Optional<TransactionLog> findByTransactionIdAndParticipant(int transactionId, String participant);

    @Query("SELECT DISTINCT t.transactionId FROM TransactionLog t WHERE t.status = 'PREPARE'")
    List<Integer> findStuckPreparedTransactions();

    @Query("SELECT t FROM TransactionLog t WHERE t.transactionId = :transactionId")
    List<TransactionLog> findAllByTransactionId(@Param("transactionId") int transactionId);

}
