package com.justinaji.gateway.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void springSecurityFilterChain_ShouldReturnNonNull() {
        // Arrange
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // Act
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        // Assert
        assertNotNull(filterChain);
    }

    @Test
    void springSecurityFilterChain_ShouldBuildSuccessfully() {
        // Arrange
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // Act & Assert
        assertDoesNotThrow(() -> {
            SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);
            assertNotNull(filterChain);
        });
    }

    @Test
    void securityConfig_ShouldInstantiateWithoutErrors() {
        // Act & Assert
        assertNotNull(securityConfig);
        assertDoesNotThrow(() -> new SecurityConfig());
    }

    @Test
    void springSecurityFilterChain_CalledMultipleTimes_ShouldCreateDifferentInstances() {
        // Arrange
        ServerHttpSecurity http1 = ServerHttpSecurity.http();
        ServerHttpSecurity http2 = ServerHttpSecurity.http();

        // Act
        SecurityWebFilterChain chain1 = securityConfig.springSecurityFilterChain(http1);
        SecurityWebFilterChain chain2 = securityConfig.springSecurityFilterChain(http2);

        // Assert
        assertNotNull(chain1);
        assertNotNull(chain2);
        assertNotSame(chain1, chain2);
    }
}