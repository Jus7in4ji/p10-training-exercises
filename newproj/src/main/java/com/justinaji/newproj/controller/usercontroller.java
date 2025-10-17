package com.justinaji.newproj.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.justinaji.newproj.model.users;
import com.justinaji.newproj.service.userservice;
 

class invaliduser extends RuntimeException{
    public invaliduser(){
        super("User credentials are incorrect");
    }
}

@RestController
public class usercontroller {
    @Autowired
    userservice userservice;

    @GetMapping("/users")
    public List<users> userlist() {
        return userservice.getuserdets();
    }
    
    @PutMapping("/login")
    public String login(@RequestBody loginreq login) {
        String status = userservice.validateuser(login.getmail(),login.getPass());
        try{
            if (status==null)throw new invaliduser();
            else{return "Login Successfull";}
        }   
        catch (invaliduser i){ return i.getMessage(); }
    }

    @PostMapping("/signup")
    public String signup( @RequestBody users user) {
        return userservice.userRegister(user);
    }
    @GetMapping("/logout")
    public String logout() {
        userservice.loggedin = false;
        return "logged out";
    }
    
}
class loginreq{
    String email;
    String password; 
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public String getmail() { return email; }
    public String getPass() { return password; }
}
