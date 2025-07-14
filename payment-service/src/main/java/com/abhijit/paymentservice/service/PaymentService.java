package com.abhijit.paymentservice.service;

import com.abhijit.paymentservice.dto.OrderRequest;

public interface PaymentService {
    void preparePayment(OrderRequest orderRequest);
    void commitPayment(OrderRequest orderRequest);
    void rollbackPayment(OrderRequest orderRequest);
}
