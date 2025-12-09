package com.justinaji.chatapp_userchats.config;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.UserRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserStatusSchedulerTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserStatusScheduler scheduler;

    private users user1;
    private users user2;

    @BeforeEach
    void setup() {
        user1 = new users("u1", "mail1", "user1", "pass", true, LocalDateTime.now().minusMinutes(40));
        user2 = new users("u2", "mail2", "user2", "pass", true, LocalDateTime.now().minusMinutes(50));
    }

    @Test
    void autoDeactivateUsers_DeactivateOldActiveUsers() {

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);

        when(userRepo.findByStatusTrueAndRecentLoginBefore(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(user1, user2));

        scheduler.autoDeactivateUsers();

        // verify both users were saved after deactivation
        verify(userRepo, times(1)).save(user1);
        verify(userRepo, times(1)).save(user2);

        // assert side-effects
        assert !user1.isStatus();
        assert !user2.isStatus();

        // verify find method called once
        verify(userRepo, times(1)).findByStatusTrueAndRecentLoginBefore(any(LocalDateTime.class));
    }

    @Test
    void autoDeactivateUsers_noUsersFound_DoNothing() {

        when(userRepo.findByStatusTrueAndRecentLoginBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        scheduler.autoDeactivateUsers();

        verify(userRepo, never()).save(any(users.class));
        verify(userRepo, times(1)).findByStatusTrueAndRecentLoginBefore(any(LocalDateTime.class));
    }
}
