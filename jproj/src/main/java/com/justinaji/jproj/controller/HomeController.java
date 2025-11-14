package com.justinaji.jproj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {
    @GetMapping("/")
    public String greet(){
        return "\n---CRUD app---\n";
    }

    @GetMapping("/about")
    public String aboutmsg() {
        return "\n-- A simple CRUD app handling a simple chat simulation --\n";
    }
    
}
