package com.justinaji.depinj;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
//@Primary has spring choose this class if another class of same interface exists 
public class kerala implements State {
    public void action(){
        String state = "Kerala";
        System.out.println("--State: "+state);
    }
}
