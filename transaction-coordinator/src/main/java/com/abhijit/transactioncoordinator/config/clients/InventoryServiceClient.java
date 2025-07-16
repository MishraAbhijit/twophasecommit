package com.abhijit.transactioncoordinator.config.clients;

import com.abhijit.transactioncoordinator.config.apis.ServiceConfig;
import com.abhijit.transactioncoordinator.config.apis.ServiceConfigFactory;
import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.enums.TransactionPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(2)
public class InventoryServiceClient extends ApiClient implements BaseClient {

    public InventoryServiceClient(RestTemplate restTemplate, ServiceConfigFactory serviceConfigFactory) {
        super(restTemplate, serviceConfigFactory.getConfig("inventory-service"));
    }

    @Override
    public boolean prepare(OrderRequest orderRequest) {
        return call(TransactionPhase.PREPARE,orderRequest);
    }

    @Override
    public boolean commit(OrderRequest orderRequest) {
        return call(TransactionPhase.COMMIT,orderRequest);
    }

    @Override
    public boolean rollback(OrderRequest orderRequest) {
        return call(TransactionPhase.ROLLBACK,orderRequest);
    }
}
