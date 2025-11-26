package com.justinaji.jproj.service;

import java.util.HashMap;
import java.util.List;

import com.justinaji.jproj.dto.messageDTO;
import com.justinaji.jproj.model.WSmessage;

public interface mesage_services {

    List<messageDTO> GetPvtmessages(String chatname) ;

    void SendPvtMessage(String chatname, String message) ;

    List<messageDTO> GetGrpmessages(String chatname) ;

    void SendGrpMessage(String chatname, String message) ;

    HashMap<String,String> ischatvalid(String chatname, String username, boolean isgroup);

    List<WSmessage> getchathistory(String username , String chatid, String timezone);

    String Sendmessage(String text, String username, String chatid);
}
