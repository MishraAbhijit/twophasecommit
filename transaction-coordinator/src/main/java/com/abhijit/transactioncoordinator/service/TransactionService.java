package com.abhijit.transactioncoordinator.service;

import com.abhijit.transactioncoordinator.dto.OrderRequest;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    ResponseEntity<String> paceOrder(OrderRequest orderRequest);
}
