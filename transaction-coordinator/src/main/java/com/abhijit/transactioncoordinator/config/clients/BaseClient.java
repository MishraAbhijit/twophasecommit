package com.abhijit.transactioncoordinator.config.clients;

import com.abhijit.transactioncoordinator.dto.OrderRequest;
import org.springframework.http.ResponseEntity;

public interface BaseClient {
    boolean prepare(OrderRequest orderRequest);
    boolean commit(OrderRequest orderRequest);
    boolean rollback(OrderRequest orderRequest);
}
