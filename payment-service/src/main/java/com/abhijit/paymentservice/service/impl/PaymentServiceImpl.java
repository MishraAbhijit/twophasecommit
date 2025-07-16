package com.abhijit.paymentservice.service.impl;

import com.abhijit.paymentservice.domain.PaymentLog;
import com.abhijit.paymentservice.dto.OrderRequest;
import com.abhijit.paymentservice.enums.PaymentStatus;
import com.abhijit.paymentservice.enums.Status;
import com.abhijit.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final List<PaymentLog> paymentLogs = Collections.synchronizedList(new ArrayList<>());
    private int counter = 0;

    @Override
    public void preparePayment(OrderRequest orderRequest) {
        Optional<PaymentLog> optionalPaymentLog = paymentLogs.stream().filter(paymentLog -> paymentLog.getTransactionId() == orderRequest.getTransactionId()).findAny();

        if(optionalPaymentLog.isEmpty()){
            PaymentLog paymentLog = PaymentLog.builder()
                    .id(++counter)
                    .transactionId(orderRequest.getTransactionId())
                    .productId(orderRequest.getProductId())
                    .quantity(orderRequest.getQuantity())
                    .amount(orderRequest.getAmount())
                    .paymentStatus(PaymentStatus.CAPTURED)
                    .status(Status.PREPARED)
                    .build();

            paymentLogs.add(paymentLog);
            log.info("Payment in prepared state");
        }

    }

    @Override
    public void commitPayment(OrderRequest orderRequest) {
        Optional<PaymentLog> optionalPaymentLog = paymentLogs.stream().filter(paymentLog -> paymentLog.getTransactionId() == orderRequest.getTransactionId()).findAny();
        optionalPaymentLog.ifPresent(paymentLog -> {
            paymentLog.setPaymentStatus(PaymentStatus.SUCCESSFUL);
            paymentLog.setStatus(Status.COMMITTED);
        });
        log.info("Payment is committed....");
    }

    @Override
    public void rollbackPayment(OrderRequest orderRequest) {
        Optional<PaymentLog> optionalPaymentLog = paymentLogs.stream().filter(paymentLog -> paymentLog.getTransactionId() == orderRequest.getTransactionId()).findAny();
        optionalPaymentLog.ifPresent(paymentLog -> {
            paymentLog.setPaymentStatus(PaymentStatus.FAILED);
            paymentLog.setStatus(Status.ROLLBACK);
        });log.info("Payment is rolled back....");
    }
}
