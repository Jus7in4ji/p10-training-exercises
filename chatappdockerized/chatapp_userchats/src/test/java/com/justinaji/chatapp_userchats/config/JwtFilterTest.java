package com.justinaji.chatapp_userchats.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.justinaji.chatapp_userchats.service.JWTService;
import com.justinaji.chatapp_userchats.service.MyUserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JWTService jwtService;

    @Mock
    private MyUserDetailService userDetailService;

    @Mock
    private ApplicationContext context;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void DoFilterInternal_validToken_setsAuthentication() throws Exception {
        String token = "validToken";
        String username = "user1";

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("pass")
                .authorities("ROLE_USER")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUser(token)).thenReturn(username);
        when(context.getBean(MyUserDetailService.class)).thenReturn(userDetailService);
        when(userDetailService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validatetoken(token, userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }


    @Test
    void DoFilterInternal_noHeader_doesNothing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
