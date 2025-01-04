package com.spoony.spoony_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpoonyServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpoonyServerApplication.class, args);
	}

}
