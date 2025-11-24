package com.justinaji.chatapp_messages.service;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.justinaji.chatapp_messages.model.CurrentUser;
import com.justinaji.chatapp_messages.model.users;

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
        return formatTimestamp(timestamp, "UTC");
    }

    public static String formatTimestamp(Timestamp timestamp, String timezone) {
        ZoneId zone;
        try {
            zone = (timezone == null || timezone.trim().isEmpty())
                    ? ZoneId.of("UTC") 
                    : ZoneId.of(timezone);
        } catch (Exception e) {
            zone = ZoneId.of("UTC"); // use UTC if invalid timezone string
        }

        ZonedDateTime zdt = timestamp.toInstant().atZone(zone);

        String today = LocalDate.now(zone).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String currentyear = String.valueOf(Year.now(zone));

        String givenyear = zdt.format(DateTimeFormatter.ofPattern("yyyy"));
        String givendate = zdt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        if (givenyear.equals(currentyear)) {
            return givendate.equals(today)
                ? zdt.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))
                : zdt.format(DateTimeFormatter.ofPattern("dd/MM | hh:mm:ss a"));
        } else {
            return zdt.format(DateTimeFormatter.ofPattern("dd/MM/yy | hh:mm:ss a"));
        }
    }

}