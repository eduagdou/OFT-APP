package com.example.oftbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class InsforgeConfig {

    @Bean
    public RestTemplate insforgeRestTemplate() {
        return new RestTemplate();
    }
}
