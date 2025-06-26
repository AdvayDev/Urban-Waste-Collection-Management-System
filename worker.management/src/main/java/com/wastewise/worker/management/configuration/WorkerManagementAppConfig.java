package com.wastewise.worker.management.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WorkerManagementAppConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}