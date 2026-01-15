package com.justinaji.chatapp_messages.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempMsg {
    private int id;
    private String sender, chatid, message;
    private Timestamp senttime;
    private String formattedtime;
}
