package com.justinaji.spring4jdk17test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.spring4jdk17test.service.UserChatService;


@RestController
@RequestMapping("/api")
public class Controller {

    private final UserChatService UserChats;
    
    public Controller(UserChatService UserChats) {
        this.UserChats = UserChats;
    }

    @GetMapping(value = "/{version}/getchats", version = "1.0")
    public ResponseEntity<?> getchatdetails(@RequestParam String chatid) {
        return ResponseEntity.ok(UserChats.getChat(chatid));
    }
    
    @GetMapping( "/v1/getchats")
    public ResponseEntity<?> getchatdetail(@RequestParam String chatid) {
        return ResponseEntity.ok(UserChats.getChat(chatid));
    }

    @GetMapping(value = "/{version}/getuser", version = "1.0")
    public ResponseEntity<?> getUserDetailsfromName(@RequestParam String username) {
        return ResponseEntity.ok(UserChats.getUser(username));
    }

    @GetMapping(value = "/{version}/apiversions", version = "1.0")
    public String callv1() {
        return "request from v1";
    }

    @GetMapping(value = "/{version}/apiversions", version = "2.0")
    public String callv2() {
        return "request from v2";
    }
    
}
