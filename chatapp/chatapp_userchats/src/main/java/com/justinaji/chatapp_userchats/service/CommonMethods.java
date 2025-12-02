package com.justinaji.chatapp_userchats.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.justinaji.chatapp_userchats.model.CurrentUser;
import com.justinaji.chatapp_userchats.model.users;

public class CommonMethods {
   
    static String getAlphaNumericString(){ // used to create custom id strings for tables
        String AlphaNumericString = "0123456789" + "abcdefghijklmnopqrstuvxyz"; 
        StringBuilder sb = new StringBuilder(8); 

        for (int i = 0; i < 8; i++) { 
            int index  = (int)(AlphaNumericString.length() * Math.random()); 
            sb.append(AlphaNumericString.charAt(index)); 
        } 
        return sb.toString(); 
    } 

    public static users getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser user = (CurrentUser) auth.getPrincipal();
        return user.getUser();
    }

    public static SecretKey convertStringToSecretKeyto(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    public static String generateKey(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

            SecretKey secretKey = keyGenerator.generateKey();
            return convertSecretKeyToString(secretKey);
        } 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}