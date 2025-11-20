package com.justinaji.jproj.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import com.justinaji.jproj.dto.Loginform;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.service.JWTService;
import com.justinaji.jproj.service.user_servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Hidden;



@RestController
public class UserController {
    
    @Autowired
    private JWTService jwtService; 

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

    @Hidden
    @GetMapping("/getusername")
    public Map<String, String> getUsername(@RequestParam String token) {
        try {
            String username = jwtService.extractUser(token);
            return Map.of("username", username);
        } catch (Exception e) {
            return Map.of("username", "");
        }
    }

    
}

