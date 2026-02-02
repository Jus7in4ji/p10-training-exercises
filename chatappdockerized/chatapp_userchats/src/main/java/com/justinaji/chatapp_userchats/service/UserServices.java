package com.justinaji.chatapp_userchats.service;

import com.justinaji.chatapp_userchats.dto.SignUp;

public interface UserServices {

    String login(String username , String password);

    String RegisterUser(SignUp signUp);

}
