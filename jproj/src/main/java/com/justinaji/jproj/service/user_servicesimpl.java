package com.justinaji.jproj.service;

import org.springframework.stereotype.Service;

import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.UserRepo;

@Service
public class user_servicesimpl implements user_services {

    private final UserRepo urepo;
    public user_servicesimpl(UserRepo urepo) {
        this.urepo = urepo; 
    }

    public static boolean loggedin = false ;
    public static String current_user = "";

    @Override
    public String RegisterUser(users user) {
        if (loggedin) return "you must log out to register a new user";
        if (user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Email and password both must be filled";
        }
        if (urepo.existsByEmail(user.getEmail())) return "User already Exists";
        
        String randomId;
        
        do {
            randomId = CommonMethods.getAlphaNumericString();
        } while (urepo.existsById(randomId));
        
        user.setU_id(randomId);
        user.setPassword(CommonMethods.encryptpassword(user.getPassword()));
        loggedin = true;
        current_user = randomId;

        urepo.save(user);
        return "New user '" + user.getEmail() + "' registered successfully";
    }

    @Override
    public String UserLogin(String email, String pass) {
        String hashedpass = CommonMethods.encryptpassword(pass); 
        return urepo.findAll().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(hashedpass))
                .findFirst()
                .map(u -> {
                    loggedin = true;
                    current_user = u.getU_id();
                    return "user '" + email + "' has successfully logged in";
                })
                .orElse(null);
    }

    @Override
    public void UserLogout() {
        loggedin = false;
        current_user = "";
    }

}
