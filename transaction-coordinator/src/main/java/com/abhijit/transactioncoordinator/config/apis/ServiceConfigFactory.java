package com.abhijit.transactioncoordinator.config.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ServiceConfigFactory {

    private final Map<String, ServiceConfig> configMap;

    @Autowired
    public ServiceConfigFactory(List<ServiceConfig> configs) {
        this.configMap = configs.stream()
                .collect(Collectors.toMap(ServiceConfig::getServiceName, Function.identity()));
    }

    public ServiceConfig getConfig(String serviceName) {
        return Optional.ofNullable(configMap.get(serviceName))
                .orElseThrow(() -> new IllegalArgumentException("No config found for service: " + serviceName));
    }
}

