package com.justinaji.usemysql.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "uploader")
    private List<filedets> userfiles; 
}
