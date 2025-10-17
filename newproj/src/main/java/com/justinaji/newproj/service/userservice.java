package com.justinaji.newproj.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.justinaji.newproj.dto.AdminDTO;
import com.justinaji.newproj.dto.UserDTO;
import com.justinaji.newproj.exception.NoUserFound;
import com.justinaji.newproj.model.users;

@Service
public class userservice {
    List<users> userslist = new ArrayList<>(Arrays.asList( //initialize sample data 
        new users(1,"u1@domain.com","ADMIN","pass1", true),
        new users(2,"u2@domain.com","Justin","asdf"),
        new users(3,"u3@domain.com","Fabin","wasd")));

    public static boolean loggedin = false;
    public static boolean isadmin = false;

    public List<?> getuserdets() {
        if (!loggedin) {
            throw new NoUserFound();
        }

        if (isadmin) {
            List<AdminDTO> adminDTOList = new ArrayList<>();
            for (users u : userslist) {
                AdminDTO dto = new AdminDTO( u.getId(), u.getName(), u.getEmail(), u.isAdmin() );
                adminDTOList.add(dto);
            }
            return adminDTOList;
        } else {
            List<UserDTO> userDTOList = new ArrayList<>();
            for (users u : userslist) {
                UserDTO dto = new UserDTO(u.getName(), u.getEmail());
                userDTOList.add(dto);
            }
            return userDTOList;
        }
    } 

    public String validateuser(String email , String pass){
        for (users u : userslist){
            if(u.getEmail().equals(email) && u.getPassword().equals(pass)){
                loggedin = true;
                if(u.isAdmin()) isadmin = true;
                return "user '"+email+"' has succesfully logged in " ;
            }
        }
        return null;
    }

    public String userRegister( users user){
        if (!loggedin){
            if (user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
                return "Email and password both must be filled";
            } 
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
