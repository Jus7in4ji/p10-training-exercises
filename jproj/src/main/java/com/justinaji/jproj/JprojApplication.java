package com.justinaji.jproj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JprojApplication {

	public static void main(String[] args) {
		SpringApplication.run(JprojApplication.class, args);
	}

}
