package com.abhijit.inventoryservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private int transactionId;
    private int productId;
    private int quantity;
}
