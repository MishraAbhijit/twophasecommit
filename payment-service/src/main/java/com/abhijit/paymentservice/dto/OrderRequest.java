package com.abhijit.paymentservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    private int transactionId;
    private int productId;
    private int quantity;
    private BigDecimal amount;
}
