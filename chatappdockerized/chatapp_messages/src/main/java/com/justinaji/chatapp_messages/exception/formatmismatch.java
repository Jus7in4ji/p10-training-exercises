package com.justinaji.chatapp_messages.exception;

public class formatmismatch extends RuntimeException{
    public formatmismatch(){
        super("Mismatch in format of data entered");
    }
}
