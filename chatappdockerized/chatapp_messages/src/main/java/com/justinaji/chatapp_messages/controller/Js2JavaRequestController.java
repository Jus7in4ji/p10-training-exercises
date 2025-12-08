package com.justinaji.chatapp_messages.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.service.MessageServicesImpl;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
@RequestMapping("/msg")
public class Js2JavaRequestController {
    
    private final MessageServicesImpl msgservice;
    public Js2JavaRequestController(MessageServicesImpl msgservice){
        this.msgservice = msgservice;
    }
   
    @PostMapping("/gethistory")
    public List<WSmessage> RetrieveChatHistory(@RequestBody Map<String, String> payload) {   
        return msgservice.getchathistory(payload.get("user"), payload.get("chatid"), payload.get("timezone"));
    }

}
