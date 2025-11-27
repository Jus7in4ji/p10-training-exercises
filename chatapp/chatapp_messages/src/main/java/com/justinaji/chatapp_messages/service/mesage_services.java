package com.justinaji.chatapp_messages.service;

import java.util.HashMap;
import java.util.List;

import com.justinaji.chatapp_messages.dto.messageDTO;
import com.justinaji.chatapp_messages.dto.WSmessage;

public interface mesage_services {

    List<messageDTO> GetPvtmessages(String chatname) ;

    void SendPvtMessage(String chatname, String message) ;

    List<messageDTO> GetGrpmessages(String chatname) ;

    void SendGrpMessage(String chatname, String message) ;

    HashMap<String,String> ischatvalid(String chatname, String username, boolean isgroup);

    List<WSmessage> getchathistory(String username , String chatid, String timezone);

    String Sendmessage(String text, String username, String chatid);
}

