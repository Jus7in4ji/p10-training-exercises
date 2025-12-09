package com.justinaji.chatapp_userchats.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.justinaji.chatapp_userchats.model.CurrentUser;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class MyUserDetailServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private MyUserDetailService myUserDetailService;

    private users testUser;
    private String testUsername;

    @BeforeEach
    void setUp() {
        testUsername = "testuser";
        testUser = new users();
        testUser.setName(testUsername);
        testUser.setPassword("password123");
        // Set any other required fields for the users object
    }

    @Test
    void LoadUserByUsername_WithExistingUser_ReturnUserDetails() {
        // Arrange
        when(userRepo.findByName(testUsername)).thenReturn(testUser);

        // Act
        UserDetails result = myUserDetailService.loadUserByUsername(testUsername);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CurrentUser);
        assertEquals(testUsername, result.getUsername());
        verify(userRepo, times(1)).findByName(testUsername);
    }

    @Test
    void LoadUserByUsername_WithNonExistingUser_ThrowException() {
        // Arrange
        String nonExistentUsername = "nonexistent";
        when(userRepo.findByName(nonExistentUsername)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> myUserDetailService.loadUserByUsername(nonExistentUsername)
        );

        assertEquals("User not Found", exception.getMessage());
        verify(userRepo, times(1)).findByName(nonExistentUsername);
    }

    @Test
    void LoadUserByUsername_WithNullUsername_ThrowException() {
        // Arrange
        when(userRepo.findByName(null)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            myUserDetailService.loadUserByUsername(null);
        });

        verify(userRepo, times(1)).findByName(null);
    }

    @Test
    void LoadUserByUsername_WithEmptyUsername_ThrowException() {
        // Arrange
        String emptyUsername = "";
        when(userRepo.findByName(emptyUsername)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            myUserDetailService.loadUserByUsername(emptyUsername);
        });

        verify(userRepo, times(1)).findByName(emptyUsername);
    }

    @Test
    void LoadUserByUsername_RepositoryCalledWithCorrectParameter() {
        // Arrange
        String username = "specificuser";
        users specificUser = new users();
        specificUser.setName(username);
        when(userRepo.findByName(username)).thenReturn(specificUser);

        // Act
        myUserDetailService.loadUserByUsername(username);

        // Assert
        verify(userRepo, times(1)).findByName(username);
        verify(userRepo, never()).findByName(argThat(arg -> !username.equals(arg)));
    }

    @Test
    void LoadUserByUsername_ReturnsCurrentUserWithCorrectUserObject() {
        // Arrange
        when(userRepo.findByName(testUsername)).thenReturn(testUser);

        // Act
        UserDetails result = myUserDetailService.loadUserByUsername(testUsername);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CurrentUser);
        
        // Verify the CurrentUser was created with the correct user object
        CurrentUser currentUser = (CurrentUser) result;
        assertEquals(testUser.getName(), currentUser.getUsername());
        assertEquals(testUser.getPassword(), currentUser.getPassword());
    }

    @Test
    void Constructor_WithValidUserRepo_Initialize() {
        // Arrange
        UserRepo repo = mock(UserRepo.class);

        // Act
        MyUserDetailService service = new MyUserDetailService(repo);

        // Assert
        assertNotNull(service);
    }

    @Test
    void LoadUserByUsername_MultipleCalls_CallRepositoryEachTime() {
        // Arrange
        when(userRepo.findByName(testUsername)).thenReturn(testUser);

        // Act
        myUserDetailService.loadUserByUsername(testUsername);
        myUserDetailService.loadUserByUsername(testUsername);
        myUserDetailService.loadUserByUsername(testUsername);

        // Assert
        verify(userRepo, times(3)).findByName(testUsername);
    }
}