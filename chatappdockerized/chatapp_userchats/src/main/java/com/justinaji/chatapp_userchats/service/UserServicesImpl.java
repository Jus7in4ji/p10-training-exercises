package com.justinaji.chatapp_userchats.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.justinaji.chatapp_userchats.dto.SignUp;
import com.justinaji.chatapp_userchats.exception.Username_taken;
import com.justinaji.chatapp_userchats.model.Logs;
import com.justinaji.chatapp_userchats.model.chats;
import com.justinaji.chatapp_userchats.model.members;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.ChatRepo;
import com.justinaji.chatapp_userchats.repository.LogRepo;
import com.justinaji.chatapp_userchats.repository.MemberRepo;
import com.justinaji.chatapp_userchats.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class UserServicesImpl implements UserServices {

    private final UserRepo urepo;
    private final MemberRepo memberRepo;
    private final ChatRepo chatRepo;
    private final LogRepo logrepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtservice;
    
    public UserServicesImpl(UserRepo urepo, MemberRepo memberRepo, ChatRepo chatRepo, LogRepo logrepo, AuthenticationManager authManager, JWTService jwtservice) {
        this.urepo = urepo; 
        this.memberRepo = memberRepo;
        this.chatRepo = chatRepo;
        this.logrepo = logrepo;
        this.authManager = authManager;
        this.jwtservice = jwtservice;
    }


    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    Logger logger = LoggerFactory.getLogger(UserServicesImpl.class);

    @Override
    @Transactional
    public String RegisterUser(SignUp signUp) {
        String name , email , password;
        name = signUp.getUsername();
        email = signUp.getEmail();
        password = signUp.getPassword();

        if (email == null || email.isEmpty() ||
            signUp.getPassword() == null || signUp.getPassword().isEmpty()) {
            return "Email and password both must be filled";
        }
        if (urepo.existsByEmail(email)) return "User with the given email id already Exists";
        if (chatRepo.existsByName(name)|| urepo.existsByName(name)) throw new Username_taken();

        String randomId;
        do { randomId = CommonMethods.getAlphaNumericString(); } 
        while (urepo.existsById(randomId));
        users user = new users();

        user.setEmail(email);
        user.setName(name);
        user.setUserId(randomId);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        users savedUser = urepo.save(user);// save user w new id and encrypted password

        List<users> allUsers = urepo.findAll();
        allUsers.stream()
            .filter(otherUser-> !otherUser.getUserId().equals(savedUser.getUserId()))
            .forEach(otherUser->{
        
                String chatId;
                do { chatId = CommonMethods.getAlphaNumericString(); } 
                while (chatRepo.existsById(chatId));

                chats chat = new chats(); //new 1-1 chat 
                chat.setChatId(chatId);
                chat.setChat_key(CommonMethods.generateKey());
                chatRepo.save(chat);

                members m1 = new members(); //register both(savedUser & otherUser) as members of 1-1 chat
                m1.setChat(chat);
                m1.setMember(savedUser);
                memberRepo.save(m1);

                members m2 = new members();
                m2.setChat(chat);
                m2.setMember(otherUser);
                memberRepo.save(m2);
            });
        String logid;
            
        do { logid = CommonMethods.getAlphaNumericString(); } 
        while (logrepo.existsById(logid));

        Logs l = new Logs(logid, "Sign up", "New User '"+name+"' has Signed up.", new Timestamp(System.currentTimeMillis()), user);
        logrepo.save(l);
        logger.info("user "+name+" registered ");
        return "New user '" + email + "' registered successfully";
    }

    @Override
    public String login(String username, String password) {
        Authentication authentication = 
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));

            users u = urepo.findByName(username);
            u.setRecentLogin(LocalDateTime.now());
            u.setStatus(true);
            urepo.save(u);
            
            String logid;
            do { logid = CommonMethods.getAlphaNumericString(); } 
            while (logrepo.existsById(logid));

            Logs l = new Logs(logid, "Login", "User '"+username+"' has logged in", new Timestamp(System.currentTimeMillis()), urepo.findByName(username));
            logrepo.save(l);
            logger.info("user "+username+" logged in. ");

            return jwtservice.gentoken(username);
    }

}
