package xceptions;

import java.util.Scanner;

class notatriangle extends RuntimeException{

    public notatriangle() {
        super("sides cannot be zero for a triangle to be valid");
    }
    
}
class superclass{
    void sides(int a, int b, int c){
        System.out.println("a = "+a+"\nb = "+b+"\nc = "+c);

    }
}
class subclass extends superclass{
    void sides(int a, int b, int c) throws notatriangle {
        if(a==0||b==0||c==0) throw new notatriangle();
        System.out.println("a = "+a+"\nb = "+b+"\nc = "+c);
    }
}
public class custexcept {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("eneter in length of sides a , b , c in a single line : ");
        String abc[],sides = sc.nextLine();
        int a,b,c;
        abc = sides.split(",");
        a = Integer.parseInt(abc[0].trim());
        b = Integer.parseInt(abc[1].trim());
        c = Integer.parseInt(abc[2].trim());
        superclass s = new subclass();
        try {
            s.sides(a, b, c);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
