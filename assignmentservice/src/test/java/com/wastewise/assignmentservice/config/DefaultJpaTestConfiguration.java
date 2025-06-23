package com.wastewise.assignmentservice.config;

import java.util.Optional;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class DefaultJpaTestConfiguration {
    // You can add AuditorAware bean here if needed
	 @Bean
	    public AuditorAware<String> auditorProvider() {
	        return () -> Optional.of("test-user");
	    }
}
