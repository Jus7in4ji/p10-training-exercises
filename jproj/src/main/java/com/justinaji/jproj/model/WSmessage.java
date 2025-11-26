package com.justinaji.jproj.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSmessage {
    private String msgid;
    private String from;
    private String text;  
    private String sentTime; 
    private String room;
    private boolean msgread;

}


