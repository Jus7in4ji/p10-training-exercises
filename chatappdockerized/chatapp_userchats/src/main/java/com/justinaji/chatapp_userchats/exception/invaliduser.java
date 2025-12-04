package com.justinaji.chatapp_userchats.exception;

public class invaliduser extends RuntimeException{
    public invaliduser(){
        super("User credentials are incorrect");
    }
}
