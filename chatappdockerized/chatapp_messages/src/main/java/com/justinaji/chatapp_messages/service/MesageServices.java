package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.util.List;

import com.justinaji.chatapp_messages.dto.WSmessage;

public interface MesageServices {

    List<WSmessage> getchathistory(String username , String chatid, String timezone);

    String Sendmessage(String text, String username, String chatid, Timestamp ts);
}

