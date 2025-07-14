package com.abhijit.transactioncoordinator.api;

import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionCoordinatorController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/place-order")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
       return transactionService.paceOrder(orderRequest);
    }
}

