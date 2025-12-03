package com.justinaji.chatapp_userchats.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.chatapp_userchats.model.chats;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.ChatRepo;
import com.justinaji.chatapp_userchats.repository.UserRepo;
import com.justinaji.chatapp_userchats.service.JWTService;
import com.justinaji.chatapp_userchats.service.chat_servicesimpl;

import io.swagger.v3.oas.annotations.Hidden;

import org.springframework.web.bind.annotation.RequestMapping;

import com.justinaji.chatapp_userchats.service.CommonMethods;

@Hidden
@RestController
@RequestMapping("/userchat")
public class MessageCommunications {

    @Autowired
    private JWTService jwtService;

    private final UserRepo urepo;
    private final ChatRepo chatrepo ; 
    private final chat_servicesimpl chat_services;

    Logger logger = LoggerFactory.getLogger(MessageCommunications.class);

    public MessageCommunications(chat_servicesimpl chat_services, UserRepo urepo,ChatRepo chatrepo){
        this.chat_services = chat_services;
        this.urepo = urepo;
        this.chatrepo = chatrepo;
    }
//----------------------------------------------  Communication w/ frontend ( js->java )  ------------------------------------------------------------
    //user&chat
    @GetMapping("/isactive")
    public Map<String, String> getUsername() {
        Map<String, String> user = new HashMap<>();
        try {
            
            users u = CommonMethods.getCurrentUser();
            user.put("username", u.getName());
            user.put("active", u.isStatus()?"true":"false");

        } catch (Exception e) {
            user.put("username", "");
            user.put("active", "false");
            logger.error("unable to verify token");
        }
        return user;
    }

    // user&chat
    @PostMapping("/subscribe-room")
    public Map<String, String> subscribeRoom(@RequestBody Map<String, String> payload) {
        String room = payload.get("room");
        
        HashMap<String, String> result =  chat_services.ischatvalid(
            room, payload.get("user"), Boolean.parseBoolean(payload.get("isGroup")));

        if(result.get("Status").equals("Success")) result.put("Room",room);
        else result.put("Room","[None]");

        if(room==null){ 
            result.put("Status", "Disconnected from Chat");
            logger.info("user "+payload.get("user")+ " has been disconnected from the chat");
        }

        return result;
    }

//----------------------------------------------  Communcation w/ message microservices  ----------------------------------------------------------------

    @GetMapping("/getuser")
    public users getuserfromname(@RequestParam String username) {
        users u = urepo.findByName(username);
        return u;
    }
    
    @GetMapping("/getchat")
    public chats getchatfromid(@RequestParam String chatid) {
        chats c = chatrepo.findById(chatid).orElse(null);
        return c;
    }
    

}