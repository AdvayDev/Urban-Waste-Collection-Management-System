package com.wastewise.zoneservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot application class to start the Zone Service.
 */
@SpringBootApplication
@EnableDiscoveryClient // Registers service with Eureka
@EnableFeignClients(basePackages = "com.wastewise.zoneservice.client") // Enables Feign clients
@EnableJpaAuditing
public class ZoneServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZoneServiceApplication.class, args);
    }
}
