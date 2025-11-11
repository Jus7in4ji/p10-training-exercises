package com.justinaji.jproj.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.jproj.dto.Messagerequest;
import com.justinaji.jproj.dto.messageDTO;
import com.justinaji.jproj.service.message_servicesimpl;

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
            return msgservice.SendPvtMessage(receiver, Message.getMessage());
        }
    
    @GetMapping("/groupchat")
        public List<messageDTO> getGrpMessages(@RequestParam String groupname) {
            return msgservice.GetGrpmessages(groupname);
        }
    
    @PostMapping("/groupchat")
        public List<messageDTO> SendGrpMessage(@RequestParam String groupname, @RequestBody Messagerequest Message) {
            return msgservice.SendGrpMessage(groupname, Message.getMessage());
        }

}
