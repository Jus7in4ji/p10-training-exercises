package com.justinaji.jproj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.jproj.model.messages;
import com.justinaji.jproj.model.users;
import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<messages, String> {
    List<messages> findBySender(users sender);
}
