package com.justinaji.jproj.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class messageDTO {
    private String message;
    private String sender;
    private Timestamp sentTime;
}
