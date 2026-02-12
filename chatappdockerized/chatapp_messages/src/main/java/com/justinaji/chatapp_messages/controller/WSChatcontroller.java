package com.justinaji.chatapp_messages.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.justinaji.chatapp_messages.dto.ReadRequest;
import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.service.MessageServicesImpl;
import com.justinaji.chatapp_messages.service.SseService;

@Controller
public class WSChatcontroller {
    
    Logger logger = LoggerFactory.getLogger(WSChatcontroller.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageServicesImpl msgservice;
    private final SseService sseService;

    public WSChatcontroller(
        MessageServicesImpl msgservice, 
        SimpMessagingTemplate messagingTemplate,
        SseService sseService){
        this.msgservice = msgservice;
        this.sseService = sseService;
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
        switch (room){
            case null-> logger.error("no room passed");
            default ->  {
                //save in messages table if text message
                if (!message.isIsfile()) message.setMsgid(msgservice.Sendmessage(message.getText(), username, room,null));
                messagingTemplate.convertAndSend("/topic/" + room, message); // send via websocket
                sseService.broadcast( "unread-update", room);
            }
        }
    }

    @MessageMapping("/chat.read")
    public void markRead(ReadRequest body) {
        String msgId = body.getMsgId();
        messagingTemplate.convertAndSend("/topic/read", msgId);
        msgservice.setread(msgId);
    }
}
