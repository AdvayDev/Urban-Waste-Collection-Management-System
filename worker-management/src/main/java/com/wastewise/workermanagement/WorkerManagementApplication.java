package com.wastewise.workermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WorkerManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerManagementApplication.class, args);
	}

}
