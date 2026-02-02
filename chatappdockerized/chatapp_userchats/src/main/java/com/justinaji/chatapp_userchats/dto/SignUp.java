package com.justinaji.chatapp_userchats.dto;

import lombok.Getter;

@Getter
public final class SignUp extends Loginform {
    private String email;

    public SignUp(String username, String password, String email) {
        super(username, password);
        this.email = email;
    }
}