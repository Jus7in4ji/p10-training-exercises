package com.justinaji.chatapp_userchats.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.chatapp_userchats.dto.Messagerequest;
import com.justinaji.chatapp_userchats.dto.messageDTO;
import com.justinaji.chatapp_userchats.service.message_servicesimpl;

@RestController
public class MessageController {

    private final message_servicesimpl msgservice;
    public MessageController(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }

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

    
}


