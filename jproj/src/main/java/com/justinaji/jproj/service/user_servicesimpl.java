package com.justinaji.jproj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.justinaji.jproj.exception.invaliduser;
import com.justinaji.jproj.model.CurrentUser;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.UserRepo;

@Service
public class user_servicesimpl implements user_services {

    private final UserRepo urepo;
    public user_servicesimpl(UserRepo urepo) {
        this.urepo = urepo; 
    }

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtservice;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);


/*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
CurrentUser user = (CurrentUser) auth.getPrincipal();
String uid = user.getUser().getU_id();*/  //to fetch userid 


//public static boolean loggedin = false ;
//public static String current_user = "";

    @Override
    public String RegisterUser(users user) {
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
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        urepo.save(user);
        return "New user '" + user.getEmail() + "' registered successfully";
    }


    @Override
    public String login(users user) {
        Authentication authentication = 
            authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
        
            return jwtservice.gentoken(user.getEmail());
    }


}
