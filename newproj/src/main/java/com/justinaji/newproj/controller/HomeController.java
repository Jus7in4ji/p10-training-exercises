package com.justinaji.newproj.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {
    @RequestMapping("/")
    public String greet(){
        return "\n---CRUD app---\n";
    }

    @RequestMapping("/about")
    public String aboutmsg() {
        return "\n-- A simple CRUD app handling a sample user and file dataset --\n";
    }
    
}
