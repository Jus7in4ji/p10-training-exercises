package com.justinaji.spring4jdk17test.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.justinaji.spring4jdk17test.service.UserChatService;


@RestController
public class newRestClientController {

    private final UserChatService UserChats;
    
    public newRestClientController(UserChatService UserChats) {
        this.UserChats = UserChats;
    }

    @GetMapping("/getchats")
    public ResponseEntity<?> getchatdetails(@RequestParam String chatid) {
        return ResponseEntity.ok(UserChats.getChat(chatid));
    }
    
    @GetMapping("/getuser")
    public ResponseEntity<?> getUserDetailsfromName(@RequestParam String username) {
        return ResponseEntity.ok(UserChats.getUser(username));
    }
}
