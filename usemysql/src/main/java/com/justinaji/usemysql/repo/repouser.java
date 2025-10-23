package com.justinaji.usemysql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.usemysql.model.users;

@Repository
public interface repouser extends JpaRepository<users, String>{

}
