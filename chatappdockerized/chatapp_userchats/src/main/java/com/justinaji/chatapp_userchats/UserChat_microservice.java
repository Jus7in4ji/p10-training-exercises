package com.justinaji.chatapp_userchats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UserChat_microservice {
	public static void main(String[] args) {
		SpringApplication.run(UserChat_microservice.class, args);
	}

}
