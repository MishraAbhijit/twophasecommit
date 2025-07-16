package com.abhijit.transactioncoordinator.config.apis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "services.payment")
@Data
public class PaymentServiceConfig implements ServiceConfig{
    private String name;
    private String host;
    private int port;
    private ServiceEndpoints endpoint;

    @Override
    public String getServiceName() {
        return this.name;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getPrepareEndpoint() {
        return this.getEndpoint().getPrepare();
    }

    @Override
    public String getCommitEndpoint() {
        return this.getEndpoint().getCommit();
    }

    @Override
    public String getRollbackEndpoint() {
        return this.getEndpoint().getRollback();
    }
}
