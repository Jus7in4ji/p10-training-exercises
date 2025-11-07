package com.justinaji.jproj.controller;

import org.springframework.web.bind.annotation.RestController;

import com.justinaji.jproj.service.chat_servicesimpl;
import com.justinaji.jproj.service.user_servicesimpl;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.justinaji.jproj.dto.addmember;
import com.justinaji.jproj.model.chats;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class ChatController {

    private final user_servicesimpl userservice;
    private final chat_servicesimpl chatservice;

    public ChatController(user_servicesimpl userservice, chat_servicesimpl chatservice) {// Constructor injection
        this.userservice = userservice;
        this.chatservice = chatservice;
    }

    @PostMapping("/Newchat/")
    public chats postMethodName(@RequestBody addmember newGroup) {
        return chatservice.CreateChat(newGroup);        
        
    }
    
    @GetMapping("/Chats")
    public String getchats() {
        return chatservice.getChats();
    }
    
}
