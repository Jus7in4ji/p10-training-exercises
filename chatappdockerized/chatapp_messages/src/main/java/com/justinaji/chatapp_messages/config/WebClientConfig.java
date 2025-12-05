package com.justinaji.chatapp_messages.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig{

    @Value("${userchats.service.url}")
    private String userchatsServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(userchatsServiceUrl)
            .build();
    }
}