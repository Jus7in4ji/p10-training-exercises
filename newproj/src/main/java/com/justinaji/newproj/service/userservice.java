package com.justinaji.newproj.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import com.justinaji.newproj.model.users;

@Service
public class userservice {
    List<users> userslist = new ArrayList<>(Arrays.asList( //initialize sample data 
        new users(1,"u1@domain.com","pass1"),
        new users(2,"u2@domain.com","asdf"),
        new users(3,"u3@domain.com","wasd")));
    public static boolean loggedin = false;
    public List<users> getuserdets(){ 
        if(loggedin)return userslist;
        return null; } 

    public String validateuser(String email , String pass){
        for (users u : userslist){
            if(u.getEmail().equals(email) && u.getPassword().equals(pass)){
                loggedin = true;
                return "user '"+email+"' has succesfully logged in " ;
            }
        }
        return null;

    }
    public String userRegister( users user){
        if (!loggedin){
            int newid =0;
            for (users u : userslist){
                if(newid <=u.id) newid = u.id+1;
            }
            user.id = newid;
            userslist.add(user);
            loggedin = true;
            return "New user '"+user.email +"' registered successfully";}
        return "you must Log out to register a new user"; 
    }
}
