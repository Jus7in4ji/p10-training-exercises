package com.justinaji.jproj.service;

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

}