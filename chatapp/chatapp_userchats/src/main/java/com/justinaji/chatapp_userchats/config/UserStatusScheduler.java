package com.justinaji.chatapp_userchats.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.UserRepo;

@Component
public class UserStatusScheduler {

    @Autowired
    private UserRepo urepo;

    @Scheduled(fixedRate = 60000) // run every 60 seconds
    public void autoDeactivateUsers() {
        System.out.println("Scheduler triggered at: " + LocalDateTime.now());

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        List<users> toDeactivate = urepo.findByStatusTrueAndRecentLoginBefore(cutoff);

        System.out.println("Users to deactivate: " + toDeactivate.size());
        
        toDeactivate.forEach(u -> {
            u.setStatus(false);
            urepo.save(u);
        });
    }
}

