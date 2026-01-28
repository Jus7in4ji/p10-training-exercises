package com.justinaji.spring4jdk17test.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class chats {
    private String c_id;
    private String name;
    
    private users createdBy;   
    
    private boolean isgroup = false;

    private String chat_key ="";
}