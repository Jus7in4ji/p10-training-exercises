package com.justinaji.jproj.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.justinaji.jproj.service.chat_servicesimpl;
import com.justinaji.jproj.dto.addmember;
import com.justinaji.jproj.dto.chatdetails;
import com.justinaji.jproj.dto.UserEditRequest;
import com.justinaji.jproj.dto.UserChat;

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
    public String Leavechat(@RequestParam String request) {
        return chatservice.LeaveGroup(request);
    }

    @PutMapping("/makeadmin")
    public String putMethodName(@RequestBody UserChat request) {
        return chatservice.Makeadmin(request.getChatname(), request.getUsername());
    }
}
