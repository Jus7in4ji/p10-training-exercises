package com.justinaji.chatapp_messages.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.justinaji.chatapp_messages.dto.ReadRequest;
import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.service.MessageServicesImpl;

@Controller
public class WSChatcontroller {
    
    Logger logger = LoggerFactory.getLogger(WSChatcontroller.class);

    private  final SimpMessagingTemplate messagingTemplate;

    private final MessageServicesImpl msgservice;
    public WSChatcontroller(MessageServicesImpl msgservice, SimpMessagingTemplate messagingTemplate){
        this.msgservice = msgservice;
        this.messagingTemplate = messagingTemplate;
    }
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(WSmessage message) {
        String username = message.getFrom();
        // Reject if username is missing
        if (username == null || username.trim().isEmpty()) {
            logger.error("no username passed");
            return; // do not send message
        }

        String room = message.getRoom();
        if (room!= null) {
            if (!message.isIsfile()) message.setMsgid(msgservice.Sendmessage(message.getText(), username, room));//needs chat, user objects from other ms
            messagingTemplate.convertAndSend("/topic/" + room, message); // send only if roomid is valid
        }
        else {
            logger.error("no room passed");
        }
        
        
    }

    @MessageMapping("/chat.read")
    public void markRead(ReadRequest body) {
        String msgId = body.getMsgId();
        messagingTemplate.convertAndSend("/topic/read", msgId);
        msgservice.setread(msgId);
    }
}
