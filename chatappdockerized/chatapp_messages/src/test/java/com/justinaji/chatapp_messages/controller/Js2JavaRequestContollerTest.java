package com.justinaji.chatapp_messages.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.service.MessageServicesImpl;

class Js2JavaRequestControllerTest {

    @Mock
    private MessageServicesImpl msgservice;

    @InjectMocks
    private Js2JavaRequestController controller;

    public Js2JavaRequestControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void GetHistory_Success() {
        // Arrange
        Map<String,String> payload = new HashMap<>();
        payload.put("user", "justin");
        payload.put("chatid", "chat123");
        payload.put("timezone", "UTC");

        List<WSmessage> mockResponse = List.of(
            new WSmessage("id1", "justin", "Hi", "10:00 AM", "chat123", false)
        );

        when(msgservice.getchathistory("justin", "chat123", "UTC")).thenReturn(mockResponse);

        // Act
        List<WSmessage> result = controller.RetrieveChatHistory(payload);

        // Assert
        assertEquals(mockResponse, result);
        verify(msgservice).getchathistory("justin", "chat123", "UTC");
    }
}
