package com.justinaji.chatapp_messages.exception;

public class No_messages extends RuntimeException{
    public No_messages(String chatname){
        super("No messages found in chat : "+chatname);
    }
}
