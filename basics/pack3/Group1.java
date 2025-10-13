package pack3;

public class Group1 {
    private String strx= "Priv";
    protected String stry = "Prot" ;
    public String strz= "Def";

    public Group1(String S) {
        System.out.println(S);

    }

    public Group1() {
    }
        
    public class Innergrp{
        public void printinnergrp(){
            System.out.println("strx = "+strx); // accessible inside inner class
        }
    }
    static void display(){
        System.out.println("static void display called ");
    }
    public static void divide(int x , int y ){
        System.out.println("x/y = "+ x/y);
    } 
}

