package com.justinaji.chatapp_messages.repository;

import java.util.List;

import com.justinaji.chatapp_messages.model.Logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.chatapp_messages.model.users;


@Repository
public interface LogRepo extends JpaRepository<Logs, String> {
    List<Logs> findByUser(users user);
}
