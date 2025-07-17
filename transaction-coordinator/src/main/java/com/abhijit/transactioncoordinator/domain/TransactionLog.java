package com.abhijit.transactioncoordinator.domain;

import com.abhijit.transactioncoordinator.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer transactionId;

    private String participant; // e.g., "OrderService", "PaymentService"

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime timestamp;
}
