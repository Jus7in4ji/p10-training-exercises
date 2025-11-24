package com.justinaji.chatapp_messages.service;

import com.justinaji.chatapp_messages.model.users;

public interface user_services {

    String login(String username , String password);

    String RegisterUser(users user);

}
