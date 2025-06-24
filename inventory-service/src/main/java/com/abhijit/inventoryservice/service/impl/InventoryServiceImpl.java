package com.abhijit.inventoryservice.service.impl;

import com.abhijit.inventoryservice.domain.InventoryItem;
import com.abhijit.inventoryservice.domain.InventoryLog;
import com.abhijit.inventoryservice.dto.OrderRequest;
import com.abhijit.inventoryservice.enums.Status;
import com.abhijit.inventoryservice.service.InventoryService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final List<InventoryItem> inventory = Collections.synchronizedList(new ArrayList<>());
    private final List<InventoryLog> inventoryLogs = Collections.synchronizedList(new ArrayList<>());
    private int counter = 0;

    @PostConstruct
    void setup(){
      log.info("setting up inventory......");
      inventory.add(new InventoryItem(1,1,10));
      inventory.add(new InventoryItem(2,2,3));
      inventory.add(new InventoryItem(3,3,5));
    }

    @Override
    public void prepareInventory(OrderRequest orderRequest) {
        synchronized (this){
            Optional<InventoryItem> optionalInventoryItem = inventory.stream()
                    .filter(inventoryItem -> inventoryItem.getProductId() == orderRequest.getProductId())
                    .findFirst();

            if(optionalInventoryItem.isPresent()){
                if(optionalInventoryItem.get().getQuantity() >= orderRequest.getQuantity()){
                    InventoryLog inventoryLog = new InventoryLog(++counter,orderRequest.getTransactionId(),orderRequest.getProductId(),orderRequest.getQuantity(), Status.PREPARE);
                    inventoryLogs.add(inventoryLog);
                    log.info("Inventory log updated with status PREPARE for transactionId: {}",orderRequest.getTransactionId());
                }else{
                    throw new RuntimeException("Insufficient Amount");
                }
            }else {
                throw new RuntimeException("Item doesn't exist");
            }
        }
    }

    @Override
    public void commitInventory(OrderRequest orderRequest) {
        synchronized (this){
            Optional<InventoryItem> optionalInventoryItem = inventory.stream()
                    .filter(inventoryItem -> inventoryItem.getProductId() == orderRequest.getProductId())
                    .findFirst();

            if(optionalInventoryItem.isPresent()){
                if(optionalInventoryItem.get().getQuantity() >= orderRequest.getQuantity()){
                    optionalInventoryItem.get().setQuantity(optionalInventoryItem.get().getQuantity()-orderRequest.getQuantity());
                    log.info("Inventory updated");
                    Optional<InventoryLog> inventoryLogOptional = inventoryLogs.stream()
                            .filter(inventoryLog -> inventoryLog.getTransactionId() == orderRequest.getTransactionId())
                            .findAny();
                    if(inventoryLogOptional.isPresent()){
                        inventoryLogOptional.get().setStatus(Status.COMMIT);
                        log.info("Inventory log updated with status COMMIT for transactionId: {}",orderRequest.getTransactionId());
                    }else {
                        throw new RuntimeException("Invalid transactionId");
                    }
                }else{
                    throw new RuntimeException("Insufficient Amount");
                }
            }else {
                throw new RuntimeException("Item doesn't exist");
            }
        }
    }

    @Override
    public void rollbackInventory(OrderRequest orderRequest) {
        Optional<InventoryLog> inventoryLogOptional = inventoryLogs.stream()
                .filter(inventoryLog -> inventoryLog.getTransactionId() == orderRequest.getTransactionId())
                .findAny();
        if(inventoryLogOptional.isPresent()){
            inventoryLogOptional.get().setStatus(Status.ROLLBACK);
            log.info("Inventory log updated with status ROLLBACK for transactionId: {}",orderRequest.getTransactionId());
        }else {
            throw new RuntimeException("Invalid transactionId");
        }
    }
}
