package com.justinaji.chatapp_messages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Message_microservice {

	public static void main(String[] args) {
		SpringApplication.run(Message_microservice.class, args);
	}

	@Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }
}
