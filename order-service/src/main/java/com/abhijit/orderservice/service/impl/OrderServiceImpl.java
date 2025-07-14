package com.abhijit.orderservice.service.impl;

import com.abhijit.orderservice.domain.OrderLog;
import com.abhijit.orderservice.dto.OrderRequest;
import com.abhijit.orderservice.enums.Status;
import com.abhijit.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final List<OrderLog> orderLogs = Collections.synchronizedList(new ArrayList<>());
    int counter = 0;

    @Override
    public void prepareOrder(OrderRequest orderRequest) {
        synchronized (this) {
            Optional<OrderLog> optionalOrderLog = orderLogs.stream().filter(orderLog -> orderLog.getTransactionId() == orderRequest.getTransactionId()).findAny();

            if (optionalOrderLog.isEmpty()) {
                OrderLog orderLog = OrderLog.builder()
                        .id(++counter)
                        .transactionId(orderRequest.getTransactionId())
                        .productId(orderRequest.getProductId())
                        .quantity(orderRequest.getQuantity())
                        .amount(orderRequest.getAmount())
                        .status(Status.PREPARED)
                        .build();

                orderLogs.add(orderLog);
                log.info("Order Created.....");
            }
        }
    }

    @Override
    public void commitOrder(OrderRequest orderRequest) {
        synchronized (this) {
            Optional<OrderLog> optionalOrderLog = orderLogs.stream().filter(orderLog -> orderLog.getTransactionId() == orderRequest.getTransactionId()).findAny();
            optionalOrderLog.ifPresent(orderLog -> orderLog.setStatus(Status.COMMITTED));
            log.info("Order Committed.....");
        }
    }

    @Override
    public void rollbackOrder(OrderRequest orderRequest) {
        synchronized (this) {
            Optional<OrderLog> optionalOrderLog = orderLogs.stream().filter(orderLog -> orderLog.getTransactionId() == orderRequest.getTransactionId()).findAny();
            optionalOrderLog.ifPresent(orderLog -> orderLog.setStatus(Status.ROLLBACK));
            log.info("Order Rolled back.....");
        }
    }
}
