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
        ServerHttpSecurity http = ServerHttpSecurity.http();

         SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

         assertNotNull(filterChain);
    }

    @Test
    void springSecurityFilterChain_ShouldBuildSuccessfully() {
        ServerHttpSecurity http = ServerHttpSecurity.http();

        assertDoesNotThrow(() -> {
            SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);
            assertNotNull(filterChain);
        });
    }

    @Test
    void securityConfig_ShouldInstantiateWithoutErrors() {
        assertNotNull(securityConfig);
        assertDoesNotThrow(() -> new SecurityConfig());
    }

    @Test
    void springSecurityFilterChain_CalledMultipleTimes_ShouldCreateDifferentInstances() {
        ServerHttpSecurity http1 = ServerHttpSecurity.http();
        ServerHttpSecurity http2 = ServerHttpSecurity.http();

         SecurityWebFilterChain chain1 = securityConfig.springSecurityFilterChain(http1);
        SecurityWebFilterChain chain2 = securityConfig.springSecurityFilterChain(http2);

         assertNotNull(chain1);
        assertNotNull(chain2);
        assertNotSame(chain1, chain2);
    }
}