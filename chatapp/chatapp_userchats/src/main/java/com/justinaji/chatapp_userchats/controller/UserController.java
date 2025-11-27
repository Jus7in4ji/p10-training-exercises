package com.justinaji.chatapp_userchats.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import com.justinaji.chatapp_userchats.dto.Loginform;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.service.user_servicesimpl;



@RestController
public class UserController { 

    private final user_servicesimpl userservice;
    public UserController(user_servicesimpl userservice) {// Constructor injection
        this.userservice = userservice;
    }

    @PostMapping("/SignUp")
    public String signup( @RequestBody users user) {
        return userservice.RegisterUser(user);
    }
    
    @PostMapping("/login")
    public String login(@RequestBody Loginform user) {
        
        return userservice.login(user.getUsername(),user.getPassword());
    }
    
}

