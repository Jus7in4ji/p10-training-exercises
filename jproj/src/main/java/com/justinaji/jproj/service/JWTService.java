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
    
    public JWTService(){
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keygen.generateKey();
            secretKEY = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    } 
    
    public String gentoken(String mailid){

        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
            .claims()
            .add(claims)
            .subject(mailid)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
            .and()
            .signWith(getkey())
            .compact();
    }

    private SecretKey getkey() {
        
        byte[] keybytes = Decoders.BASE64.decode(secretKEY);
        System.out.println(secretKEY);
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
