package com.justinaji.chatapp_messages.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.UserRepo;
import com.justinaji.chatapp_messages.service.JWTService;
import com.justinaji.chatapp_messages.service.message_servicesimpl;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class Js2JavaRequestController {
    
    private final message_servicesimpl msgservice;
    public Js2JavaRequestController(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }
   
    @PostMapping("/gethistory")
    public List<WSmessage> getMethodName(@RequestBody Map<String, String> payload) {   
        return msgservice.getchathistory(payload.get("user"), payload.get("chatid"), payload.get("timezone"));
    }

}
