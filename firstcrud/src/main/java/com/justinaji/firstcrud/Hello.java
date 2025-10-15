package com.justinaji.firstcrud;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {
    @RequestMapping
    public String disp(){
        return "first springboot app ";
    }
}
