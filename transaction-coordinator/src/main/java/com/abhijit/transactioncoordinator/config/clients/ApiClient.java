package com.abhijit.transactioncoordinator.config.clients;

import com.abhijit.transactioncoordinator.config.apis.ServiceConfig;
import com.abhijit.transactioncoordinator.config.apis.ServiceConfigFactory;
import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.enums.TransactionPhase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Slf4j
public class ApiClient {
    private final RestTemplate restTemplate;
    private final ServiceConfig serviceConfig;

    boolean call(TransactionPhase transactionPhase, OrderRequest orderRequest){

       try {
           String endpoint = switch (transactionPhase) {
               case PREPARE -> serviceConfig.getPrepareEndpoint();
               case COMMIT -> serviceConfig.getCommitEndpoint();
               case ROLLBACK -> serviceConfig.getRollbackEndpoint();
           };

           String url = String.format("http://%s:%d%s", serviceConfig.getHost(), serviceConfig.getPort(), endpoint);
           ResponseEntity<String> response = restTemplate.postForEntity(url, orderRequest, String.class);
           log.info("[{}] {} phase response: {}", serviceConfig.getServiceName(), transactionPhase, response.getStatusCode());
           return response.getStatusCode().is2xxSuccessful();
       }catch (Exception exception){
           log.error("[{}] {} phase failed: {}", serviceConfig.getServiceName(), transactionPhase, exception.getMessage());
           return false;
       }
    }
}
