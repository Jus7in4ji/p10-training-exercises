package com.justinaji.chatapp_userchats.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class addmember {
    String name;
    List<chatmember> members;

    public String getName() {
        return this.name;
    }

    public List<chatmember> getMembers() {
        return this.members;
    }
}

