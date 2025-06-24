package com.abhijit.inventoryservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryItem {
    private int id;
    private int productId;
    private int quantity;
}
