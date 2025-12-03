package com.justinaji.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;


@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Skip auth endpoints from JWT validation
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        // Extract token from Authorization header (case-insensitive)
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            authHeader = exchange.getRequest().getHeaders().getFirst("authorization");
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        try {
            // Validate token exactly as your microservice does:
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("xfjlcyD4pLcXF011QjeJPyEKdnxxTSUq9AJ2AcqC0+o=")))
                .build()
                .parseSignedClaims(token)
                .getPayload();

            // You can add extra validations if you want, but minimal validation is done here

        } catch (Exception e) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + e.getMessage()));
        }

        // Forward request unchanged (Authorization header remains)
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;  // Make sure filter runs early
    }
}
