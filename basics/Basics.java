package basics;
import static pack3.Group1.divide; //must use import staic to import static methods from packages
import static pack3.Group2.divide;

import pack3.*;
public class Basics {
    public static void main(String[] args) {
        divide(3,4);
        divide(3, 4.0); // method overloading. 

        Group2 g2 = new Group2();
        g2.strz = "g2.DEF"; // modified copy of superclass variables 

        Group1 g11 = new Group1();
        Group1.Innergrp gin = g11.new Innergrp();
        gin.printinnergrp();
        g2.printGP1(); // accessing superclass variables through subclass


        g11.strz = "g1.Def";
        System.out.println("\ng1.strz = "+g11.strz);
        System.out.println("g2.strz = "+g2.strz);

        Group1 g12 = new Group1("\nPassed random string to parent class");
        


    }
    
}
