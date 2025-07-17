package com.abhijit.transactioncoordinator.config.clients;

import com.abhijit.transactioncoordinator.config.apis.ServiceConfig;
import com.abhijit.transactioncoordinator.dto.OrderRequest;
import com.abhijit.transactioncoordinator.enums.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Slf4j
public class ApiClient {
    private final RestTemplate restTemplate;
    private final ServiceConfig serviceConfig;

    boolean call(Status status, OrderRequest orderRequest){

       try {
           String endpoint = switch (status) {
               case PREPARE -> serviceConfig.getPrepareEndpoint();
               case COMMIT -> serviceConfig.getCommitEndpoint();
               case ROLLBACK -> serviceConfig.getRollbackEndpoint();
           };

           String url = String.format("http://%s:%d%s", serviceConfig.getHost(), serviceConfig.getPort(), endpoint);
           ResponseEntity<String> response = restTemplate.postForEntity(url, orderRequest, String.class);
           log.info("[{}] {} phase response: {}", serviceConfig.getServiceName(), status, response.getStatusCode());
           return response.getStatusCode().is2xxSuccessful();
       }catch (Exception exception){
           log.error("[{}] {} phase failed: {}", serviceConfig.getServiceName(), status, exception.getMessage());
           return false;
       }
    }
}
