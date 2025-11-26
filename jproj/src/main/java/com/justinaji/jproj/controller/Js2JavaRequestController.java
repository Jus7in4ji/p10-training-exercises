package com.justinaji.jproj.controller;

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

import com.justinaji.jproj.model.WSmessage;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.UserRepo;
import com.justinaji.jproj.service.JWTService;
import com.justinaji.jproj.service.message_servicesimpl;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class Js2JavaRequestController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepo urepo;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final message_servicesimpl msgservice;
    public Js2JavaRequestController(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }

    //user&chat
    @GetMapping("/getusername")
    public Map<String, String> getUsername(@RequestParam String token) {
        Map<String, String> user = new HashMap<>();
        try {
            String username = jwtService.extractUser(token);
            user.put("username", username);
            users u = urepo.findByName(username);
            user.put("active", u.isStatus()?"true":"false");

        } catch (Exception e) {
            user.put("username", "");
            user.put("active", "false");
        }
        return user;
    }

    // user&chat
    @PostMapping("/subscribe-room")
    public Map<String, String> subscribeRoom(@RequestBody Map<String, String> payload) {
        String room = payload.get("room");
        
        HashMap<String, String> result =  msgservice.ischatvalid(
            room, payload.get("user"), Boolean.parseBoolean(payload.get("isGroup")));

        if(result.get("Status").equals("Success")) result.put("Room",room);
        else result.put("Room","[None]");

        if(room==null) result.put("Status", "Disconnected from Chat");

        return result;
    }
    
    // messaging
    @PostMapping("/gethistory")
    public List<WSmessage> getMethodName(@RequestBody Map<String, String> payload) {   
        return msgservice.getchathistory(payload.get("user"), payload.get("chatid"), payload.get("timezone"));
    }


    // independent
    @PostMapping("/chat.read")
    public void markRead(@RequestBody Map<String, String> body) {
        String msgId = body.get("msgId");
        messagingTemplate.convertAndSend("/topic/read", msgId);
    }
}
