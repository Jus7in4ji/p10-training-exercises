package com.justinaji.chatapp_messages.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberId implements Serializable {
    private String chat;
    private String member;
}
