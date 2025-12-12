package com.justinaji.gateway.security;

import java.net.URI;
import java.util.Date;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String TEST_SECRET = "dGVzdFNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlc09ubHlNdXN0QmVMb25nRW5vdWdoRm9ySFMyNTY=";
    private String validToken;
    private HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "secretKEY", TEST_SECRET);
        
        // Generate a valid JWT token for testing
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        long now = System.currentTimeMillis();
        validToken = Jwts.builder()
            .setSubject("testuser")
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + 30 * 60 * 1000))
            .signWith(key)
            .compact();

        // Use real HttpHeaders instead of mocking
        httpHeaders = new HttpHeaders();

        
    }

    @Test
    void filter_AuthEndpoint_SkipValidation() {
        when(exchange.getRequest()).thenReturn(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        when(request.getURI()).thenReturn(URI.create("http://localhost/auth/login"));

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertNotNull(result);
        result.block(); // Block to execute the reactive chain
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void filter_AuthSignUpEndpoint_SkipValidation() {
        when(exchange.getRequest()).thenReturn(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        when(request.getURI()).thenReturn(URI.create("http://localhost/auth/SignUp"));

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertNotNull(result);
        result.block();
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void filter_ValidToken_AllowRequest() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertNotNull(result);
        result.block();
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void filter_MissingAuthorizationHeader_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        // httpHeaders is empty (no Authorization header set)

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertNotNull(result);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            result.block();
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Missing or invalid Authorization header"));
        verify(chain, never()).filter(exchange);
    }

    @Test
    void filter_InvalidBearerFormat_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Basic " + validToken);

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            result.block();
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(chain, never()).filter(exchange);
    }

    @Test
    void filter_NoBearerPrefix_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, validToken);

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            result.block();
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void filter_InvalidToken_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here");

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            result.block();
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid JWT token"));
        verify(chain, never()).filter(exchange);
    }

    @Test
    void filter_ExpiredToken_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        long now = System.currentTimeMillis();
        String expiredToken = Jwts.builder()
            .setSubject("testuser")
            .setIssuedAt(new Date(now - 2 * 60 * 60 * 1000)) // 2 hours ago
            .setExpiration(new Date(now - 1 * 60 * 60 * 1000)) // 1 hour ago
            .signWith(key)
            .compact();

        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken);

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            result.block();
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(chain, never()).filter(exchange);
    }

    @Test
    void filter_LowercaseAuthorizationHeader_Work() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set("authorization", "Bearer " + validToken);

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertNotNull(result);
        result.block();
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void filter_MalformedToken_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer malformed");

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertThrows(ResponseStatusException.class, () -> result.block());
    }

    @Test
    void filter_EmptyBearerToken_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer ");

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertThrows(ResponseStatusException.class, () -> result.block());
    }

    @Test
    void filter_ProtectedEndpoint_WithoutToken_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/chats"));
        // No Authorization header set

         Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

         assertThrows(ResponseStatusException.class, () -> result.block());
        verify(chain, never()).filter(exchange);
    }

    @Test
    void filter_MultipleAuthEndpoints_AllSkipValidation() {
        when(exchange.getRequest()).thenReturn(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        // Test /auth/login
        when(request.getURI()).thenReturn(URI.create("http://localhost/auth/login"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        // Test /auth/signup
        when(request.getURI()).thenReturn(URI.create("http://localhost/auth/signup"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        // Test /auth/refresh
        when(request.getURI()).thenReturn(URI.create("http://localhost/auth/refresh"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        verify(chain, times(3)).filter(exchange);
    }

    @Test
    void getOrder_ReturnNegativeOne() {
         int order = jwtAuthenticationFilter.getOrder();

         assertEquals(-1, order);
    }

    @Test
    void filter_ValidTokenWithDifferentEndpoints_AllowAll() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);

        // Test /api/users
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        // Test /chats
        when(request.getURI()).thenReturn(URI.create("http://localhost/chats"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        // Test /messages
        when(request.getURI()).thenReturn(URI.create("http://localhost/messages"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        verify(chain, times(3)).filter(exchange);
    }

    @Test
    void filter_AuthEndpointVariations_AllSkip() {
        when(exchange.getRequest()).thenReturn(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        // Test different /auth paths
        when(request.getURI()).thenReturn(URI.create("http://localhost/auth"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        when(request.getURI()).thenReturn(URI.create("http://localhost/auth/"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        when(request.getURI()).thenReturn(URI.create("http://localhost/auth/test"));
        jwtAuthenticationFilter.filter(exchange, chain).block();

        verify(chain, times(3)).filter(exchange);
    }

    @Test
    void filter_NullToken_AfterBearer_ReturnUnauthorized() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/users"));
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer null");

        assertThrows(ResponseStatusException.class, () -> {
            jwtAuthenticationFilter.filter(exchange, chain).block();
        });
    }
}