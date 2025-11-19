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

import com.justinaji.jproj.dto.Messagerequest;
import com.justinaji.jproj.dto.messageDTO;
import com.justinaji.jproj.service.JWTService;
import com.justinaji.jproj.service.message_servicesimpl;

@RestController
public class MessageController {

    private final message_servicesimpl msgservice;
    public MessageController(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }

    @Autowired
    private JWTService jwtService; 

    @GetMapping("/privatechat")
    public List<messageDTO> getpvtMessages(@RequestParam String receiver) {
        return msgservice.GetPvtmessages(receiver);
    }
    
    @PostMapping("/privatechat")
    public List<messageDTO> SendpvtMessage(@RequestParam String receiver, @RequestBody Messagerequest Message) {
        msgservice.SendPvtMessage(receiver, Message.getMessage());
        return msgservice.GetPvtmessages(receiver);
    }
    
    @GetMapping("/groupchat")
    public List<messageDTO> getGrpMessages(@RequestParam String groupname) {
        return msgservice.GetGrpmessages(groupname);
    }
    
    @PostMapping("/groupchat")
    public List<messageDTO> SendGrpMessage(@RequestParam String groupname, @RequestBody Messagerequest Message) {
        msgservice.SendGrpMessage(groupname, Message.getMessage());
        return msgservice.GetGrpmessages(groupname);
    }

    @PostMapping("/subscribe-room")
    public Map<String, String> subscribeRoom(@RequestBody Map<String, String> payload) {

        String room = payload.get("room");
        String token = payload.get("token");
        boolean isGroup = Boolean.parseBoolean(payload.get("isGroup"));
        
        System.out.println("Room received from JS: " + room+"\nJWT received from JS: " + token+"\nisGroup: " + isGroup);
        
        String username="";
        try {
            username = jwtService.extractUser(token);
        } catch (Exception e){}

        HashMap<String, String> result =  msgservice.ischatvalid(room, username ,isGroup);

        

        if(result.get("Status").equals("Success")) result.put("Room",room);
        else result.put("Room","[None]");
        if(room.equals("public")) result.put("Status", "Disconnected from Chat");
        return result;
    }
}
