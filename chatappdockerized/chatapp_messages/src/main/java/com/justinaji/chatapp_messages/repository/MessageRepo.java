package com.justinaji.chatapp_messages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.chatapp_messages.model.messages;
import com.justinaji.chatapp_messages.model.users;

import java.util.List;

import com.justinaji.chatapp_messages.model.chats;

@Repository
public interface MessageRepo extends JpaRepository<messages, String> {
    List<messages> findBySender(users sender);
    List<messages> findByChat(chats chat);
    List<messages> findByChatOrderBySentTimeAsc(chats chat);
}
