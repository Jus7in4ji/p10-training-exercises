package com.justinaji.chatapp_messages.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class chats {
    @Id
    @Column(name = "c_id")
    private String chatId;
    
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private users createdBy;   
    
    private boolean isgroup = false;

    @Column(columnDefinition = "TEXT")
    private String chat_key ="";
}