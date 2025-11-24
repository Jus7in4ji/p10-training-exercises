package com.justinaji.chatapp_userchats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class messageDTO {
    private String message;
    private String sender;
    private String sentTime;
}
