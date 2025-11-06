package com.justinaji.jproj.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class chatmember {
    private String id;
    private boolean isadmin;
    public String getid() {
        return this.id;
    }
    public boolean getisadmin() {
        return this.isadmin;
    }

    
}
