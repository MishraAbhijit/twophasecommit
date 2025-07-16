package com.abhijit.transactioncoordinator.config.apis;

import lombok.Data;

@Data
public class ServiceEndpoints {
    private String prepare;
    private String commit;
    private String rollback;
}