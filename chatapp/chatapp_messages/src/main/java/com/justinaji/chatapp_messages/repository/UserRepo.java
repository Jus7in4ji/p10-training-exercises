package com.justinaji.chatapp_messages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.chatapp_messages.model.users;

@Repository
public interface UserRepo extends JpaRepository<users, String>{
    boolean existsByEmail(String email); //checks if gievn email id is already assigned to a user
    boolean existsByName(String name);
    users findByEmail(String email);
    users findByName(String uname);
}
