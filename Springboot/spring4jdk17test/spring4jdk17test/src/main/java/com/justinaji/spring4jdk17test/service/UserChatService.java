package com.justinaji.spring4jdk17test.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.justinaji.spring4jdk17test.models.chats;
import com.justinaji.spring4jdk17test.models.users;

@HttpExchange(url = "http://localhost:8082", accept = "application/json")
public interface UserChatService {

    @GetExchange("/userchat/getchat")
    chats getChat(@RequestParam("chatid") String chatid);

    @GetExchange("/userchat/getuser")
    users getUser(@RequestParam("username") String username);
    
}
