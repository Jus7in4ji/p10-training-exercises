package com.justinaji.chatapp_userchats.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.justinaji.chatapp_userchats.model.CurrentUser;
import com.justinaji.chatapp_userchats.model.users;

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
    void GetAlphaNumericString_ContainOnlyAlphanumericCharacters() {
        // Act
        String result = CommonMethods.getAlphaNumericString();

        // Assert
        assertTrue(result.matches("^[0-9a-z]+$"));
    }

    @Test
    void GetAlphaNumericString_MultipleCalls_GenerateDifferentStrings() {
        // Act
        Set<String> generatedStrings = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            generatedStrings.add(CommonMethods.getAlphaNumericString());
        }

        // Assert - with 100 calls, we  have many unique strings (allowing some collisions)
        assertTrue(generatedStrings.size() > 90, 
            "Expected more unique strings, got " + generatedStrings.size());
    }

    @Test
    void GetCurrentUser_WithAuthenticatedUser_ReturnUser() {
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
    void GetCurrentUser_ReturnSameUserObjectFromCurrentUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        users result = CommonMethods.getCurrentUser();

        // Assert
        assertSame(testUser, result);
    }

    @Test
    void ConvertSecretKeyToString_ReturnBase64EncodedString() {
        // Arrange
        String originalKey = CommonMethods.generateKey();
        SecretKey secretKey = CommonMethods.convertStringToSecretKeyto(originalKey);

        // Act
        String result = CommonMethods.convertSecretKeyToString(secretKey);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Verify it's valid Base64
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void ConvertStringToSecretKeyto_ReturnValidSecretKey() {
        // Arrange
        String encodedKey = CommonMethods.generateKey();

        // Act
        SecretKey result = CommonMethods.convertStringToSecretKeyto(encodedKey);

        // Assert
        assertNotNull(result);
        assertEquals("AES", result.getAlgorithm());
        assertNotNull(result.getEncoded());
    }


    @Test
    void GenerateKey_ReturnNonEmptyString() {
        // Act
        String result = CommonMethods.generateKey();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void GenerateKey_ValidBase64String() {
        // Act
        String result = CommonMethods.generateKey();

        // Assert
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void GenerateKey_MultipleCalls_GenerateDifferentKeys() {
        // Act
        String key1 = CommonMethods.generateKey();
        String key2 = CommonMethods.generateKey();
        String key3 = CommonMethods.generateKey();

        // Assert
        assertNotEquals(key1, key2);
        assertNotEquals(key2, key3);
        assertNotEquals(key1, key3);
    }

    @Test
    void GenerateKey_Generate256BitKey() {
        // Act
        String encodedKey = CommonMethods.generateKey();
        SecretKey secretKey = CommonMethods.convertStringToSecretKeyto(encodedKey);

        // Assert
        // 256 bits = 32 bytes
        assertEquals(32, secretKey.getEncoded().length);
    }

    @Test
    void KeyConversion_RoundTrip() {
        // Arrange
        String originalEncodedKey = CommonMethods.generateKey();

        // Act
        SecretKey secretKey = CommonMethods.convertStringToSecretKeyto(originalEncodedKey);
        String convertedBackKey = CommonMethods.convertSecretKeyToString(secretKey);

        // Assert
        assertEquals(originalEncodedKey, convertedBackKey);
    }

    @Test
    void KeyConversion_WithGeneratedKey_BeUsableForEncryption() {
        // Arrange
        String encodedKey = CommonMethods.generateKey();

        // Act
        SecretKey secretKey = CommonMethods.convertStringToSecretKeyto(encodedKey);

        // Assert
        assertNotNull(secretKey);
        assertEquals("AES", secretKey.getAlgorithm());
        assertEquals("RAW", secretKey.getFormat());
    }



}