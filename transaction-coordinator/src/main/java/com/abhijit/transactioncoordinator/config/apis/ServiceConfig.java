package com.abhijit.transactioncoordinator.config.apis;

public interface ServiceConfig {
    String getServiceName();
    String getHost();
    int getPort();
    String getPrepareEndpoint();
    String getCommitEndpoint();
    String getRollbackEndpoint();
}
