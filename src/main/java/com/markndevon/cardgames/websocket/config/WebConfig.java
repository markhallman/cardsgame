package com.markndevon.cardgames.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow all paths
                        .allowedOrigins("http://localhost:5173") // Allow only your frontend's origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specify HTTP methods
                        .allowedHeaders("*") // Allow any headers
                        .allowCredentials(true); // Allow credentials if necessary
            }
        };
    }
}