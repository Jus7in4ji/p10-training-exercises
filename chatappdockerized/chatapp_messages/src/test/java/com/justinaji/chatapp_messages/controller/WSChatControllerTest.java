package com.justinaji.chatapp_messages.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.justinaji.chatapp_messages.dto.ReadRequest;
import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.service.MessageServicesImpl;

public class WSChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessageServicesImpl msgservice;

    @InjectMocks
    private WSChatcontroller wscontroller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void SendMessage_Success() {

        WSmessage message = new WSmessage(
                null,
                "justin",       // from
                "hello world",  // text
                null,           // sent time
                "room123",      // room id
                false
        );

        when(msgservice.Sendmessage("hello world", "justin", "room123"))
                .thenReturn("generatedMsgId");

        wscontroller.sendMessage(message);

        // verify Sendmessage() is called correctly
        verify(msgservice, times(1))
                .Sendmessage("hello world", "justin", "room123");

        // verify convertAndSend() is called
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/room123"), any(WSmessage.class));
    }

    @Test
    void SendMessage_NoUsername_NotSend() {
        WSmessage message = new WSmessage(null, "   ", "text", null, "room123", false);

        wscontroller.sendMessage(message);

        verify(msgservice, never()).Sendmessage(any(), any(), any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(WSmessage.class));
    }

    @Test
    void SendMessage_NoRoom_NotSend() {
        WSmessage message = new WSmessage(null, "justin", "text", null, null, false);

        wscontroller.sendMessage(message);

        verify(msgservice, never()).Sendmessage(any(), any(), any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(WSmessage.class));
    }

    @Test
    void markRead_Success(){
        ReadRequest rr = new ReadRequest("id");
        wscontroller.markRead(rr);

        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/read"), eq("id"));
        verify(msgservice, times(1)).setread(eq("id"));
    }
}
