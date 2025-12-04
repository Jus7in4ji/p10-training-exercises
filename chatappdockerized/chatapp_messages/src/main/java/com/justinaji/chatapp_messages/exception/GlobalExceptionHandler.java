package com.justinaji.chatapp_messages.exception;

import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<String> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex) {
        return new ResponseEntity<>("Encryption algorithm not found: " + ex.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(formatmismatch.class)
    public ResponseEntity<String> handleformatmismatch(formatmismatch ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handlebadcredentials(BadCredentialsException ex){ 
        return new ResponseEntity<>("User credentials are incorrect", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(No_messages.class)
    public ResponseEntity<String>  handleNoMessages(No_messages ex ){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(nochatFound.class)
    public ResponseEntity<String>  HandleInvalidChatName(nochatFound ex ){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotaMember.class)
    public ResponseEntity<String>  handleAlienUser(NotaMember ex ){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoUserFound.class)
    public ResponseEntity<String>  handleinvaliduser(NoUserFound ex ){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }
}