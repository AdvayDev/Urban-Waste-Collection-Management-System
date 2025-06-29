package com.wastewise.pickup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PickUpApplication {
	public static void main(String[] args) {
		SpringApplication.run(PickUpApplication.class, args);
	}
}