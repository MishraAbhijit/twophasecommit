package com.abhijit.orderservice.service;

import com.abhijit.orderservice.dto.OrderRequest;

public interface OrderService {
    void prepareOrder(OrderRequest orderRequest);
    void commitOrder(OrderRequest orderRequest);
    void rollbackOrder(OrderRequest orderRequest);
}
