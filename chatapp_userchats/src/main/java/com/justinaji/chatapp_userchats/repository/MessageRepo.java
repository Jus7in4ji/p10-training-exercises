package com.justinaji.chatapp_userchats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.chatapp_userchats.model.messages;
import com.justinaji.chatapp_userchats.model.users;

import java.util.List;

import com.justinaji.chatapp_userchats.model.chats;

@Repository
public interface MessageRepo extends JpaRepository<messages, String> {
    List<messages> findBySender(users sender);
    List<messages> findByChat(chats chat);
    List<messages> findByChatOrderBySentTimeAsc(chats chat);
}
