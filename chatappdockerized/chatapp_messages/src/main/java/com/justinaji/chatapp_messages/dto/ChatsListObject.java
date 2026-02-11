package com.justinaji.chatapp_messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatsListObject {
    private String id;
    private String name;
    private boolean group;
    private boolean unread;
}
