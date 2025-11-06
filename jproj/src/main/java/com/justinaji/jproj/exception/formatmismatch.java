package com.justinaji.jproj.exception;

public class formatmismatch extends RuntimeException{
    public formatmismatch(){
        super("Mismatch in format of data entered");
    }
}
