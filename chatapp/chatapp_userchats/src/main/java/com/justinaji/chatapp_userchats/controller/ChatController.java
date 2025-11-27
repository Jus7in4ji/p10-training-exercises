package com.justinaji.chatapp_userchats.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.justinaji.chatapp_userchats.service.chat_servicesimpl;
import com.justinaji.chatapp_userchats.dto.addmember;
import com.justinaji.chatapp_userchats.dto.chatdetails;
import com.justinaji.chatapp_userchats.dto.UserEditRequest;
import com.justinaji.chatapp_userchats.dto.UserChat;

import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/chats")
public class ChatController {

    private final chat_servicesimpl chatservice;

    public ChatController( chat_servicesimpl chatservice) { this.chatservice = chatservice; }

    @PostMapping("/create")
    public chatdetails postMethodName(@RequestBody addmember newGroup) {
        return chatservice.CreateChat(newGroup); 
    }
    
    @GetMapping("")
    public String getchats() {
        return chatservice.getChats();
    }
    
    @GetMapping("/members")
    public String getMethodName(@RequestParam String groupname) {
        return chatservice.getMembers(groupname);
    }
    

    @PostMapping("/addmember")
    public String Addmemembertochat(@RequestBody UserEditRequest request) {
        return chatservice.AddMember(request.getChatname(), request.getUsername(), request.isAdmin());
    }
    
    @PutMapping("/removemember")
    public String Removefromchat(@RequestBody UserChat request) {
        return chatservice.RemoveMember(request.getChatname(), request.getUsername());
    }

    @PutMapping("/leave")
    public String Leavechat(@RequestParam String chatname) {
        return chatservice.LeaveGroup(chatname);
    } 

    @PutMapping("/makeadmin")
    public String putMethodName(@RequestBody UserChat request) {
        return chatservice.Makeadmin(request.getChatname(), request.getUsername());
    }
}
