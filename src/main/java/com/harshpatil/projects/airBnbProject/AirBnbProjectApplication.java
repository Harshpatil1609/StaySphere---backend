package com.harshpatil.projects.airBnbProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirBnbProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirBnbProjectApplication.class, args);
	}

}
