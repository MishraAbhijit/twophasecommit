package com.abhijit.paymentservice.domain;

import com.abhijit.paymentservice.enums.PaymentStatus;
import com.abhijit.paymentservice.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int transactionId;
    private int productId;
    private int quantity;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

}
