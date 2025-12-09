package com.justinaji.chatapp_messages.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    // Generate a valid base64-encoded secret key for testing (minimum 256 bits for HS256)
    private static final String TEST_SECRET = "dGVzdFNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlc09ubHlNdXN0QmVMb25nRW5vdWdoRm9ySFMyNTY=";
    private String testUsername;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        // Inject the test secret key into the service
        ReflectionTestUtils.setField(jwtService, "secretKEY", TEST_SECRET);
        
        testUsername = "testuser@example.com";
        testUserDetails = User.builder()
            .username(testUsername)
            .password("password")
            .authorities(new ArrayList<>())
            .build();
    }

    @Test
    void Gentoken_ShouldGenerateValidToken() {
        // Act
        String token = jwtService.gentoken(testUsername);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void Gentoken_TokenShouldContainCorrectUsername() {
        // Act
        String token = jwtService.gentoken(testUsername);
        String extractedUsername = jwtService.extractUser(token);

        // Assert
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void Gentoken_TokenShouldHaveCorrectExpiration() {
        // Arrange
        long beforeGeneration = System.currentTimeMillis();
        
        // Act
        String token = jwtService.gentoken(testUsername);
        
        // Assert
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();
        
        long expirationTime = expiration.getTime() - issuedAt.getTime();
        long expectedExpiration = 30 * 60 * 1000; // 30 minutes
        
        // Allow small time difference (within 1 second)
        assertTrue(Math.abs(expirationTime - expectedExpiration) < 1000);
    }

    @Test
    void ExtractUser_ShouldReturnCorrectUsername() {
        // Arrange
        String token = jwtService.gentoken(testUsername);

        // Act
        String extractedUser = jwtService.extractUser(token);

        // Assert
        assertEquals(testUsername, extractedUser);
    }

    @Test
    void Validatetoken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtService.gentoken(testUsername);

        // Act
        boolean isValid = jwtService.validatetoken(token, testUserDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void Validatetoken_WithDifferentUsername_ShouldReturnFalse() {
        // Arrange
        String token = jwtService.gentoken("differentuser@example.com");

        // Act
        boolean isValid = jwtService.validatetoken(token, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

 @Test
    void Validatetoken_WithExpiredToken_ShouldThrowExpiredJwtException() {
        // Arrange - create an expired token
        String expiredToken = createExpiredToken(testUsername);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.validatetoken(expiredToken, testUserDetails);
        });
    }

    @Test
    void ExtractUser_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtService.extractUser(invalidToken);
        });
    }

    @Test
    void Gentoken_MultipleCalls_ShouldGenerateDifferentTokens() {
        // Act
        String token1 = jwtService.gentoken("name1");
        
        // Wait a bit to ensure different issuedAt timestamps
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String token2 = jwtService.gentoken("name2");

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    void Validatetoken_WithNullUserDetails_ShouldThrowException() {
        // Arrange
        String token = jwtService.gentoken(testUsername);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            jwtService.validatetoken(token, null);
        });
    }

    // Helper method to parse token for testing purposes
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // Helper method to create an expired token for testing
    private String createExpiredToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        long now = System.currentTimeMillis();
        long expiredTime = now - (60 * 60 * 1000); // 1 hour ago
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(expiredTime - (30 * 60 * 1000)))
            .setExpiration(new Date(expiredTime))
            .signWith(key)
            .compact();
    }
}