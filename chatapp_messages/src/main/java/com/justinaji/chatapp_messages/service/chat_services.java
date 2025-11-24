package com.justinaji.chatapp_messages.service;

import com.justinaji.chatapp_messages.dto.addmember;
import com.justinaji.chatapp_messages.dto.chatdetails;

public interface  chat_services {

    chatdetails CreateChat( addmember newGroup);

    String getChats();

    String getMembers(String chat);

    String AddMember(String chat, String name, boolean isadmin);

    String RemoveMember(String chat, String name);

    String Makeadmin(String chat, String name);

    String LeaveGroup(String chat);
}
