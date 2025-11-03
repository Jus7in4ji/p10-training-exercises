package com.justinaji.jproj.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.justinaji.jproj.model.CurrentUser;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.UserRepo;

@Service
public class MyUserDetailService implements UserDetailsService{

    private final UserRepo urepo;
    public MyUserDetailService(UserRepo urepo) {
        this.urepo = urepo; 
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        users user = urepo.findByEmail(email);
        if(user ==null){
            throw new UsernameNotFoundException("User not Found");
        }
        return new CurrentUser(user);
    }


}
