package com.justinaji.chatapp_userchats.service;

import java.util.HashMap;

import com.justinaji.chatapp_userchats.dto.addmember;
import com.justinaji.chatapp_userchats.dto.chatdetails;

public interface  ChatServices {

    chatdetails CreateChat( addmember newGroup);

    String getChats();

    String getMembers(String chat);

    String AddMember(String chat, String name, boolean isadmin);

    String RemoveMember(String chat, String name);

    String Makeadmin(String chat, String name);

    String LeaveGroup(String chat);

    HashMap<String,String> ischatvalid(String chatname, String username, boolean isgroup);
}
