package com.justinaji.chatapp_messages.exception;

public class NoUserFound extends RuntimeException {
    public NoUserFound(String username){
        super("Error: No user of the name '"+username+"' exists.\nMake sure the information entered is correct. ");
    }
}
