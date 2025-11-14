package com.justinaji.jproj.dto;

public class chatmember {
    private String name;
    private boolean isadmin= false;
    
    public chatmember(String name2, boolean b) {
        this.name = name2;
        this.isadmin = b;
    }
    public String getName() {
        return this.name;
    }

    public boolean isAdmin() {   // boolean uses "is"
        return this.isadmin;
    }

}
