package com.justinaji.spring4jdk17test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

import com.justinaji.spring4jdk17test.service.UserChatService;

@Configuration
@ImportHttpServices(UserChatService.class)
public class HttpConfig {

}
