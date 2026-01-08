package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.messages;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.MediaRepo;
import com.justinaji.chatapp_messages.repository.MessageRepo;

import reactor.core.publisher.Mono;

@ExtendWith (MockitoExtension.class)
class MessageServicesImplTest {

    @Mock
    private MessageRepo messageRepo;

    @Mock
    private MediaRepo mediaRepo;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> headerSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;


    private MessageServicesImpl messageServices;

    @BeforeEach
    void setup() {
        messageServices = new MessageServicesImpl(messageRepo,mediaRepo);
        ReflectionTestUtils.setField(messageServices, "webClient", webClient);
    }



    @Test
    void getchathistory_Success() {
        // Test data
        String chatId = "chat1";
        String timezone = "Asia/Kolkata";
        users sender = new users("u1","mail","justin","pwd",true,null);
        chats chat = new chats(chatId, "GroupA", sender, true, "key123");

        messages message = new messages("m1", "encryptedText", sender, chat,
                Timestamp.from(Instant.now()), false);

        List<messages> messageList = Arrays.asList(message);

        // Mock WebClient 
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(chats.class)).thenReturn(Mono.just(chat));


        // Mock repo
        when(messageRepo.findByChatOrderBySentTimeAsc(chat)).thenReturn(messageList);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(() -> CommonMethods.decryptMessage("encryptedText", "key123"))
                    .thenReturn("Hello");
            utilities.when(() -> CommonMethods.formatTimestamp(any(), eq(timezone)))
                    .thenReturn("formatted-date");

            List<WSmessage> result = messageServices.getchathistory("justin", chatId, timezone);

            assertEquals(1, result.size());
            assertEquals("Hello", result.get(0).getText());
            assertEquals("formatted-date", result.get(0).getSentTime());
        }
    }

    @Test
    void getchathistory_NullUsername() {
        List<WSmessage> result = messageServices.getchathistory(null, "chat1", "Asia/Kolkata");
        assertNull(result);
    }

    @Test
    void sendmessage_Success() {

        String chatid = "chat1";
        String user = "justin";

        users sender = new users("u1","mail","justin","pwd",true,null);
        chats chat = new chats(chatid, "GroupA", sender, true, "key123");

        when(messageRepo.existsById(anyString())).thenReturn(false);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/userchat/getuser?username=" + user)).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(users.class)).thenReturn(Mono.just(sender));

        when(uriSpec.uri("/userchat/getchat?chatid=" + chatid)).thenReturn(uriSpec);
        when(responseSpec.bodyToMono(chats.class)).thenReturn(Mono.just(chat));

        try (MockedStatic<CommonMethods> staticMock = mockStatic(CommonMethods.class)) {
            staticMock.when(CommonMethods::getAlphaNumericString).thenReturn("mid1");
            staticMock.when(() -> CommonMethods.encryptMessage("Hello","key123")).thenReturn("encrypted");

            String result = messageServices.Sendmessage("Hello", user, chatid);
            
            assertEquals("mid1", result);
            verify(messageRepo, times(1)).save(any());
        }
    }

    @Test
    void setread_Success() {
        messages m = new messages("m1", "txt", null, null, Timestamp.from(Instant.now()), false);
        when(messageRepo.findById("m1")).thenReturn(Optional.of(m));

        messageServices.setread("m1");

        assertTrue(m.isMsgread());
        verify(messageRepo).save(m);
    }

}