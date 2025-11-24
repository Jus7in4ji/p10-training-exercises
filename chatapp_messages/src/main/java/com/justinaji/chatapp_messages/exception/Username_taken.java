package com.justinaji.chatapp_messages.exception;

public class Username_taken extends RuntimeException{

    public Username_taken(){
        super("The given name is already taken , please try another");
    }
}
