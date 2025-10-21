package com.justinaji.newproj.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.justinaji.newproj.model.filedets;
import com.justinaji.newproj.service.fileservice;

@RestController
@RequestMapping("/files")
public class filecontroller {
    private final fileservice fservice;

    public filecontroller(fileservice fservice) {
        this.fservice = fservice;
    }

    @GetMapping("")
    public List<?> fdeets(){
        return fservice.getfiledets();
    }
    @GetMapping("/{id}")
    public String getfilebyid(@PathVariable String id ){
        return fservice.filebyid(id);
    }

    @PostMapping("")
    public String addfile(@RequestBody filedets file) {
        if(fservice.addfile(file))return "new file added";
        return "Unable to add new file";
    }

    @PutMapping("/{id}/{name}")
    public String rennamedoc(@PathVariable String id, @PathVariable String name) {
        return fservice.updatefile(id, name);
    }
    
    @DeleteMapping("/id/{id}")
    public String delbyid(@PathVariable String id) {
        return fservice.delete(id);
    }

    /*@DeleteMapping("/name/{name}")
    public String delbyname(@PathVariable String name) {
        return fservice.delete(name);
    }*/
    
}

