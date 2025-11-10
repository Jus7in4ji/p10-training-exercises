package com.justinaji.jproj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class messages {
    @Id
    private String m_id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JoinColumn(name = "sender") 
    private users sender;

    @ManyToOne
    @JoinColumn(name = "chat")
    private chats chat;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Timestamp sentTime;

    public messages(String m_id, String message, users sender, chats chat) {
    this.m_id = m_id;
    this.message = message;
    this.sender = sender;
    this.chat = chat;
}

}
