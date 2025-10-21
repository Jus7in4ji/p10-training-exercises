package com.justinaji.newproj.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.newproj.model.users;

@Repository
public interface repouser extends JpaRepository<users, String>{

}
