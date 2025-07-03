package com.wastewise.assignmentservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class AssignmentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentserviceApplication.class, args);
	}
	// Register ModelMapper as a Spring Bean
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
