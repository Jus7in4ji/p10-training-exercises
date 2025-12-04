package com.justinaji.chatapp_userchats.exception;

public class formatmismatch extends RuntimeException{
    public formatmismatch(){
        super("Mismatch in format of data entered");
    }
}
