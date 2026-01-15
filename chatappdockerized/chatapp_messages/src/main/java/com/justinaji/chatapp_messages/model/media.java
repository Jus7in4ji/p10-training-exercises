package com.justinaji.chatapp_messages.model;

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

    private String path;

    private String sender;

    private String name;

    private String filetype;

    private String chatid;

    private String senttime;

    private boolean msgread = false;
}
