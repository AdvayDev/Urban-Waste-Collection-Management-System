// src/main/java/com/wastewise/api_gateway/config/CorsConfig.java
package com.wastewise.api_gateway.config; // Adjust package as per your project structure

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // For Spring WebFlux (if your API Gateway is a reactive gateway)
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:5173"); // Allow your frontend origin
        corsConfig.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, PUT, DELETE, etc.)
        corsConfig.addAllowedHeader("*"); // Allow all headers
        corsConfig.setAllowCredentials(true); // Allow sending cookies/auth headers
        corsConfig.setMaxAge(3600L); // How long the pre-flight request can be cached

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply CORS to all paths

        return new CorsWebFilter(source);
    }

    // For Spring Web MVC (if your API Gateway is a traditional MVC application or just a basic filter)
    // You might need this if you're not using WebFlux or if the above doesn't fully cover it.
    // However, for a Spring Cloud Gateway (which is WebFlux-based), the CorsWebFilter is usually sufficient.
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to all paths
                        .allowedOrigins("http://localhost:5173") // Allow your frontend origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Allowed methods
                        .allowedHeaders("*") // Allowed headers
                        .allowCredentials(true) // Allow sending cookies/auth headers
                        .maxAge(3600); // Max age for pre-flight cache
            }
        };
    }
}