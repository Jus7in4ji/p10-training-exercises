package com.justinaji.jproj.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.jproj.dto.messageDTO;
import com.justinaji.jproj.service.message_servicesimpl;

@RestController
public class MessageController {

    private final message_servicesimpl msgservice;
    public MessageController(message_servicesimpl msgservice){
        this.msgservice = msgservice;
    }

    @GetMapping("/messages")
        public List<messageDTO> getMessages(@RequestParam String chat) {
            return msgservice.Getmessages(chat);
        }
    
    @PostMapping("/messages")
        public List<messageDTO> SendMessage(@RequestParam String chat, @RequestBody String Message) {
            return msgservice.SendMessage(chat, Message);
        }

}
