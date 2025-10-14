package xceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class floatdiv {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Float> fls = new ArrayList<>();
        int i;
        float f,a,b,quo;
        boolean ok=false;
        for ( i =0; i < 6; i++) { // take 6 sample inputs
            System.out.println("Float "+(i+1)+": ");
            f = sc.nextFloat();
            fls.add(f);
        }
        System.out.println("Out of the 6 numbers , pick two to divide eachother : 'a/b '");
        while (true) {
        System.out.println("index of a = "); 
        i = sc.nextInt();
        try {
            a = fls.get(i);
            ok=true;
            break; } 
        catch (IndexOutOfBoundsException e) {
            System.out.println("no element exists at this index");
            ok = false; 
        } 
        }
        while (true) {
        System.out.println("index of b = "); 
        i = sc.nextInt();
        try {
            b = fls.get(i);
            quo = a/b;
            if( (int) b==0){ //float divison by zero doesn't normally trigger arithmeticException 
                throw new ArithmeticException(); //so we throw it here manually
            }
            System.out.println(a+"/ "+ b+"= "+quo);
            ok = true;
            break; } 
        catch (IndexOutOfBoundsException e) {ok= false; }
        catch( ArithmeticException z){
            System.out.println("Denominator cannot be zero");
            ok = false;} 
        finally{
            if(ok){
                System.out.println("\nExiting..");
            }
            else{
                System.out.println("Pick a different denominator ");
            }
        }
        }
    }
}
