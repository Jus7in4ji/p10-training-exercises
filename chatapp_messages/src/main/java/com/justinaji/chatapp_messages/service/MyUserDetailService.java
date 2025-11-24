package com.justinaji.chatapp_messages.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.justinaji.chatapp_messages.model.CurrentUser;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.UserRepo;

@Service
public class MyUserDetailService implements UserDetailsService{

    private final UserRepo urepo;
    public MyUserDetailService(UserRepo urepo) {
        this.urepo = urepo; 
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        users user = urepo.findByName(name);
        if(user ==null){
            throw new UsernameNotFoundException("User not Found");
        }
        return new CurrentUser(user);
    }


}
