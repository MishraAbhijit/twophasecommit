package com.abhijit.inventoryservice.domain;

import com.abhijit.inventoryservice.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int transactionId;
    private int productId;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createdAt = LocalDateTime.now();

    public InventoryLog(int transactionId, int productId, int quantity, Status status) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
    }
}
