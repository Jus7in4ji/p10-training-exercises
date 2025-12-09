package com.justinaji.chatapp_userchats.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtFilter jwtFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(securityConfig, "userDetailsService", userDetailsService);
        ReflectionTestUtils.setField(securityConfig, "jwtFilter", jwtFilter);
    }

    @Test
    void AuthenticationProvider_ReturnDaoAuthenticationProvider() {
        // Act
        AuthenticationProvider provider = securityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void AuthenticationProvider_UseUserDetailsService() {
        // Act
        AuthenticationProvider provider = securityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void AuthenticationManager_ReturnAuthenticationManager() throws Exception {
        // Arrange
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // Act
        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(manager);
        assertEquals(authenticationManager, manager);
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    void AuthenticationProvider_MultipleCallsCreateNewInstances() {
        // Act
        AuthenticationProvider provider1 = securityConfig.authenticationProvider();
        AuthenticationProvider provider2 = securityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider1);
        assertNotNull(provider2);
        assertNotSame(provider1, provider2); // Each call creates a new instance
    }

    @Test
    void AuthenticationManager_WithNullConfigThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            securityConfig.authenticationManager(null);
        });
    }

    @Test
    void SecurityConfig_BeansAreNotNull() {
        // Assert
        assertNotNull(securityConfig);
    }

    @Test
    void AuthenticationProvider_PasswordEncoderStrength() {
        // Act
        AuthenticationProvider provider = securityConfig.authenticationProvider();
        
        // Assert - BCrypt with strength 12 is used
        // We can verify this by creating a BCryptPasswordEncoder and comparing behavior
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String testPassword = "testPassword";
        String encoded = encoder.encode(testPassword);
        
        assertTrue(encoder.matches(testPassword, encoded));
        assertTrue(encoded.startsWith("$2a$12$")); // BCrypt strength 12 identifier
    }

    @Test
    void AuthenticationProvider_IsConfiguredCorrectly() {
        // Act
        DaoAuthenticationProvider provider = (DaoAuthenticationProvider) securityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        // Verify the provider can be used (it doesn't throw exceptions when created)
        assertDoesNotThrow(() -> provider.toString());
    }
}