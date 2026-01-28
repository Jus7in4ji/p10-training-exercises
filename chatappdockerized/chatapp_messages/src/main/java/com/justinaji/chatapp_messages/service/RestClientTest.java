package com.justinaji.chatapp_messages.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.users;

@HttpExchange(url = "http://localhost:8082", accept = "application/json")
public interface RestClientTest {

    @GetExchange("/userchat/getchat")
    chats getChat(@RequestParam("chatid") String chatid);

    @GetExchange("/userchat/getuser")
    users getUser(@RequestParam("username") String username);
    
}

