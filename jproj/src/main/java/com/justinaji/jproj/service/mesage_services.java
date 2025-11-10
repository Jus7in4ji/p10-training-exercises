package com.justinaji.jproj.service;

import java.util.List;

import com.justinaji.jproj.dto.messageDTO;

public interface mesage_services {

    List<messageDTO> Getmessages(String chatname);

    List<messageDTO> SendMessage(String chatname, String message);

}
