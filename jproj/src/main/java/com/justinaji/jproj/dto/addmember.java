package com.justinaji.jproj.dto;

import java.util.List;


public class addmember {
    String name;
    List<chatmember> members;

    public String getName() {
        return this.name;
    }
    public List<chatmember> getmembers(){
        return this.members;
    }
}

