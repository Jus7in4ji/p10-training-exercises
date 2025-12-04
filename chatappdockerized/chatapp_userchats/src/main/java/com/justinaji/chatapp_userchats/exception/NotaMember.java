package com.justinaji.chatapp_userchats.exception;

public class NotaMember extends RuntimeException {
    public NotaMember(String username, String chatname){
        super("Error: User ( "+username+" ) is not a member of the group ( "+chatname+" )");
    }

}
