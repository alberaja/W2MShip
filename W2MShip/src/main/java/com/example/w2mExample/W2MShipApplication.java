package com.example.w2mExample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication (exclude = {SecurityAutoConfiguration.class})
public class W2MShipApplication {

	public static void main(String[] args) {
		SpringApplication.run(W2MShipApplication.class, args);
	}

}
