package com.justinaji.jproj.exception;

public class Username_taken extends RuntimeException{

    public Username_taken(){
        super("The given name is already taken , please try another");
    }
}
