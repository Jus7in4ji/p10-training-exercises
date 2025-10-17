package com.justinaji.newproj.service;

public class NoUserFound extends RuntimeException {
    public NoUserFound(){
        super("No user found . Please login");
    }
}
