package com.abhijit.inventoryservice.domain;

import com.abhijit.inventoryservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLog {
    private int id;
    private int transactionId;
    private int productId;
    private int quantity;
    private Status status;
}
