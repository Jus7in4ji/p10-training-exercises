package com.justinaji.chatapp_userchats.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.justinaji.chatapp_userchats.dto.addmember;
import com.justinaji.chatapp_userchats.dto.chatdetails;
import com.justinaji.chatapp_userchats.dto.chatmember;
import com.justinaji.chatapp_userchats.exception.*;
import com.justinaji.chatapp_userchats.model.*;
import com.justinaji.chatapp_userchats.repository.*;

@ExtendWith(MockitoExtension.class)
class ChatServicesImplTest {

    @Mock private ChatRepo chatRepo;
    @Mock private MemberRepo memberRepo;
    @Mock private UserRepo urepo;
    @Mock private LogRepo logRepo;

    @InjectMocks
    private ChatServicesImpl chatServices;
    private users creator;

    @BeforeEach
    void setup() {
        creator = new users("u1","mail1@domain.com", "justin", "pwd", true, null);
    }

    // CreateChat 
    @Test
    void CreateChat_Success() {
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {

            List<chatmember> membersList = new ArrayList<>();
            membersList.add(new chatmember("alex", false));
            membersList.add(new chatmember("kannan", false));

            addmember dto = new addmember("TestGroup", membersList);

            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            utilities.when(CommonMethods::getAlphaNumericString).thenReturn("chat123");
            utilities.when(CommonMethods::generateKey).thenReturn("key123");

            users userAlex = new users("u2","mail2@domain.com", "alex", "pass", true, null);
            users userKannan = new users("u3","mail3@domain.com", "kannan", "pass", true, null);

            when(chatRepo.existsByName("TestGroup")).thenReturn(false);
            when(urepo.existsByName("TestGroup")).thenReturn(false);
            when(chatRepo.existsById("chat123")).thenReturn(false);

            when(urepo.existsByName("justin")).thenReturn(true);
            when(urepo.findByName("justin")).thenReturn(creator);

            when(urepo.existsByName("alex")).thenReturn(true);
            when(urepo.findByName("alex")).thenReturn(userAlex);

            when(urepo.existsByName("kannan")).thenReturn(true);
            when(urepo.findByName("kannan")).thenReturn(userKannan);

            when(logRepo.existsById(anyString())).thenReturn(false);

            chatdetails result = chatServices.CreateChat(dto);

            assertEquals("TestGroup", result.getChatname());
            assertEquals(3, result.getMember_count());
            assertTrue(result.getMembers().contains("justin"));
            assertTrue(result.getMembers().contains("alex"));
            assertTrue(result.getMembers().contains("kannan"));

            verify(chatRepo, times(1)).save(any(chats.class));
            verify(memberRepo, times(3)).save(any(members.class)); // creator + alex
            verify(logRepo, times(1)).save(any(Logs.class));
        }
    }

    @Test
    void CreateChat_NameAlreadyExists() {
        List<chatmember> membersList = List.of(new chatmember("alex", false),new chatmember("kannan", false) );
        addmember dto = new addmember("TestGroup", membersList);

        when(chatRepo.existsByName("TestGroup")).thenReturn(true);

        assertThrows(Username_taken.class, () -> chatServices.CreateChat(dto));
    }

    @Test
    void CreateChat_InvalidFormat() {
        addmember dto = new addmember("", List.of(new chatmember("alex", false)));

        assertThrows(formatmismatch.class, () -> chatServices.CreateChat(dto));
    }

