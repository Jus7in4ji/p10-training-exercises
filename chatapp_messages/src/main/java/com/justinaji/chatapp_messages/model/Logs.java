package com.justinaji.chatapp_messages.model;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Logs {
    @Id
    private String id;
    private String action;
    private String report;
    private Timestamp time;
    @ManyToOne
    @JoinColumn(name = "user")
    private users user;

}
