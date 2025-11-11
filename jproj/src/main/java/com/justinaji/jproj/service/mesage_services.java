package com.justinaji.jproj.service;

import java.util.List;

import com.justinaji.jproj.dto.messageDTO;

public interface mesage_services {

    List<messageDTO> GetPvtmessages(String chatname) ;

    List<messageDTO> SendPvtMessage(String chatname, String message) ;

    List<messageDTO> GetGrpmessages(String chatname) ;

    List<messageDTO> SendGrpMessage(String chatname, String message) ;

}
