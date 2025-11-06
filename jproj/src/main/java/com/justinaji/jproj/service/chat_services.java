package com.justinaji.jproj.service;

import com.justinaji.jproj.model.addmember;
import com.justinaji.jproj.model.chats;

public interface  chat_services {

    chats CreateChat( addmember newGroup);

    String getChats();

}
