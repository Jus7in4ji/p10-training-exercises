package com.justinaji.depinj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class country {
    @Autowired //feild injection 
    @Qualifier("kerala")
    private State state;
    
    public void action(){
        
        String country = "India";
        System.out.println("-Country: "+country);
        state.action();
    }

}
