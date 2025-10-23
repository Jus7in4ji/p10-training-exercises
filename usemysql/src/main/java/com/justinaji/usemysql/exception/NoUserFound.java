package com.justinaji.usemysql.exception;

public class NoUserFound extends RuntimeException {
    public NoUserFound(){
        super("No user found . Please login");
    }
}
