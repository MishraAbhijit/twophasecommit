package com.abhijit.transactioncoordinator.config.clients;

import com.abhijit.transactioncoordinator.config.apis.ServiceConfigFactory;
import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.enums.Status;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(1)
public class PaymentServiceClient extends ApiClient implements BaseClient {

    public PaymentServiceClient(RestTemplate restTemplate, ServiceConfigFactory serviceConfigFactory) {
        super(restTemplate, serviceConfigFactory.getConfig("payment-service"));
    }

    @Override
    public boolean prepare(OrderRequest orderRequest) {
        return call(Status.PREPARE,orderRequest);
    }

    @Override
    public boolean commit(OrderRequest orderRequest) {
        return call(Status.COMMIT,orderRequest);
    }

    @Override
    public boolean rollback(OrderRequest orderRequest) {
        return call(Status.ROLLBACK,orderRequest);
    }
}
