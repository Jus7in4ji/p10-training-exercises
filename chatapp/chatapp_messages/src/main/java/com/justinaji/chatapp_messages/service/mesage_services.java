package com.justinaji.chatapp_messages.service;

import java.util.List;

import com.justinaji.chatapp_messages.dto.WSmessage;

public interface mesage_services {

    List<WSmessage> getchathistory(String username , String chatid, String timezone);

    String Sendmessage(String text, String username, String chatid);
}

