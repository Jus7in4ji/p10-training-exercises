package com.justinaji.chatapp_userchats.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.chatapp_userchats.service.CommonMethods;


@RestController
public class testcontroller {

  @GetMapping("/new/getname")
    public Map<String, String> getusername() {
        return Map.of("username", CommonMethods.getCurrentUser().getName());
    }

  @GetMapping("/new/hello")
  public String gethello() {
      return "hello from microservice";
  }
  
}
