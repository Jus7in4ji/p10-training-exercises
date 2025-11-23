package com.justinaji.jproj.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.jproj.dto.messageDTO;
import com.justinaji.jproj.service.JWTService;
import com.justinaji.jproj.service.message_servicesimpl;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class Js2JavaRequestController {

    @Autowired
    private JWTService jwtService;

    private final message_servicesimpl msgservice;
    public Js2JavaRequestController(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }

    @GetMapping("/getusername")
    public Map<String, String> getUsername(@RequestParam String token) {
        try {
            String username = jwtService.extractUser(token);
            return Map.of("username", username);
        } catch (Exception e) {
            return Map.of("username", "");
        }
    }

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

    @PostMapping("/gethistory")
    public List<messageDTO> getMethodName(@RequestBody Map<String, String> payload) {   
        return msgservice.getchathistory(payload.get("user"), payload.get("chatid"), payload.get("timezone"));
    }
}
