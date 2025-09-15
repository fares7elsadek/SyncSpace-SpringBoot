package com.fares7elsadek.syncspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableJpaAuditing
@EnableRetry
public class SyncspaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncspaceApplication.class, args);
	}

}
