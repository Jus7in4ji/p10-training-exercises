package com.justinaji.jproj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.justinaji.jproj.model.WSmessage;
import com.justinaji.jproj.service.JWTService;
 
@Controller
public class WSChatcontroller {

    @Autowired
    private JWTService jwtService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public WSmessage sendMessage(WSmessage message) {

        String username = "Random user";

        try {
            // Extract username from token
            String extracted = jwtService.extractUser(message.getToken());
            if (extracted != null) {
                username = extracted;
            }
        } catch (Exception e) {
            // token invalid → keep "Random user"
        }

        message.setFrom(username);
        return message;
    }
}
