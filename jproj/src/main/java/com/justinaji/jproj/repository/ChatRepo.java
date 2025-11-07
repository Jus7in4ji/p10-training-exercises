
package com.justinaji.jproj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justinaji.jproj.model.chats;

@Repository
public interface ChatRepo extends JpaRepository<chats, String> {
    boolean existsByName(String name);
    chats findByName(String chat);
}
