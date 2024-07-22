package com.develokit.maeum_ieum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MaeumIeumApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaeumIeumApplication.class, args);
	}

}
