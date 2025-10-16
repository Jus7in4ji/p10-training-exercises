package com.justinaji.depinj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class DepinjApplication {

	public static void main(String[] args) {
		

		ApplicationContext appcont = SpringApplication.run(DepinjApplication.class, args); 
		//creates container when run 
		country s = appcont.getBean(country.class);
		System.out.println("Location:");
		s.action();
	}

}
