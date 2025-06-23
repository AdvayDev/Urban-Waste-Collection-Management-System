package com.wastewise.zoneservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for Zone Service.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI zoneServiceOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("WasteWise Zone Service API")
                        .description("APIs for managing zones in WasteWise system")
                        .version("1.0.0"));
    }
}
