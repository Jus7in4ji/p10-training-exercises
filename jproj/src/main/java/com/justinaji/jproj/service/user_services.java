package com.justinaji.jproj.service;

import com.justinaji.jproj.model.users;

public interface user_services {
    String RegisterUser(users user);

    String UserLogin(String email, String pass);

    void UserLogout();
}
