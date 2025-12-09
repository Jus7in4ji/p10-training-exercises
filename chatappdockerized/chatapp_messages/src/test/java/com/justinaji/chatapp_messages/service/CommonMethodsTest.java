package com.justinaji.chatapp_messages.service;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.justinaji.chatapp_messages.model.CurrentUser;
import com.justinaji.chatapp_messages.model.users;

class CommonMethodsTest {

    private SecurityContext securityContext;
    private Authentication authentication;
    private CurrentUser currentUser;
    private users testUser;

    @BeforeEach
    void setUp() {
        // Setup security context
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Setup test user
        testUser = new users();
        testUser.setName("testuser");
        testUser.setPassword("password123");

        currentUser = new CurrentUser(testUser);
        authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
    }

    @AfterEach
    void tearDown() {
        // Clean up security context
        SecurityContextHolder.clearContext();
    }


    @Test
    void GetAlphaNumericString_ShouldContainOnlyAlphanumericCharacters() {
        // Act
        String result = CommonMethods.getAlphaNumericString();

        // Assert
        assertTrue(result.matches("^[0-9a-z]+$"));
    }

    @Test
    void GetAlphaNumericString_MultipleCalls_ShouldGenerateDifferentStrings() {
        // Act
        Set<String> generatedStrings = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            generatedStrings.add(CommonMethods.getAlphaNumericString());
        }

        // Assert - with 100 calls, we should have many unique strings (allowing some collisions)
        assertTrue(generatedStrings.size() > 90, 
            "Expected more unique strings, got " + generatedStrings.size());
    }

    @Test
    void GetCurrentUser_WithAuthenticatedUser_ShouldReturnUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        users result = CommonMethods.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getPassword(), result.getPassword());
    }

    @Test
    void GetCurrentUser_ShouldReturnSameUserObjectFromCurrentUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        users result = CommonMethods.getCurrentUser();

        // Assert
        assertSame(testUser, result);
    }

    @Test
    void FormatTimestamp_TodaySameYear_UTC() {
        // create a timestamp for current date and time
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        String result = CommonMethods.formatTimestamp(timestamp, "UTC");

        // expected format = hh:mm:ss AM/PM (today case)
        String expected = timestamp.toInstant()
            .atZone(java.time.ZoneId.of("UTC"))
            .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));

        assertEquals(expected, result);
    }

    @Test
    void FormatTimestamp_DifferentDaySameYear() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(2);
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        String result = CommonMethods.formatTimestamp(timestamp, "Asia/Kolkata");

        String expected = dateTime.atZone(java.time.ZoneId.of("Asia/Kolkata"))
                                  .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM | hh:mm:ss a"));

        assertEquals(expected, result);
    }

    @Test
    void FormatTimestamp_DifferentYear() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 5, 10, 15, 30);
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        String result = CommonMethods.formatTimestamp(timestamp, "Asia/Kolkata");

        String expected = dateTime.atZone(java.time.ZoneId.of("Asia/Kolkata"))
                                  .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy | hh:mm:ss a"));

        assertEquals(expected, result);
    }

    @Test
    void FormatTimestamp_InvalidTimezone() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        String result = CommonMethods.formatTimestamp(timestamp, "INVALID_ZONE");

        // Should fall back to UTC → today's case
        String expected = timestamp.toInstant()
                                   .atZone(java.time.ZoneId.of("UTC"))
                                   .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));

        assertEquals(expected, result);
    }
}
