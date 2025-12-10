package com.justinaji.chatapp_messages.exception;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // -----------------------------
    //  NoSuchAlgorithmException
    // -----------------------------
    @Test
    void testHandleNoSuchAlgorithmException() {
        NoSuchAlgorithmException ex = new NoSuchAlgorithmException("SHA-256");

        ResponseEntity<String> response = handler.handleNoSuchAlgorithmException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Encryption algorithm not found: SHA-256", response.getBody());
    }

    // -----------------------------
    //  formatmismatch
    // -----------------------------
    @Test
    void testHandleFormatMismatch() {
        formatmismatch ex = new formatmismatch();

        ResponseEntity<String> response = handler.handleformatmismatch(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Mismatch in format of data entered", response.getBody());
    }

    // -----------------------------
    //  BadCredentialsException
    // -----------------------------
    @Test
    void testHandleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Invalid");

        ResponseEntity<String> response = handler.handlebadcredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User credentials are incorrect", response.getBody());
    }

    // -----------------------------
    //  No_messages
    // -----------------------------
    @Test
    void testHandleNoMessages() {
        No_messages ex = new No_messages("TestChat");

        ResponseEntity<String> response = handler.handleNoMessages(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("No messages found in chat : TestChat", response.getBody());
    }

    // -----------------------------
    //  nochatFound
    // -----------------------------
    @Test
    void testHandleNoChatFound() {
        nochatFound ex = new nochatFound("MyGroup");

        ResponseEntity<String> response = handler.HandleInvalidChatName(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error: No chat of the name MyGroup exists", response.getBody());
    }

    // -----------------------------
    //  NotaMember
    // -----------------------------
    @Test
    void testHandleNotMember() {
        NotaMember ex = new NotaMember("justin", "TestGroup");

        ResponseEntity<String> response = handler.handleAlienUser(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error: User ( justin ) is not a member of the group ( TestGroup )", response.getBody());
    }

    // -----------------------------
    //  NoUserFound
    // -----------------------------
    @Test
    void testHandleNoUserFound() {
        NoUserFound ex = new NoUserFound("justin");

        ResponseEntity<String> response = handler.handleinvaliduser(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(
            "Error: No user of the name 'justin' exists.\nMake sure the information entered is correct. ",
            response.getBody()
        );
    }

    // -----------------------------
    //  Generic Exception
    // -----------------------------
    @Test
    void testHandleOtherExceptions() {
        Exception ex = new Exception("Something bad");

        ResponseEntity<String> response = handler.handleOtherExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: Something bad", response.getBody());
    }
}
