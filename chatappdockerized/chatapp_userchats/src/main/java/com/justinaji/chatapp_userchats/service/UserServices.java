package com.justinaji.chatapp_userchats.service;

import com.justinaji.chatapp_userchats.model.users;

public interface UserServices {

    String login(String username , String password);

    String RegisterUser(users user);

}
