package com.wastewise.assignmentservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
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
