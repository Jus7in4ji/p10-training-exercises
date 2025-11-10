package com.justinaji.jproj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.jproj.model.messages;
import com.justinaji.jproj.model.users;

import java.util.List;

import com.justinaji.jproj.model.chats;
import com.justinaji.jproj.service.message_servicesimpl;

@Repository
public interface MessageRepo extends JpaRepository<messages, String> {
    List<messages> findBySender(users sender);
    List<messages> findByChat(chats chat);
    List<messages> findByChatOrderBySentTimeAsc(chats chat);
}
