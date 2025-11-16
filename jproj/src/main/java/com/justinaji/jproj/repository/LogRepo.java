package com.justinaji.jproj.repository;

import java.util.List;

import com.justinaji.jproj.model.Logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.jproj.model.users;


@Repository
public interface LogRepo extends JpaRepository<Logs, String> {
    List<Logs> findByUser(users user);
}
