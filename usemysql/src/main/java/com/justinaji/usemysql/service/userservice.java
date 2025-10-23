package com.justinaji.usemysql.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.justinaji.usemysql.dto.AdminDTO;
import com.justinaji.usemysql.dto.UserDTO;
import com.justinaji.usemysql.exception.NoUserFound;
import com.justinaji.usemysql.model.users;
import com.justinaji.usemysql.repo.repouser;

@Service
public class userservice {

    private final repouser urepo;
    public userservice(repouser urepo) {
        this.urepo = urepo;
    }

    public static boolean loggedin = false ,isadmin = false;
    public static String current_user = "";

    public List<?> getuserdets() {
        if (!loggedin) throw new NoUserFound();  
        List<users> userslist = urepo.findAll(); 
        if (isadmin) {
            return userslist.stream()
                    .map(u -> new AdminDTO(u.getId(), u.getName(), u.getEmail(), u.isAdmin()))
                    .collect(Collectors.toList());
        } else {
            return userslist.stream()
                    .map(u -> new UserDTO(u.getName(), u.getEmail()))
                    .collect(Collectors.toList());
        }
    }

    public String validateuser(String email, String pass) {
        return urepo.findAll().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(pass))
                .findFirst()
                .map(u -> {
                    loggedin = true;
                    isadmin = u.isAdmin()? true: false ;
                    current_user = u.getId();
                    return "user '" + email + "' has successfully logged in";
                })
                .orElse(null);
    }

    public String userRegister(users user) {
        if (loggedin) return "you must log out to register a new user";
        if (user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Email and password both must be filled";
        }
        if (urepo.existsByEmail(user.getEmail())) return "User already Exists";
        
        if (!user.isAdmin()) user.setAdmin(false);

        String randomId;
        do {
            randomId = CommonMethods.getAlphaNumericString();
        } while (urepo.existsById(randomId));
        user.setId(randomId);
        urepo.saveAndFlush(user);
        loggedin = true;
        if (user.isAdmin()) isadmin = true;
        current_user = user.getId();

        return "New user '" + user.getEmail() + "' registered successfully";
    }

    public void logoutuser(){
        loggedin = isadmin = false;
        current_user = "";
    }
}
