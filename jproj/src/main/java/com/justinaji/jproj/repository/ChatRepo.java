package com.justinaji.jproj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justinaji.jproj.model.chats;
import com.justinaji.jproj.model.users;
import java.util.List;

@Repository
public interface ChatRepo extends JpaRepository<chats, String> {
    List<chats> findByCreatedBy(users createdBy);
    chats findByName(String name);
}
