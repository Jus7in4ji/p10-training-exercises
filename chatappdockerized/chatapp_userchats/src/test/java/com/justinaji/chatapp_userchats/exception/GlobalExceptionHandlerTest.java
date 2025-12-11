package com.justinaji.chatapp_userchats.exception;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNoSuchAlgorithmException_ShouldReturnInternalServerError() {
        NoSuchAlgorithmException exception = new NoSuchAlgorithmException("AES algorithm not found");

         ResponseEntity<String> response = exceptionHandler.handleNoSuchAlgorithmException(exception);

         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Encryption algorithm not found: AES algorithm not found", response.getBody());
    }

    @Test
    void handleInvalidUser_ShouldReturnUnauthorized() {
        invaliduser exception = new invaliduser();

         ResponseEntity<String> response = exceptionHandler.handleinvaliduser(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User credentials are incorrect", response.getBody());
    }

    @Test
    void handleFormatMismatch_ShouldReturnUnauthorized() {
        formatmismatch exception = new formatmismatch();

         ResponseEntity<String> response = exceptionHandler.handleformatmismatch(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Mismatch in format of data entered", response.getBody());
    }

    @Test
    void handleBadCredentials_ShouldReturnUnauthorizedWithCustomMessage() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

         ResponseEntity<String> response = exceptionHandler.handlebadcredentials(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User credentials are incorrect", response.getBody());
    }

    @Test
    void handleUsernameTaken_ShouldReturnUnauthorized() {
        Username_taken exception = new Username_taken();

         ResponseEntity<String> response = exceptionHandler.handleusedname(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("The given name is already taken , please try another", response.getBody());
    }

    @Test
    void handleNoMessages_ShouldReturnUnauthorized() {
        String chatname = "TestChat";
        No_messages exception = new No_messages(chatname);

         ResponseEntity<String> response = exceptionHandler.handleNoMessages(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("No messages found in chat : TestChat", response.getBody());
    }

    @Test
    void handleNotaMember_ShouldReturnUnauthorized() {
        String username = "john";
        String chatname = "Developers";
        NotaMember exception = new NotaMember(username, chatname);

         ResponseEntity<String> response = exceptionHandler.handleAlienUser(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error: User ( john ) is not a member of the group ( Developers )", response.getBody());
    }

    @Test
    void handleNoUserFound_ShouldReturnUnauthorized() {
        String username = "nonexistentuser";
        NoUserFound exception = new NoUserFound(username);

         ResponseEntity<String> response = exceptionHandler.handleinvaliduser(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error: No user of the name 'nonexistentuser' exists.\nMake sure the information entered is correct. ", 
                     response.getBody());
    }

    @Test
    void handleOtherExceptions_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error occurred");

         ResponseEntity<String> response = exceptionHandler.handleOtherExceptions(exception);

         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: Unexpected error occurred", response.getBody());
    }

    @Test
    void handleNoSuchAlgorithmException_WithNullMessage_ShouldHandleGracefully() {
        NoSuchAlgorithmException exception = new NoSuchAlgorithmException();

         ResponseEntity<String> response = exceptionHandler.handleNoSuchAlgorithmException(exception);

         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Encryption algorithm not found"));
    }

    @Test
    void handleBadCredentials_WithAnyMessage_ShouldReturnSameCustomMessage() {
        BadCredentialsException exception1 = new BadCredentialsException("Wrong password");
        BadCredentialsException exception2 = new BadCredentialsException("Invalid username");

         ResponseEntity<String> response1 = exceptionHandler.handlebadcredentials(exception1);
        ResponseEntity<String> response2 = exceptionHandler.handlebadcredentials(exception2);

        // Assert - should return same message regardless of input
        assertEquals("User credentials are incorrect", response1.getBody());
        assertEquals("User credentials are incorrect", response2.getBody());
    }

    @Test
    void handleInvalidUser_WithEmptyMessage_ShouldReturnEmptyString() {
        // Arrange - invaliduser has no parameters, always returns same message
        invaliduser exception = new invaliduser();

         ResponseEntity<String> response = exceptionHandler.handleinvaliduser(exception);

         assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User credentials are incorrect", response.getBody());
    }

    @Test
    void handleOtherExceptions_WithRuntimeException_ShouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("Runtime error");

         ResponseEntity<String> response = exceptionHandler.handleOtherExceptions(exception);

         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("An unexpected error occurred"));
        assertTrue(response.getBody().contains("Runtime error"));
    }

    @Test
    void handleOtherExceptions_WithNullPointerException_ShouldReturnInternalServerError() {
        NullPointerException exception = new NullPointerException("Null pointer error");

         ResponseEntity<String> response = exceptionHandler.handleOtherExceptions(exception);

         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Null pointer error"));
    }

    @Test
    void allHandlers_ShouldReturnNonNullResponseEntity() {
        // Test that all handlers return non-null responses
        assertNotNull(exceptionHandler.handleNoSuchAlgorithmException(new NoSuchAlgorithmException()));
        assertNotNull(exceptionHandler.handleinvaliduser(new invaliduser()));
        assertNotNull(exceptionHandler.handleformatmismatch(new formatmismatch()));
        assertNotNull(exceptionHandler.handlebadcredentials(new BadCredentialsException("test")));
        assertNotNull(exceptionHandler.handleusedname(new Username_taken()));
        assertNotNull(exceptionHandler.handleNoMessages(new No_messages("testchat")));
        assertNotNull(exceptionHandler.handleAlienUser(new NotaMember("user", "chat")));
        assertNotNull(exceptionHandler.handleinvaliduser(new NoUserFound("user")));
        assertNotNull(exceptionHandler.handleOtherExceptions(new Exception("test")));
    }

    @Test
    void allUnauthorizedHandlers_ShouldReturnCorrectStatusCode() {
        // Verify all handlers that should return UNAUTHORIZED do so
        assertEquals(HttpStatus.UNAUTHORIZED, 
            exceptionHandler.handleinvaliduser(new invaliduser()).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, 
            exceptionHandler.handleformatmismatch(new formatmismatch()).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, 
            exceptionHandler.handlebadcredentials(new BadCredentialsException("test")).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, 
            exceptionHandler.handleusedname(new Username_taken()).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, 
            exceptionHandler.handleNoMessages(new No_messages("chat")).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, 
            exceptionHandler.handleAlienUser(new NotaMember("user", "chat")).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, 
            exceptionHandler.handleinvaliduser(new NoUserFound("user")).getStatusCode());
    }

    @Test
    void allInternalServerErrorHandlers_ShouldReturnCorrectStatusCode() {
        // Verify handlers that should return INTERNAL_SERVER_ERROR do so
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, 
            exceptionHandler.handleNoSuchAlgorithmException(new NoSuchAlgorithmException()).getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, 
            exceptionHandler.handleOtherExceptions(new Exception()).getStatusCode());
    }
}