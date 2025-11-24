package com.justinaji.chatapp_messages.model;

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

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp sentTime;

}
