package com.justinaji.newproj.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.justinaji.newproj.model.filedets;
import com.justinaji.newproj.service.fileservice;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
public class filecontroller {
    @Autowired
    fileservice fservice; //object of fileservices 

    @GetMapping("/files")
    public List<filedets> fdeets(){
        return fservice.getfiledets();
    }
    @GetMapping("/files/{id}")
    public filedets getfilebyid(@PathVariable int id ){
        return fservice.filebyid(id);
    }

    @PostMapping("/files")
    public String postMethodName(@RequestBody filedets file) {
        fservice.addfile(file);
        return "new file added";
    }

    @PutMapping("/files/{id}/{name}")
    public void putMethodName(@PathVariable int id, @PathVariable String name) {
        fservice.rename(id, name);
    }
    
    @DeleteMapping("/files/id/{id}")
    public String putMethodName(@PathVariable int id) {
        return fservice.delete(id);
    }
    @DeleteMapping("/files/name/{name}")
    public String putMethodName(@PathVariable String name) {
        return fservice.delete(name);
    }
    
    
}
