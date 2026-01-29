package com.justinaji.chatapp_messages.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

import com.justinaji.chatapp_messages.RestClientAPIs.MediaDetails;
import com.justinaji.chatapp_messages.RestClientAPIs.TempMsgs;
import com.justinaji.chatapp_messages.RestClientAPIs.Userchat;

@Configuration
@ImportHttpServices(Userchat.class)
@ImportHttpServices(MediaDetails.class)
@ImportHttpServices(TempMsgs.class)
public class HttpClientConfig {
    
/*
   @Bean
    public RestClientTest serChatsClient() {
        RestClient restClient = RestClient.builder()
            .baseUrl("http://your-service-url")
            .build();
        
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        
        return factory.createClient(RestClientTest.class);
    }
*/
}