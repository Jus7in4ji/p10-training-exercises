package com.justinaji.depinj;
import org.springframework.stereotype.Component;

@Component
public class karnataka implements State {
    public void action(){
        String state = "Karnataka";
        System.out.println("--State: "+state);
    }
}
