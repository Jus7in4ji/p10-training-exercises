package com.justinaji.jproj.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.justinaji.jproj.exception.invaliduser;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.service.user_servicesimpl;


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
    
}

