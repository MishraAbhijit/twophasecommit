package com.abhijit.inventoryservice.service.impl;

import com.abhijit.inventoryservice.domain.InventoryItem;
import com.abhijit.inventoryservice.domain.InventoryLog;
import com.abhijit.inventoryservice.dto.OrderRequest;
import com.abhijit.inventoryservice.enums.Status;
import com.abhijit.inventoryservice.repository.InventoryLogRepository;
import com.abhijit.inventoryservice.service.InventoryService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final List<InventoryItem> inventory = Collections.synchronizedList(new ArrayList<>());
    private final InventoryLogRepository logRepository;

    public InventoryServiceImpl(InventoryLogRepository inventoryLogRepository) {
        this.logRepository = inventoryLogRepository;
    }

    @PostConstruct
    void setup(){
      log.info("setting up inventory......");
      inventory.add(new InventoryItem(1,1,10));
      inventory.add(new InventoryItem(2,2,3));
      inventory.add(new InventoryItem(3,3,5));
    }

    @Override
    @Transactional
    public void prepareInventory(OrderRequest orderRequest) {
        Optional<InventoryLog> existing = logRepository.findByTransactionId(orderRequest.getTransactionId());
        if (existing.isPresent()) {
            if (existing.get().getStatus() == Status.PREPARE) {
                log.info("Transaction {} already prepared.", orderRequest.getTransactionId());
                return; // Idempotent check
            } else {
                throw new IllegalStateException("Transaction already in "+existing.get().getStatus()+" state");
            }
        }

        InventoryItem item = inventory.stream()
                .filter(i -> i.getProductId() == orderRequest.getProductId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getQuantity() < orderRequest.getQuantity())
            throw new RuntimeException("Insufficient stock");

        item.setQuantity(item.getQuantity() - orderRequest.getQuantity());

        InventoryLog logEntry = new InventoryLog(
                orderRequest.getTransactionId(),
                orderRequest.getProductId(),
                orderRequest.getQuantity(),
                Status.PREPARE);

        logRepository.save(logEntry);
        log.info("Prepared inventory for txId {}", orderRequest.getTransactionId());
    }

    @Override
    @Transactional
    public void commitInventory(OrderRequest orderRequest) {
        InventoryLog inventoryLog = logRepository.findByTransactionId(orderRequest.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (inventoryLog.getStatus() == Status.COMMIT) {
            log.info("Transaction {} already committed.", orderRequest.getTransactionId());
            return;
        }
        if (inventoryLog.getStatus() != Status.PREPARE)
            throw new IllegalStateException("Transaction is not in PREPARE phase");

        inventoryLog.setStatus(Status.COMMIT);
        logRepository.save(inventoryLog);
        log.info("Commit inventory for txId {}", orderRequest.getTransactionId());
    }

    @Override
    @Transactional
    public void rollbackInventory(OrderRequest orderRequest) {
        InventoryLog inventoryLog = logRepository.findByTransactionId(orderRequest.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (inventoryLog.getStatus() == Status.ROLLBACK) {
            log.info("Transaction {} already rolled back.", orderRequest.getTransactionId());
            return;
        }

        InventoryItem item = inventory.stream()
                .filter(i -> i.getProductId() == orderRequest.getProductId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        item.setQuantity(item.getQuantity() + inventoryLog.getQuantity());
        inventoryLog.setStatus(Status.ROLLBACK);
        logRepository.save(inventoryLog);
        log.info("Rollback inventory for txId {}", orderRequest.getTransactionId());
    }

}
