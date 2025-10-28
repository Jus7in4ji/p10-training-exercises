package com.justinaji.jproj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.jproj.model.users;

@Repository
public interface UserRepo extends JpaRepository<users, String>{
    boolean existsByEmail(String email); //checks if gievn email id is already assigned to a user
}
