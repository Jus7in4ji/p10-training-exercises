package com.justinaji.jproj.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    private String secretKEY = "";
    
    public JWTService() {
        try {
            // Generate HMAC SHA-256 key
            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keygen.generateKey();

            // Save Base64 encoded key
            secretKEY = Base64.getEncoder().encodeToString(sk.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String gentoken(String mailid) {

        Map<String, Object> claims = new HashMap<>();

        // 30 minutes expiration (example) — FIXED your wrong calculation
        long now = System.currentTimeMillis();
        long expiry = now + (30 * 60 * 1000); // 30 minutes in milliseconds

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(mailid)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(expiry))
            .signWith(getkey())
            .compact();
    }

    private SecretKey getkey() {
        
        byte[] keybytes = Decoders.BASE64.decode(secretKEY);
        return Keys.hmacShaKeyFor(keybytes);
    }

    public String extractUser(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getkey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validatetoken(String token, UserDetails userDetails) {
        final String userEmail = extractUser(token);
        return (userEmail.equals(userDetails.getUsername()) && !istokenExpired(token));

    }

    private boolean istokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
