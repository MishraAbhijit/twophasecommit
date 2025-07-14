package com.abhijit.orderservice.domain;

import com.abhijit.orderservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLog {
    private int id;
    private int transactionId;
    private int productId;
    private int quantity;
    private BigDecimal amount;
    private Status status;
}
