package com.justinaji.jproj.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.justinaji.jproj.model.WSmessage;
import com.justinaji.jproj.service.message_servicesimpl;

@Controller
public class WSChatcontroller {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final message_servicesimpl msgservice;
    public WSChatcontroller(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(WSmessage message) {
        String username = message.getFrom();
        // Reject if username is missing
        if (username == null || username.trim().isEmpty()) {
            return; // do not send message
        }

        String timezone = message.getSentTime();
        ZoneId zone;

        try {
            zone = (timezone == null || timezone.trim().isEmpty())
                    ? ZoneId.of("UTC")
                    : ZoneId.of(timezone);
        } catch (Exception e) {
            zone = ZoneId.of("UTC"); // fallback
        }

        // Take the instant NOW in UTC, shift to given timezone
        String finalTime = Instant.now()
                .atZone(zone)
                .format(DateTimeFormatter.ofPattern("hh:mm:ss a"));

        message.setSentTime(finalTime);

        String room = message.getRoom();
        if (room!= null) {
            messagingTemplate.convertAndSend("/topic/" + room, message); // send only if roomid is valid
            msgservice.Sendmessage(message.getText(), username, room); 
        }

        
    }
}
