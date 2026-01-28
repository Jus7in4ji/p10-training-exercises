package com.justinaji.spring4jdk17test.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class users {
    private String u_id;

    private String email;

    private String name;

    private String password;
    
    private boolean status = false;

    private LocalDateTime recentLogin;

}