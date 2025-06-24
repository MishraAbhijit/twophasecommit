package com.abhijit.inventoryservice.service;

import com.abhijit.inventoryservice.dto.OrderRequest;
import org.springframework.stereotype.Service;

@Service
public interface InventoryService {
    void prepareInventory(OrderRequest orderRequest);
    void commitInventory(OrderRequest orderRequest);
    void rollbackInventory(OrderRequest orderRequest);
}
