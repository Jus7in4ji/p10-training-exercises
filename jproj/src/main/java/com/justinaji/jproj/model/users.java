package com.justinaji.jproj.model; 

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
    private String u_id;
    private String email;
    private String name;
    private String password;
    private boolean status = false;
}
