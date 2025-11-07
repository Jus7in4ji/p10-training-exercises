package com.justinaji.jproj.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.justinaji.jproj.exception.Username_taken;
import com.justinaji.jproj.model.chats;
import com.justinaji.jproj.model.members;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.ChatRepo;
import com.justinaji.jproj.repository.MemberRepo;
import com.justinaji.jproj.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class user_servicesimpl implements user_services {

    private final UserRepo urepo;
    private final MemberRepo memberRepo;
    private final ChatRepo chatRepo;
    public user_servicesimpl(UserRepo urepo, MemberRepo memberRepo, ChatRepo chatRepo) {
        this.urepo = urepo; 
        this.memberRepo = memberRepo;
        this.chatRepo = chatRepo;
    }

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtservice;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

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

        return "New user '" + user.getEmail() + "' registered successfully";
    }

    @Override
    public String login(users user) {
        Authentication authentication = 
            authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(),user.getPassword()));
        
            return jwtservice.gentoken(user.getName());
    }

}
