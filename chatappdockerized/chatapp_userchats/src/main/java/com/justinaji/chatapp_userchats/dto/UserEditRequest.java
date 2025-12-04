package com.justinaji.chatapp_userchats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEditRequest {
    private String chatname;
    private String username;
    private boolean admin=false;
}