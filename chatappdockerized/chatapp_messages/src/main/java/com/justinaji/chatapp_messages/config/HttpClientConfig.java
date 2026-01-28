package com.justinaji.chatapp_messages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.justinaji.chatapp_messages.service.RestClientTest;

@Configuration
public class HttpClientConfig {
    
    @Bean
    public RestClientTest serChatsClient() {
        RestClient restClient = RestClient.builder()
            .baseUrl("http://your-service-url")
            .build();
        
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        
        return factory.createClient(RestClientTest.class);
    }
}