package com.justinaji.newproj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class HomeController {
    @RequestMapping("/")
    public String greet(){
        return "\n---basic greet message---\n";
    }

    @RequestMapping("/about")
    public String aboutmsg() {
        return "\n-- About page message --\n";
    }
    
}
