package com.justinaji.chatapp_userchats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public sealed class Loginform permits SignUp{
    private String username;
    private String password;
}
