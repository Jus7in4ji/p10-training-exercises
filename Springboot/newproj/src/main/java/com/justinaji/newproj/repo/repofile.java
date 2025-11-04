package com.justinaji.newproj.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.newproj.model.filedets;

@Repository
public interface repofile extends JpaRepository<filedets, String>{

}
 