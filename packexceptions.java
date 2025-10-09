import java.util.Scanner;

import pack2.Dvision;
public class packexceptions{
    public static void main(String args[]){
        Dvision dv = new Dvision();
        Scanner sc = new Scanner(System.in);
        System.out.println("number 1 :");
        int a = sc.nextInt();
        System.err.println("Number 2: ");
        int b = sc.nextInt();
        float x = dv.div(a,b);
        int y = dv.divint(a, b);
        System.out.println("float division\t "+a+" / "+b+" = "+x); //exception uncatched for float  
        System.out.println("int division\t "+a+" / "+b+" = "+y); // integer division catches exception 
    }
}