package com.justinaji.chatapp_userchats.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justinaji.chatapp_userchats.dto.Loginform;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.service.UserServicesImpl;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServicesImpl userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void Signup_Success() throws Exception {
        // Arrange
        users user = new users();
        user.setName("testuser");
        user.setPassword("password123");
        
        String expectedResponse = "User registered successfully";
        when(userService.RegisterUser(any(users.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/SignUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(userService, times(1)).RegisterUser(any(users.class));
    }

    @Test
    void Signup_CallRegisterUserWithCorrectData() throws Exception {
        // Arrange
        users user = new users();
        user.setName("newuser");
        user.setPassword("securepass");
        
        when(userService.RegisterUser(any(users.class))).thenReturn("Success");

        // Act
        mockMvc.perform(post("/auth/SignUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Assert
        verify(userService).RegisterUser(argThat(u -> 
            u.getName().equals("newuser") && u.getPassword().equals("securepass")
        ));
    }

    @Test
    void Login_WithValidCredentials_ReturnToken() throws Exception {
        // Arrange
        Loginform loginForm = new Loginform("testuser","password123");
        
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        when(userService.login("testuser", "password123")).thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(userService, times(1)).login("testuser", "password123");
    }

    @Test
    void Login_ExtractUsernameAndPasswordFromLoginForm() throws Exception {
        // Arrange
        Loginform loginForm = new Loginform("user@example.com","mypassword");
        
        when(userService.login(anyString(), anyString())).thenReturn("token");

        // Act
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk());

        // Assert
        verify(userService).login("user@example.com", "mypassword");
    }

    @Test
    void Login_WithEmptyCredentials_StillCallService() throws Exception {
        // Arrange
        Loginform loginForm = new Loginform("","");
        when(userService.login("", "")).thenReturn("Invalid credentials");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk());

        verify(userService, times(1)).login("", "");
    }


    @Test
    void Login_ServiceReturnsError_ReturnErrorMessage() throws Exception {
        // Arrange
        Loginform loginForm = new Loginform("wronguser","wrongpass");
        
        String errorMessage = "Invalid username or password";
        when(userService.login("wronguser", "wrongpass")).thenReturn(errorMessage);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andExpect(content().string(errorMessage));
    }

}