package com.justinaji.chatapp_messages.RestClientAPIs;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.users;

@HttpExchange(url = "${userchats.service.url}", accept = "application/json")
public interface Userchat {

    @GetExchange("/userchat/getchat")
    chats getChat(@RequestParam("chatid") String chatid);

    @GetExchange("/userchat/getuser")
    users getUser(@RequestParam("username") String username);
    
    @GetExchange("/userchat/availablechats")
    List<chats> getAvailableChats(@RequestParam("username") String username);
 }

