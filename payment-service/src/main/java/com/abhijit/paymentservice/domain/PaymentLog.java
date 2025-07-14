package com.abhijit.paymentservice.domain;

import com.abhijit.paymentservice.enums.PaymentStatus;
import com.abhijit.paymentservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLog {
    private int id;
    private int transactionId;
    private int productId;
    private int quantity;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private Status status;
}
