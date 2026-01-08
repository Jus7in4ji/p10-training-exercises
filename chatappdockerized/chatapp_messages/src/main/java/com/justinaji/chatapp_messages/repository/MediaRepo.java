package com.justinaji.chatapp_messages.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.chatapp_messages.model.media;

@Repository
public interface MediaRepo extends JpaRepository<media, String> {
    List<media> findBySender(String sender); 
    List<media> findByChatid(String chatid);
    List<media> findByChatidOrderBySentTimeAsc(String chatid);

}
