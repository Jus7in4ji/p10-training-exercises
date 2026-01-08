package com.justinaji.chatapp_messages.model;

import java.sql.Timestamp;

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
public class media {
    @Id
    private String fileid;

    @Column(columnDefinition = "TEXT")
    private String path;

    private String sender;

    private String name;

    private String filetype;

    private String chatid;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp sentTime;

    private boolean msgread = false;
}
