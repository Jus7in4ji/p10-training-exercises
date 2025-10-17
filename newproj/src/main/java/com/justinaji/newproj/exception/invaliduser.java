package com.justinaji.newproj.exception;

public class invaliduser extends RuntimeException{
    public invaliduser(){
        super("User credentials are incorrect");
    }
}