package com.justinaji.jproj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.justinaji.jproj.dto.ReadRequest;
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

        message.setMsgread(false);
        String room = message.getRoom();
        if (room!= null) {
            message.setMsgid(msgservice.Sendmessage(message.getText(), username, room));//needs chat, user objects from other ms
            messagingTemplate.convertAndSend("/topic/" + room, message); // send only if roomid is valid
            
        }
    }

    @MessageMapping("/chat.read")
    public void markRead(ReadRequest body) {
        String msgId = body.getMsgId();
        messagingTemplate.convertAndSend("/topic/read", msgId);
        msgservice.setread(msgId);
    }

}
