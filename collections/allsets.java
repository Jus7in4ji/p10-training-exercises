package collections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.TreeSet;

/*
add()       Adds an element if it's not already in the set
 */

public class allsets {
    static HashSet<String> hs = new HashSet<String>();
    static TreeSet<String> ts = new TreeSet<String>();
    static LinkedHashSet<String> lhs = new LinkedHashSet<String>();
    static void addintoset(String s){
        hs.add(s);
        ts.add(s);
        lhs.add(s);
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n ;
        System.out.println("number of elements you wihs to insert into the sets: ");
        n = Integer.parseInt(sc.nextLine()); // nextint() causes the \n after the integer to be counted in the loop's first nextline()
        while(n>0){
            System.out.println("String: ");
            String s = sc.nextLine();
            addintoset(s);
            n-=1;
        }
        System.out.println("order of Elements in sets are :");
        System.out.println("Hashset: "+hs+" -Random order\nTreeSet: "+ts+" -Alphabetical Order\nLinkedHashSet: "+lhs+" -Inserted order");
    }
}
