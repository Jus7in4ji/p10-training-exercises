package com.justinaji.jproj.exception;

import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<String> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex) {
        return new ResponseEntity<>("Encryption algorithm not found: " + ex.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(invaliduser.class)
    public ResponseEntity<String> handleinvaliduser(invaliduser ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }
}