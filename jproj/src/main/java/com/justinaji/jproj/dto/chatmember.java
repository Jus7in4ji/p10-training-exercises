package com.justinaji.jproj.dto;

public class chatmember {
    private String name;
    private boolean isadmin;
    
    public chatmember(String name2, boolean b) {
        this.name = name2;
        this.isadmin = b;
    }
    public String getname() {
        return this.name;
    }
    public boolean getisadmin() {
        return this.isadmin;
    }

}
