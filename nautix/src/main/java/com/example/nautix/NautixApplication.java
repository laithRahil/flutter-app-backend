package com.example.nautix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NautixApplication {

	public static void main(String[] args) {
		SpringApplication.run(NautixApplication.class, args);
	}

}
