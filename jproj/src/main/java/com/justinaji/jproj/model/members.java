package com.justinaji.jproj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(MemberId.class)
public class members {
    @Id
    @ManyToOne
    @JoinColumn(name = "chat")
    private chats chat;

    @Id
    @ManyToOne
    @JoinColumn(name = "member")
    private users member;

    private boolean admin = false;
}
