package com.maturity.models.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.maturity.models.api.repository")
@EntityScan(basePackages = "com.maturity.models.api.model")
public class MaturityModelsApi {

	public static void main(String[] args) {
		SpringApplication.run(MaturityModelsApi.class, args);
	}

}
