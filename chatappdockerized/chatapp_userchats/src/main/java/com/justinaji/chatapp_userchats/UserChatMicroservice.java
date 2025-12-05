package com.justinaji.chatapp_userchats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UserChatMicroservice {
	public static void main(String[] args) {
		SpringApplication.run(UserChatMicroservice.class, args);
	}

}
