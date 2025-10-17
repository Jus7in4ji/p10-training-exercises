package com.justinaji.newproj.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class users {
    public int id;
    public String email;
    public String name;
    public String password;
    public boolean admin;

    public users(int id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.admin = false;
    }
}
