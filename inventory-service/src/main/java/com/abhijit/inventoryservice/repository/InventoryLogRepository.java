package com.abhijit.inventoryservice.repository;

import com.abhijit.inventoryservice.domain.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    Optional<InventoryLog> findByTransactionId(int transactionId);
}

