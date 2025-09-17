package com.fares7elsadek.syncspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableRetry
@EnableTransactionManagement
public class SyncspaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncspaceApplication.class, args);
	}

}
