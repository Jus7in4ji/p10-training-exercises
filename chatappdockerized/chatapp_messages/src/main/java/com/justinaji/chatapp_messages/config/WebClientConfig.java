package com.justinaji.chatapp_messages.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${userchats.service.url}")
    private String userchatsServiceUrl;

    @Value("${media.service.url}")
    private String mediaServiceUrl;

    @Value("${tempmsg.service.url}")
    private String tempmsgServiceUrl;

    @Bean
    public WebClient userChatsWebClient() {
        return WebClient.builder()
                .baseUrl(userchatsServiceUrl)
                .build();
    }

    @Bean
    public WebClient MediaWebClient() {
        return WebClient.builder()
                .baseUrl(mediaServiceUrl)
                .build();
    }

    @Bean
    public WebClient TempMsgWebClient() {
        return WebClient.builder()
                .baseUrl(tempmsgServiceUrl)
                .build();
    }
}
