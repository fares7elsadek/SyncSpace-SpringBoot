package com.fares7elsadek.syncspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SyncspaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncspaceApplication.class, args);
	}

}
