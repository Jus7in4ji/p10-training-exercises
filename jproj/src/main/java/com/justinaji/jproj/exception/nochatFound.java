package com.justinaji.jproj.exception;

public class nochatFound extends RuntimeException {
    public nochatFound(String chatname){
        super("Error: No chat of the name "+chatname+" exists");
    }
}
