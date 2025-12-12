package com.justinaji.chatapp_messages.service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
    @AfterEach
    void tearDown() {
        // Clean up security context
        SecurityContextHolder.clearContext();
    }


    // getAlphaNumericString() should always 8 chars
    @Test
    void testGetAlphaNumericString() {
        String result = CommonMethods.getAlphaNumericString();
        assertNotNull(result);
        assertEquals(8, result.length());
        assertTrue(result.matches("[0-9a-z]{8}"));
    }


    // getCurrentUser() — mock SecurityContextHolder

    @Test
    void testGetCurrentUser() {
        // Mock objects
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        CurrentUser principal = mock(CurrentUser.class);
        users mockUser = new users();
        mockUser.setName("justin");

        // Wiring
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getUser()).thenReturn(mockUser);

        SecurityContextHolder.setContext(context);

        // Call
        users result = CommonMethods.getCurrentUser();
        assertEquals("justin", result.getName());
    }

    // Encryption / Decryption
    @Test
    void testEncryptDecryptMessage() {
        String text = "Hello there!";
        String key = "1234567890123456"; // 16 bytes → AES key

        // Convert key into Base64, because method expects Base64 string
        String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());

        String encrypted = CommonMethods.encryptMessage(text, encodedKey);
        assertNotNull(encrypted);
        assertNotEquals(text, encrypted);

        String decrypted = CommonMethods.decryptMessage(encrypted, encodedKey);
        assertEquals(text, decrypted);
    }

    // SecretKey <-> String conversions
    @Test
    void testSecretKeyConversion() {
        String key = "abcdefghijklmnop"; // 16 bytes
        String encoded = Base64.getEncoder().encodeToString(key.getBytes());

        SecretKey secretKey = CommonMethods.convertStringToSecretKeyto(encoded);
        assertNotNull(secretKey);

        String encodedBack = CommonMethods.convertSecretKeyToString(secretKey);
        assertEquals(encoded, encodedBack);
    }
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



    @Test
    void GetAlphaNumericString_ContainOnlyAlphanumericCharacters() {
         String result = CommonMethods.getAlphaNumericString();

        // Assert
        assertTrue(result.matches("^[0-9a-z]+$"));
    }

    @Test
    void GetAlphaNumericString_MultipleCalls_GenerateDifferentStrings() {
         Set<String> generatedStrings = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            generatedStrings.add(CommonMethods.getAlphaNumericString());
        }

        // Assert - with 100 calls, we  have many unique strings (allowing some collisions)
        assertTrue(generatedStrings.size() > 90, 
            "Expected more unique strings, got " + generatedStrings.size());
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

        //  fall back to UTC → today's case
        String expected = timestamp.toInstant()
                                   .atZone(java.time.ZoneId.of("UTC"))
                                   .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));

        assertEquals(expected, result);
    }
}
