package com.justinaji.chatapp_userchats.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.justinaji.chatapp_userchats.dto.addmember;
import com.justinaji.chatapp_userchats.dto.chatdetails;
import com.justinaji.chatapp_userchats.dto.chatmember;
import com.justinaji.chatapp_userchats.exception.NoUserFound;
import com.justinaji.chatapp_userchats.exception.NotaMember;
import com.justinaji.chatapp_userchats.exception.Username_taken;
import com.justinaji.chatapp_userchats.exception.formatmismatch;
import com.justinaji.chatapp_userchats.model.Logs;
import com.justinaji.chatapp_userchats.model.chats;
import com.justinaji.chatapp_userchats.model.members;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.ChatRepo;
import com.justinaji.chatapp_userchats.repository.LogRepo;
import com.justinaji.chatapp_userchats.repository.MemberRepo;
import com.justinaji.chatapp_userchats.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class ChatServicesImplTest {

    @Mock private ChatRepo chatRepo;
    @Mock private MemberRepo memberRepo;
    @Mock private UserRepo urepo;
    @Mock private LogRepo logRepo;

    @InjectMocks
    private ChatServicesImpl chatServices;
    private users creator;
    private users userAlex;
    private users userKannan;

    @BeforeEach
    void setup() {
        creator = new users("u1", "mail1@domain.com", "justin", "pwd", true, null);
        userAlex = new users("u2", "mail2@domain.com", "alex", "pass", true, null);
        userKannan = new users("u3", "mail3@domain.com", "kannan", "pass", true, null);
    }

    // ============ CreateChat Additional Tests ============
    
    @Test
    void CreateChat_NullName_ThrowsFormatMismatch() {
        List<chatmember> membersList = List.of(
            new chatmember("alex", false),
            new chatmember("kannan", false)
        );
        addmember dto = new addmember(null, membersList);

        assertThrows(formatmismatch.class, () -> chatServices.CreateChat(dto));
    }

    @Test
    void CreateChat_OnlyOneMember_ThrowsFormatMismatch() {
        List<chatmember> membersList = List.of(new chatmember("alex", false));
        addmember dto = new addmember("TestGroup", membersList);

        assertThrows(formatmismatch.class, () -> chatServices.CreateChat(dto));
    }

    @Test
    void CreateChat_UsernameConflict_ThrowsUsernameTaken() {
        List<chatmember> membersList = List.of(
            new chatmember("alex", false),
            new chatmember("kannan", false)
        );
        addmember dto = new addmember("TestGroup", membersList);

        when(chatRepo.existsByName("TestGroup")).thenReturn(false);
        when(urepo.existsByName("TestGroup")).thenReturn(true);

        assertThrows(Username_taken.class, () -> chatServices.CreateChat(dto));
    }

    @Test
    void CreateChat_MemberNotFound_ThrowsNoUserFound() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            List<chatmember> membersList = new ArrayList<>(List.of(
                new chatmember("alex", false),
                new chatmember("nonexistent", false)
            ));

            addmember dto = new addmember("TestGroup", membersList);

            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("chat123");
            utilities.when(CommonMethods::generateKey).thenReturn("key123");

            when(chatRepo.existsByName("TestGroup")).thenReturn(false);
            when(urepo.existsByName("TestGroup")).thenReturn(false);
            when(chatRepo.existsById("chat123")).thenReturn(false);

            when(urepo.existsByName("alex")).thenReturn(true);
            when(urepo.findByName("alex")).thenReturn(userAlex);
            when(urepo.existsByName("nonexistent")).thenReturn(false);

            assertThrows(NoUserFound.class, () -> chatServices.CreateChat(dto));
        }
    }

    @Test
    void CreateChat_CreatorAutomaticallyAddedAsAdmin() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            List<chatmember> membersList = new ArrayList<>(List.of(
                new chatmember("alex", false),
                new chatmember("kannan", false)
            ));
            addmember dto = new addmember("TestGroup", membersList);

            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("chat123");
            utilities.when(CommonMethods::generateKey).thenReturn("key123");

            when(chatRepo.existsByName("TestGroup")).thenReturn(false);
            when(urepo.existsByName("TestGroup")).thenReturn(false);
            when(chatRepo.existsById("chat123")).thenReturn(false);
            when(urepo.existsByName("justin")).thenReturn(true);
            when(urepo.findByName("justin")).thenReturn(creator);
            when(urepo.existsByName("kannan")).thenReturn(true);
            when(urepo.findByName("kannan")).thenReturn(userKannan);
            when(urepo.existsByName("alex")).thenReturn(true);
            when(urepo.findByName("alex")).thenReturn(userAlex);
            when(logRepo.existsById(anyString())).thenReturn(false);

            chatdetails result = chatServices.CreateChat(dto);

            assertTrue(result.getMembers().contains("justin"));
            assertEquals(3, result.getMember_count());
        }
    }

    // ============ getChats Additional Tests ============

    @Test
    void getChats_NoChats_ReturnsMessage() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            when(urepo.findById("u1")).thenReturn(Optional.of(creator));
            when(memberRepo.findByMember(creator)).thenReturn(Collections.emptyList());

            String result = chatServices.getChats();

            assertEquals("You have no chats yet.", result);
        }
    }

    @Test
    void getChats_OnlyGroupChats_ReturnsGroupList() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            chats groupChat1 = new chats("c1", "DevTeam", creator, true, "key1");
            chats groupChat2 = new chats("c2", "ProjectX", creator, true, "key2");

            members m1 = new members(groupChat1, creator, true);
            members m2 = new members(groupChat2, creator, false);

            when(urepo.findById("u1")).thenReturn(Optional.of(creator));
            when(memberRepo.findByMember(creator)).thenReturn(Arrays.asList(m1, m2));

            String result = chatServices.getChats();

            assertTrue(result.contains("DevTeam"));
            assertTrue(result.contains("ProjectX"));
            assertTrue(result.contains("Groupchat"));
        }
    }

    @Test
    void getChats_MixedGroupAndPrivate_ReturnsBoth() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            chats groupChat = new chats("c1", "DevTeam", creator, true, "key1");
            chats privateChat = new chats("c2", "private", null, false, "key2");

            members m1 = new members(groupChat, creator, true);
            members m2 = new members(privateChat, creator, false);
            members m3 = new members(privateChat, userAlex, false);

            when(urepo.findById("u1")).thenReturn(Optional.of(creator));
            when(memberRepo.findByMember(creator)).thenReturn(Arrays.asList(m1, m2));
            when(memberRepo.findByChat(privateChat)).thenReturn(Arrays.asList(m2, m3));

            String result = chatServices.getChats();

            assertTrue(result.contains("DevTeam"));
            assertTrue(result.contains("alex"));
        }
    }

    @Test
    void getChats_PrivateChatWithUnknownUser_HandlesGracefully() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            chats privateChat = new chats("c1", "private", null, false, "key1");
            members m1 = new members(privateChat, creator, false);

            when(urepo.findById("u1")).thenReturn(Optional.of(creator));
            when(memberRepo.findByMember(creator)).thenReturn(List.of(m1));
            when(memberRepo.findByChat(privateChat)).thenReturn(List.of(m1));

            String result = chatServices.getChats();

            assertTrue(result.contains("Personal chat (unknown user)"));
        }
    }

    // ============ AddMember Additional Tests ============

    @Test
    void AddMember_Success_ReturnsConfirmation() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("log123");

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, true);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(List.of(m1));
            when(urepo.existsByName("alex")).thenReturn(true);
            when(urepo.findByName("alex")).thenReturn(userAlex);
            when(logRepo.existsById(anyString())).thenReturn(false);

            String result = chatServices.AddMember("GroupA", "alex", false);

            assertEquals("alex has been added into GroupA", result);
            verify(memberRepo, times(1)).save(any(members.class));
            verify(logRepo, times(1)).save(any(Logs.class));
        }
    }

    @Test
    void AddMember_AsAdmin_SetsAdminFlag() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("log123");

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, true);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(List.of(m1));
            when(urepo.existsByName("alex")).thenReturn(true);
            when(urepo.findByName("alex")).thenReturn(userAlex);
            when(logRepo.existsById(anyString())).thenReturn(false);

            String result = chatServices.AddMember("GroupA", "alex", true);

            assertEquals("alex has been added into GroupA", result);
            verify(memberRepo).save(argThat(member -> member.isAdmin()));
        }
    }

    @Test
    void AddMember_ChatDoesNotExist_ReturnsError() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            when(chatRepo.existsByName("NonExistent")).thenReturn(false);

            String result = chatServices.AddMember("NonExistent", "alex", false);

            assertEquals("There exists no group of name 'NonExistent'.", result);
        }
    }

    @Test
    void AddMember_UserNotAdmin_ReturnsError() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, false); // Not admin

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(List.of(m1));

            String result = chatServices.AddMember("GroupA", "alex", false);

            assertEquals("You do not have Admin privileges in this chat.", result);
        }
    }

    // ============ RemoveMember Additional Tests ============

    @Test
    void RemoveMember_Success_ReturnsConfirmation() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("log123");

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, true);
            members m2 = new members(c, userAlex, false);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(Arrays.asList(m1, m2));
            when(logRepo.existsById(anyString())).thenReturn(false);

            String result = chatServices.RemoveMember("GroupA", "alex");

            assertEquals("alex has been removed from GroupA", result);
            verify(memberRepo, times(1)).delete(m2);
            verify(logRepo, times(1)).save(any(Logs.class));
        }
    }

    @Test
    void RemoveMember_ChatDoesNotExist_ReturnsError() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            when(chatRepo.existsByName("NonExistent")).thenReturn(false);

            String result = chatServices.RemoveMember("NonExistent", "alex");

            assertEquals("There exists no group of name 'NonExistent'.", result);
        }
    }

    // ============ Makeadmin Additional Tests ============

    @Test
    void Makeadmin_Success_ReturnsConfirmation() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("log123");

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, true);
            members m2 = new members(c, userAlex, false);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(Arrays.asList(m1, m2));
            when(logRepo.existsById(anyString())).thenReturn(false);

            String result = chatServices.Makeadmin("GroupA", "alex");

            assertEquals("alex is now an admin of GroupA", result);
            assertTrue(m2.isAdmin());
            verify(memberRepo, times(1)).save(m2);
            verify(logRepo, times(1)).save(any(Logs.class));
        }
    }

    @Test
    void Makeadmin_UserNotFound_ThrowsNotaMember() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, true);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(List.of(m1));

            assertThrows(NotaMember.class, () -> chatServices.Makeadmin("GroupA", "alex"));
        }
    }

    @Test
    void Makeadmin_ChatDoesNotExist_ReturnsError() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            when(chatRepo.existsByName("NonExistent")).thenReturn(false);

            String result = chatServices.Makeadmin("NonExistent", "alex");

            assertEquals("There exists no group of name 'NonExistent'.", result);
        }
    }

    // ============ LeaveGroup Additional Tests ============

    @Test
    void LeaveGroup_ChatDoesNotExist_ReturnsError() {
        when(chatRepo.existsByName("NonExistent")).thenReturn(false);

        String result = chatServices.LeaveGroup("NonExistent");

        assertEquals("There exists no group of name 'NonExistent'.", result);
    }

    @Test
    void LeaveGroup_CreatesLog() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("log123");

            chats c = new chats("chat1", "GroupA", null, true, "key");
            members m1 = new members(c, creator, true);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(List.of(m1));
            when(logRepo.existsById(anyString())).thenReturn(false);

            chatServices.LeaveGroup("GroupA");

            verify(logRepo, times(1)).save(any(Logs.class));
            verify(memberRepo, times(1)).delete(m1);
        }
    }

    // ============ getMembers Additional Tests ============

    @Test
    void getMembers_Success_ReturnsFormattedList() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, true);
            members m2 = new members(c, userAlex, false);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(Arrays.asList(m1, m2));

            String result = chatServices.getMembers("GroupA");

            assertTrue(result.contains("Chat GroupA"));
            assertTrue(result.contains("justin"));
            assertTrue(result.contains("alex"));
            assertTrue(result.contains("(Admin)"));
        }
    }

    // ============ ischatvalid Additional Tests ============

    @Test
    void ischatvalid_GroupChat_Success() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            chats c = new chats("chat1", "GroupA", creator, true, "key");
            members m1 = new members(c, creator, true);

            when(chatRepo.existsByName("GroupA")).thenReturn(true);
            when(chatRepo.findByName("GroupA")).thenReturn(c);
            when(memberRepo.findByChat(c)).thenReturn(List.of(m1));

            HashMap<String, String> result = chatServices.ischatvalid("GroupA", "justin", true);

            assertEquals("Success", result.get("Status"));
            assertEquals("chat1", result.get("roomid"));
        }
    }

    @Test
    void ischatvalid_PrivateChat_NullRoomId() {
        when(urepo.existsByName("alex")).thenReturn(false);

        HashMap<String, String> result = chatServices.ischatvalid("alex", "justin", false);

        assertNull(result.get("roomid"));
        assertEquals("No User of name alex exists.", result.get("Status"));
    }
}