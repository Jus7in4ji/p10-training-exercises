package com.justinaji.jproj.exception;

public class invaliduser extends RuntimeException{
    public invaliduser(){
        super("User credentials are incorrect");
    }
}
