package com.justinaji.chatapp_userchats.config;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.UserRepo;

@Component
public class UserStatusScheduler {

    @Autowired
    private UserRepo urepo;

    Logger logger = LoggerFactory.getLogger(UserStatusScheduler.class);
    
    @Scheduled(fixedRate = 60000) // run every 60 seconds
    public void autoDeactivateUsers() {

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        List<users> toDeactivate = urepo.findByStatusTrueAndRecentLoginBefore(cutoff);

        logger.info("Users to deactivate: " + toDeactivate.size());
        
        toDeactivate.forEach(u -> {
            u.setStatus(false);
            urepo.save(u);
        });
    }
}

