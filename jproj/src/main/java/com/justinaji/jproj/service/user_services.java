package com.justinaji.jproj.service;

import com.justinaji.jproj.model.users;

public interface user_services {

    String login(String username , String password);

    String RegisterUser(users user);

}
