package com.justinaji.jproj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.justinaji.jproj.model.WSmessage;
import com.justinaji.jproj.service.JWTService;
import com.justinaji.jproj.service.message_servicesimpl;

@Controller
public class WSChatcontroller {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final message_servicesimpl msgservice;
    public WSChatcontroller(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(WSmessage message) {

        // Reject if token is missing
        if (message.getToken() == null || message.getToken().trim().isEmpty()) {
            return; // do not send message
        }

        String username;
        try {
            username = jwtService.extractUser(message.getToken());
        } catch (Exception e) {
            return; // invalid token ⇒ reject message
        }
        // Override sender name with JWT username
        message.setFrom(username);
        String currentTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy | hh:mm:ss a"));
        message.setSentTime(currentTime);


        String room = message.getRoom();
        if (!room.equals("public")) {
            messagingTemplate.convertAndSend("/topic/" + room, message); // send only if roomid is valid
            msgservice.Sendmessage(message.getText(), username, room); 
        }

        
    }
}
