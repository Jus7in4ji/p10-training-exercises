package com.justinaji.chatapp_userchats.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.service.ChatServicesImpl;
import com.justinaji.chatapp_userchats.service.CommonMethods;

@ExtendWith(MockitoExtension.class)
public class MessageCommunicationsTest {
 
@Mock private  ChatServicesImpl chatServicesImpl;

@InjectMocks
private MessageCommunications messageCommunications;
private users CurrentUser;

    @BeforeEach
    void setup() {
        CurrentUser = new users("u1","mail1@domain.com", "justin", "pwd", true, null);
    }


    // getname
    @Test
    void getname_Success(){
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(CurrentUser);

            Map<String , String> result = messageCommunications.getusername();

            assertEquals(CurrentUser.getName(), result.get("username"));
            
        }
    }

    // get UsernameStatus
    @Test
    void getUsernameStatus_success(){
        Boolean status = false;
        CurrentUser.setStatus(status);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(CurrentUser);

            Map<String , String> result = messageCommunications.getUsernameStatus();

            assertEquals(CurrentUser.getName(), result.get("username"));
            assertEquals(Boolean.toString(status), result.get("active"));
            
        }
    }

    @Test
    void getUsernameStatus_NoAuth(){
        Boolean status = false;
        CurrentUser.setStatus(status);

        Map<String , String> result = messageCommunications.getUsernameStatus();

        assertEquals("", result.get("username"));
        assertEquals("false", result.get("active"));
            
    }

    // SubscribeRoom
    @Test
    void subscribeRoom_Success(){
        HashMap<String, String> m = new HashMap<>();
        Map<String,String> m2 = new HashMap<>();

        m.put("Status","Success");
        m.put("roomid","randomrooid");
        when(chatServicesImpl.ischatvalid("room", "username", true)).thenReturn( m);

        m2.put("room","room");
        m2.put("user","username");
        m2.put("isGroup",Boolean.toString(true));

        Map<String,String> result = messageCommunications.subscribeRoom(m2);

        assertEquals(result.get("Room"), "room");
    }

    @Test
    void subscribeRoom_Failure(){
        HashMap<String, String> m = new HashMap<>();
        Map<String,String> m2 = new HashMap<>();

        m.put("Status","Failure");
        m.put("roomid","randomrooid");
        when(chatServicesImpl.ischatvalid("room", "username", true)).thenReturn( m);

        m2.put("room","room");
        m2.put("user","username");
        m2.put("isGroup",Boolean.toString(true));

        Map<String,String> result = messageCommunications.subscribeRoom(m2);

        assertEquals(result.get("Room"), "[None]");
    }

    @Test
    void subscribeRoom_RoomNull(){
        HashMap<String, String> m = new HashMap<>();
        Map<String,String> m2 = new HashMap<>();

        m.put("Status","Failure");
        m.put("roomid","randomrooid");
        when(chatServicesImpl.ischatvalid(null, "username", true)).thenReturn( m);

        m2.put("room",null);
        m2.put("user","username");
        m2.put("isGroup",Boolean.toString(true));

        Map<String,String> result = messageCommunications.subscribeRoom(m2);

        assertEquals(result.get("Status"), "Disconnected from Chat");
    }
}
