package com.justinaji.usemysql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.justinaji.usemysql.model.filedets;

@Repository
public interface repofile extends JpaRepository<filedets, String>{

}
 