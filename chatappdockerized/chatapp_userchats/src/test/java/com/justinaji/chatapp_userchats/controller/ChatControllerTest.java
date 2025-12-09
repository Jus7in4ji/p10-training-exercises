package com.justinaji.chatapp_userchats.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justinaji.chatapp_userchats.dto.UserChat;
import com.justinaji.chatapp_userchats.dto.UserEditRequest;
import com.justinaji.chatapp_userchats.dto.addmember;
import com.justinaji.chatapp_userchats.dto.chatdetails;
import com.justinaji.chatapp_userchats.service.ChatServicesImpl;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatServicesImpl chatService;

    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void GetChats_ReturnChatsList() throws Exception {
        // Arrange
        String expectedChats = "[{\"chatname\":\"Group1\"},{\"chatname\":\"Group2\"}]";
        when(chatService.getChats()).thenReturn(expectedChats);

        // Act & Assert
        mockMvc.perform(get("/chats"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedChats));

        verify(chatService, times(1)).getChats();
    }

    @Test
    void GetMembers_WithGroupName_ReturnMembersList() throws Exception {
        // Arrange
        String groupName = "TestGroup";
        String expectedMembers = "[{\"username\":\"user1\"},{\"username\":\"user2\"}]";
        when(chatService.getMembers(groupName)).thenReturn(expectedMembers);

        // Act & Assert
        mockMvc.perform(get("/chats/members")
                .param("groupname", groupName))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMembers));

        verify(chatService, times(1)).getMembers(groupName);
    }

    @Test
    void GetMembers_WithoutGroupName_ReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/chats/members"))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).getMembers(anyString());
    }

    @Test
    void AddMember_WithValidRequest_ReturnSuccessMessage() throws Exception {
        // Arrange
        UserEditRequest request = new UserEditRequest("TestGroup","newuser",false);
        
        String expectedResponse = "Member added successfully";
        when(chatService.AddMember("TestGroup", "newuser", false)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/chats/addmember")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(chatService, times(1)).AddMember("TestGroup", "newuser", false);
    }

    @Test
    void RemoveMember_WithValidRequest_ReturnSuccessMessage() throws Exception {
        // Arrange
        UserChat request = new UserChat("TestGroup","usertoremove");
        
        String expectedResponse = "Member removed successfully";
        when(chatService.RemoveMember("TestGroup", "usertoremove")).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put("/chats/removemember")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(chatService, times(1)).RemoveMember("TestGroup", "usertoremove");
    }

    @Test
    void LeaveChat_WithChatName_ReturnSuccessMessage() throws Exception {
        // Arrange
        String chatName = "TestGroup";
        String expectedResponse = "Left chat successfully";
        when(chatService.LeaveGroup(chatName)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put("/chats/leave")
                .param("chatname", chatName))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(chatService, times(1)).LeaveGroup(chatName);
    }

    @Test
    void LeaveChat_WithoutChatName_ReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/chats/leave"))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).LeaveGroup(anyString());
    }

    @Test
    void MakeAdmin_Success() throws Exception {
        // Arrange
        UserChat request = new UserChat("TestGroup","usertopromote");
        
        String expectedResponse = "User promoted to admin";
        when(chatService.Makeadmin("TestGroup", "usertopromote")).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put("/chats/makeadmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(chatService, times(1)).Makeadmin("TestGroup", "usertopromote");
    }

    @Test
    void CreateChat_InvalidJson_BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/chats/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).CreateChat(any());
    }

    @Test
    void AddMember_WithInvalidJson_ReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/chats/addmember")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).AddMember(anyString(), anyString(), anyBoolean());
    }



    @Test
    void GetChats_MultipleCalls_CallServiceEachTime() throws Exception {
        // Arrange
        when(chatService.getChats()).thenReturn("[]");

        // Act
        mockMvc.perform(get("/chats"));
        mockMvc.perform(get("/chats"));
        mockMvc.perform(get("/chats"));

        // Assert
        verify(chatService, times(3)).getChats();
    }

    @Test
    void GetMembers_WithSpecialCharactersInGroupName_PassCorrectly() throws Exception {
        // Arrange
        String groupName = "Test Group #1";
        when(chatService.getMembers(anyString())).thenReturn("[]");

        // Act
        mockMvc.perform(get("/chats/members")
                .param("groupname", groupName))
                .andExpect(status().isOk());

        // Assert
        verify(chatService).getMembers("Test Group #1");
    }

    @Test
    void LeaveChat_WithSpecialCharactersInChatName_PassCorrectly() throws Exception {
        // Arrange
        String chatName = "Group@123";
        when(chatService.LeaveGroup(anyString())).thenReturn("Success");

        // Act
        mockMvc.perform(put("/chats/leave")
                .param("chatname", chatName))
                .andExpect(status().isOk());

        // Assert
        verify(chatService).LeaveGroup("Group@123");
    }

}