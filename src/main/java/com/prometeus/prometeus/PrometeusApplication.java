package com.prometeus.prometeus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PrometeusApplication {
	public static void main(String[] args) {
		SpringApplication.run(PrometeusApplication.class, args);
	}
}