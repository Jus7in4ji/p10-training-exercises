package com.justinaji.jproj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.justinaji.jproj.model.WSmessage;
import com.justinaji.jproj.service.JWTService;

@Controller
public class WSChatcontroller {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(WSmessage message) {

        // 1️⃣ Reject if token is missing
        if (message.getToken() == null || message.getToken().trim().isEmpty()) {
            return; // do not send message
        }

        String username;
        try {
            username = jwtService.extractUser(message.getToken());
        } catch (Exception e) {
            return; // invalid token ⇒ reject message
        }

        // 2️⃣ Override sender name with JWT username
        message.setFrom(username);

        // 3️⃣ Add timestamp
        String currentTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy | hh:mm:ss a"));

        message.setSentTime(currentTime);

        // 4️⃣ Determine chat room (default = "public")
        String room = message.getRoom();
        if (room == null || room.trim().isEmpty()) {
            room = "public";
        }

        // 5️⃣ Send to correct topic
        messagingTemplate.convertAndSend("/topic/" + room, message);
    }
}
