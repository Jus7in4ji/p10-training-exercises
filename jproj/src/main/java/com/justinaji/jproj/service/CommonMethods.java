package com.justinaji.jproj.service;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.justinaji.jproj.model.CurrentUser;
import com.justinaji.jproj.model.users;

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

    static users getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser user = (CurrentUser) auth.getPrincipal();
        return user.getUser();
    }

    public static String encryptMessage(String text , String key) {
        try{
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, convertStringToSecretKeyto(key));
            
            byte[] ecrypted = cipher.doFinal(text.getBytes());

            String encryptedString = Base64.getEncoder().encodeToString(ecrypted);
            return encryptedString;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String decryptMessage(String text , String key){
        try{
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, convertStringToSecretKeyto(key));
            
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(text));
            String decryptedString = new String(decrypted);
            return decryptedString; 
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

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

    public static String formatTimestamp(Timestamp timestamp) {
        //get current date and year values
        Date date = new Date(new Timestamp(System.currentTimeMillis()).getTime());
        String today = new SimpleDateFormat("yyyy/MM/dd").format(date);
        String currentyear = new SimpleDateFormat("yyyy").format(date);

        //convert given timestamp to it's year and date values
        LocalDateTime ldt = timestamp.toLocalDateTime();
        String givenyear = ldt.format(DateTimeFormatter.ofPattern("yyyy"));
        String givendate = ldt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String result;
        if(givenyear.equals(currentyear)){// compare the two sets
            result = givendate.equals(today) ?  
            ldt.format(DateTimeFormatter.ofPattern("hh:mm:ss a")) : //only time 
            ldt.format(DateTimeFormatter.ofPattern("dd/MM hh:mm:ss a"));  // time with day/month
        }
        else result = ldt.format(DateTimeFormatter.ofPattern("dd/MM/yy hh:mm:ss a")); // full date and time 
        return result;
    }
}