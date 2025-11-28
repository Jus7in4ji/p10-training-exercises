package com.justinaji.chatapp_userchats.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
public class user_servicesimpl implements user_services {

    private final UserRepo urepo;
    private final MemberRepo memberRepo;
    private final ChatRepo chatRepo;
    private final LogRepo logrepo;
    
    public user_servicesimpl(UserRepo urepo, MemberRepo memberRepo, ChatRepo chatRepo, LogRepo logrepo) {
        this.urepo = urepo; 
        this.memberRepo = memberRepo;
        this.chatRepo = chatRepo;
        this.logrepo = logrepo;
    }

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtservice;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    Logger logger = LoggerFactory.getLogger(user_servicesimpl.class);

    @Override
    @Transactional
    public String RegisterUser(users user) {

        if (user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Email and password both must be filled";
        }
        if (urepo.existsByEmail(user.getEmail())) return "User with the given email id already Exists";
        if (chatRepo.existsByName(user.getName())|| urepo.existsByName(user.getName())) throw new Username_taken();

        String randomId;
        do { randomId = CommonMethods.getAlphaNumericString(); } 
        while (urepo.existsById(randomId));

        user.setU_id(randomId);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        users savedUser = urepo.save(user);// save user w new id and encrypted password

        List<users> allUsers = urepo.findAll();
        allUsers.stream()
            .filter(otherUser-> !otherUser.getU_id().equals(savedUser.getU_id()))
            .forEach(otherUser->{
        
                String chatId;
                do { chatId = CommonMethods.getAlphaNumericString(); } 
                while (chatRepo.existsById(chatId));

                chats chat = new chats(); //new 1-1 chat 
                chat.setC_id(chatId);
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

        Logs l = new Logs(logid, "Sign up", "New User '"+user.getName()+"' has Signed up.", new Timestamp(System.currentTimeMillis()), user);
        logrepo.save(l);
        logger.info("user "+user.getName()+" registered ");
        return "New user '" + user.getEmail() + "' registered successfully";
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
