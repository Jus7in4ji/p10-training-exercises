package com.justinaji.newproj.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class users {
    @Id
    private String id;

    private String email;
    private String name;
    private String password;
    private boolean admin = false; 

    // Custom constructor for normal users
    public users(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.admin = false;
    }
}
