package com.wastewise.vehicleservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class VehicleserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleserviceApplication.class, args);
    }

    // Register ModelMapper as a Spring Bean
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
