package com.justinaji.chatapp_userchats.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.*;

@ExtendWith(MockitoExtension.class)
class UserServicesImplTest {

    @Mock private UserRepo urepo;
    @Mock private ChatRepo chatRepo;
    @Mock private LogRepo logrepo;
    @Mock private AuthenticationManager authManager;
    @Mock private JWTService jwtservice;

    @InjectMocks
    private UserServicesImpl userService;

    private users user;

    @BeforeEach
    void setup() {
        user = new users();
        user.setEmail("test@mail.com");
        user.setName("testUser");
        user.setPassword("1234");
    }

    @Test
    void RegisterUser_Success() {
        when(urepo.existsByEmail("test@mail.com")).thenReturn(false);
        when(urepo.existsByName("testUser")).thenReturn(false);
        when(chatRepo.existsByName("testUser")).thenReturn(false);
        when(urepo.findAll()).thenReturn(Collections.emptyList());
        when(urepo.save(any(users.class))).thenReturn(user);

        String result = userService.RegisterUser(user);

        assertEquals("New user 'test@mail.com' registered successfully", result);
        verify(urepo, times(1)).save(any(users.class));
    }

    @Test
    void RegisterUser_EmailExists() {
        when(urepo.existsByEmail("test@mail.com")).thenReturn(true);

        String result = userService.RegisterUser(user);

        assertEquals("User with the given email id already Exists", result);
        verify(urepo, never()).save(any());
    }

    @Test
    void RegisterUser_MissingFields() {
        user.setEmail("");
        String result = userService.RegisterUser(user);

        assertEquals("Email and password both must be filled", result);
    }

   @Test
    void Login_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(authentication);

        when(jwtservice.gentoken("testUser")).thenReturn("jwtToken");
        when(urepo.findByName("testUser")).thenReturn(user);
        when(logrepo.existsById(anyString())).thenReturn(false);

        String token = userService.login("testUser", "1234");

        assertEquals("jwtToken", token);
        verify(urepo, times(1)).save(user);
        verify(logrepo, times(1)).save(any());
    }
}
