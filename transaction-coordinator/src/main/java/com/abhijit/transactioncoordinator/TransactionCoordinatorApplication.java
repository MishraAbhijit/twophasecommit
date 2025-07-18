package com.abhijit.transactioncoordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TransactionCoordinatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionCoordinatorApplication.class, args);
	}

}
