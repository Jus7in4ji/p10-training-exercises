package com.justinaji.jproj.service;

import java.util.List;

import com.justinaji.jproj.dto.messageDTO;

public interface mesage_services {

    List<messageDTO> GetPvtmessages(String chatname) ;

    void SendPvtMessage(String chatname, String message) ;

    List<messageDTO> GetGrpmessages(String chatname) ;

    void SendGrpMessage(String chatname, String message) ;

}
