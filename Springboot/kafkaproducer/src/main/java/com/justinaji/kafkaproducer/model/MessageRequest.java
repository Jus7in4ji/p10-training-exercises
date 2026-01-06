package com.justinaji.kafkaproducer.model;

public class MessageRequest{
    private String key ;
    private String message;

    public void setMessage(String message){ this.message = message; }
    public void setKey(String key){ this.key = key; }
    
    public String getMessage(){ return message; }
    public String getKey(){ return key; }
}