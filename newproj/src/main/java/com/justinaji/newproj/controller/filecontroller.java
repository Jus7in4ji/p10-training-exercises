package com.justinaji.newproj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import com.justinaji.newproj.model.filedets;
import com.justinaji.newproj.service.fileservice;
@RestController
public class filecontroller {
    @Autowired
    fileservice fservice; //object of fileservices 

    @GetMapping("/files")
    public String fdeets(){
        return fservice.getfiledets();
    }
    @GetMapping("/files/{id}")
    public String getfilebyid(@PathVariable int id ){
        return fservice.filebyid(id);
    }

    @PostMapping("/files")
    public String addfile(@RequestBody filedets file) {
        if(fservice.addfile(file))return "new file added";
        return "Unable to add new file";
    }

    @PutMapping("/files/{id}/{name}")
    public String rennamedoc(@PathVariable int id, @PathVariable String name) {
        return fservice.rename(id, name);
    }
    
    @DeleteMapping("/files/id/{id}")
    public String delbyid(@PathVariable int id) {
        return fservice.delete(id);
    }
    @DeleteMapping("/files/name/{name}")
    public String delbyname(@PathVariable String name) {
        return fservice.delete(name);
    }
    
    
}

