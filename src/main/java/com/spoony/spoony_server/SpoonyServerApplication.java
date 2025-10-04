package com.spoony.spoony_server;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.TimeZone;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpoonyServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpoonyServerApplication.class, args);
	}

}
