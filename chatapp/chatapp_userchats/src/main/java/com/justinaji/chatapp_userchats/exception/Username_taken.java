package com.justinaji.chatapp_userchats.exception;

public class Username_taken extends RuntimeException{

    public Username_taken(){
        super("The given name is already taken , please try another");
    }
}
