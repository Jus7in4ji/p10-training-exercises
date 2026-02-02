package com.justinaji.chatapp_messages.model; 

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
    @Column(name = "u_id")
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;
    
    private boolean status = false;

    @Column(name = "recent_login", columnDefinition = "DATETIME")
    private LocalDateTime recentLogin;

}
