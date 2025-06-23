package com.wastewise.routeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration for API documentation.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI routeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Route Service API")
                        .description("API documentation for the Route Service")
                        .version("v1.0"));
    }
}
