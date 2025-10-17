package com.justinaji.newproj.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.justinaji.newproj.exception.invaliduser;
import com.justinaji.newproj.model.users;
import com.justinaji.newproj.service.userservice;


@RestController
public class usercontroller {
    
    private final userservice userservice;
    public usercontroller(userservice userservice) {// Constructor injection
        this.userservice = userservice;
    }

    @GetMapping("/users")
    public List<?> userlist() {
        return userservice.getuserdets();
    }
    
    @PutMapping("/login")
    public String login(@RequestBody users login) {
        String status = userservice.validateuser(login.getEmail(),login.getPassword());
        if (status==null)throw new invaliduser();
        else{return "Login Successfull";}
    }

    @PostMapping("/signup")
    public String signup( @RequestBody users user) {
        return userservice.userRegister(user);
    }
    @GetMapping("/logout")
    public String logout() {
        userservice.loggedin = userservice.isadmin = false;
        return "logged out";
    }
    
}
