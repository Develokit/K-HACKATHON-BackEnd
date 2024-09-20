package com.develokit.maeum_ieum;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
//@EnableBatchProcessing
public class MaeumIeumApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaeumIeumApplication.class, args);
	}

}
