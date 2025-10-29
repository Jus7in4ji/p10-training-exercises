package com.justinaji.jproj.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String encryptpassword(String input) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());
        BigInteger bigint = new BigInteger(1, hash);
        return bigint.toString(16);  
    } 
    catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("SHA-256 algorithm not found", e);
    }
    
}
}