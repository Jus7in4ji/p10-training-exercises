package com.justinaji.jproj.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.justinaji.jproj.model.WSmessage;
 
@Controller
public class WSChatcontroller {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public WSmessage sendMessage(WSmessage message) {
        return message;
    }
}