    // Addmember
    @Test
    void AddMember_AlreadyMember() {
        chats c = new chats("chat1", "GroupA", creator, true, "key");
        members existing = new members(c, creator, true);

        when(chatRepo.findByName("GroupA")).thenReturn(c);
        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        when(memberRepo.findByChat(c)).thenReturn(List.of(existing));

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            String msg = chatServices.AddMember("GroupA", "justin", false);
            assertEquals("Given User is already a member", msg);
        }
    }

    @Test
    void AddMember_NoUserFound() {
        chats c = new chats("chat1", "GroupA", creator, true, "key");
        members m1 = new members(c, creator, true);

        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        when(chatRepo.findByName("GroupA")).thenReturn(c);
        when(memberRepo.findByChat(c)).thenReturn(List.of(m1));
        when(urepo.existsByName("alex")).thenReturn(false);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            assertThrows(NoUserFound.class, () -> chatServices.AddMember("GroupA", "alex", false));
        }
    }
    
    // RemoveMember
    @Test
    void RemoveMember_NotAMember(){
        chats c = new chats("chat1", "GroupA", creator, true, "key");
        members m1 = new members(c, creator, true);

        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        when(chatRepo.findByName("GroupA")).thenReturn(c);
        when(memberRepo.findByChat(c)).thenReturn(List.of(m1));

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);

            assertThrows(NotaMember.class, () -> chatServices.RemoveMember("GroupA", "alex"));
        }

    }

    @Test
    void RemoveMember_UserIsAdmin(){
        chats c = new chats("chat1", "GroupA", creator, true, "key");
        members m1 = new members(c, creator, true);
        
        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        when(chatRepo.findByName("GroupA")).thenReturn(c);
        when(memberRepo.findByChat(c)).thenReturn(List.of(m1));

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            String result = chatServices.RemoveMember("GroupA", "justin");

            assertEquals("Unable to remove: The given user is also an admin ", result);}

    }

    // LeaveGroup
    @Test
    void LeaveGroup_NotAMember(){
        
        users userAlex = new users("u2","mail2@domain.com", "alex", "pass", true, null);
        chats c = new chats("chat1", "GroupA", userAlex, true, "key");
        members m1 = new members(c, userAlex, true);

        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        when(chatRepo.findByName("GroupA")).thenReturn(c);
        when(memberRepo.findByChat(c)).thenReturn(List.of(m1));

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            
        assertThrows(NotaMember.class, () -> chatServices.LeaveGroup("GroupA"));  
        }


    }

    @Test
    void LeaveGroup_Success(){
        chats c = new chats("chat1", "GroupA", null, true, "key");
        members m1 = new members(c, creator, true);

        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        when(chatRepo.findByName("GroupA")).thenReturn(c);
        when(memberRepo.findByChat(c)).thenReturn(List.of(m1));
        
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            String result = chatServices.LeaveGroup("GroupA");
            assertEquals("You are no longer a member of GroupA", result);    
        }
    }

    //Userauthority
    @Test
    void UserAuthority_NotAMember(){
        users userAlex = new users("u2","mail2@domain.com", "alex", "pass", true, null);
        chats c = new chats("chat1", "GroupA", userAlex, true, "key");
        members m1 = new members(c, userAlex, true);

        when(chatRepo.existsByName("GroupA")).thenReturn(true);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            
        assertThrows(NotaMember.class, () -> chatServices.Userauthority("GroupA", List.of(m1)));  
        }
    }

    @Test
    void UserAuthority_NotAdmin(){
        chats c = new chats("chat1", "GroupA", null, true, "key");
        members m1 = new members(c, creator, false);

        when(chatRepo.existsByName("GroupA")).thenReturn(true);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            String result = chatServices.Userauthority("GroupA", List.of(m1));
            assertEquals("You do not have Admin privileges in this chat.", result); 
        }
    }

    //Getmembers
    @Test
    void getMembers_noGroupExists(){
        when(chatRepo.existsByName("GroupA")).thenReturn(false);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            String result = chatServices.getMembers("GroupA");
            assertEquals("There exists no group of name 'GroupA'.", result); 
        }

    }

    @Test
    void getMembers_NotAMember(){
        users userAlex = new users("u2","mail2@domain.com", "alex", "pass", true, null);
        chats c = new chats("chat1", "GroupA", userAlex, true, "key");
        members m1 = new members(c, userAlex, true);

        when(chatRepo.existsByName("GroupA")).thenReturn(true);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            String result = chatServices.getMembers("GroupA");
            assertEquals("No members", result); 
        }
    }

    @Test
    void getMembers_EmptyGroup(){
        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            String result = chatServices.getMembers("GroupA");
            assertEquals("No members", result); 
        }
    }

    // ischatvalid
    @Test
    void ischatvalid_NogroupExists(){
        when(chatRepo.existsByName("chatname")).thenReturn(false);
        
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            HashMap<String , String> result = chatServices.ischatvalid("chatname", "testuser", true);
            assertEquals("No Chat of name chatname exists.", result.get("Status")); 
        }
    }

    @Test
    void ischatvalid_NoUserExists(){
        when(urepo.existsByName("justin")).thenReturn(false);
        
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            HashMap<String , String> result = chatServices.ischatvalid("justin", "testuser", false);
            assertEquals("No User of name justin exists.", result.get("Status")); 
        }
    }

    @Test
    void ischatvalid_NotAMember(){
        users userAlex = new users("u2","mail2@domain.com", "alex", "pass", true, null);
        chats c = new chats("chat1", "GroupA", userAlex, true, "key");
        members m1 = new members(c, userAlex, false);
        members m2 = new members(c, creator, false);

        List<members> members = Arrays.asList(m1,m2);

        when(memberRepo.findByChat(c)).thenReturn(members);
        when(chatRepo.existsByName("GroupA")).thenReturn(true);
        when(chatRepo.findByName("GroupA")).thenReturn(c);


        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            HashMap<String , String> result = chatServices.ischatvalid("GroupA", "kannan", true);
            assertEquals("kannan is not a member of GroupA.", result.get("Status")); 
        }
    }

    @Test
    void ischatvalid_OwnUsername(){
        when(urepo.existsByName("justin")).thenReturn(true);
        
        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            HashMap<String , String> result = chatServices.ischatvalid("justin", "justin", false);
            assertEquals("Make sure the username isn't your own.", result.get("Status")); 
        }
    }

    @Test
    void ischatvalid_Success(){
        users userAlex = new users("u2","mail2@domain.com", "alex", "pass", true, null);
        chats c = new chats("chat1", "GroupA", null, false, "key");
        members m1 = new members(c, userAlex, false);
        members m2 = new members(c, creator, false);

        List<members> creatorChats = Arrays.asList(m2);
        List<members> alexChats = Arrays.asList(m1);

        when(urepo.existsByName("alex")).thenReturn(true);
        when(urepo.findByName("alex")).thenReturn(userAlex);

        when(urepo.findByName("justin")).thenReturn(creator);

        when(memberRepo.findByMember(creator)).thenReturn(creatorChats);
        when(memberRepo.findByMember(userAlex)).thenReturn(alexChats);

        try (MockedStatic<CommonMethods> utilities = mockStatic(CommonMethods.class)) {
            utilities.when(CommonMethods::getCurrentUser).thenReturn(creator);
            HashMap<String , String> result = chatServices.ischatvalid("alex", "justin", false);
            assertEquals("Success", result.get("Status")); 
        }
    }

}
