package com.justinaji.chatapp_userchats.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.chatapp_userchats.dto.Loginform;
import com.justinaji.chatapp_userchats.dto.SignUp;
import com.justinaji.chatapp_userchats.service.UserServicesImpl;



@RestController
@RequestMapping("/auth")
public class UserController { 

    private final UserServicesImpl userservice;
    public UserController(UserServicesImpl userservice) {// Constructor injection
        this.userservice = userservice;
    }

    @PostMapping("/SignUp")
    public String signup( @RequestBody SignUp signUp) {
        return userservice.RegisterUser(signUp);
    }

    

    @PostMapping( "/login")
    public String login(@RequestBody Loginform user) {
        return userservice.login(user.getUsername(),user.getPassword());
    }
    
}

