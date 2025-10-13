package collections;
// difference sin insertion order of hashmap, liked hashmap and tree map 
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class allmaps {
    static HashMap<String, String> hm = new HashMap<>();
    static TreeMap<String , String> tm = new TreeMap<>();
    static LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
    static void addintomap(String s1 ,String s2){
        hm.put(s1,s2);
        tm.put(s1, s2);
        lhm.put(s1, s2);
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n ;
        System.out.println("number of elements you wihs to insert into the sets: ");
        n = Integer.parseInt(sc.nextLine()); // nextint() causes the \n after the integer to be counted in the loop's first nextline()
        while(n>0){
            System.out.println("String: ");
            String s = sc.nextLine();
            String keyval[] = s.split(",");
            addintomap(keyval[0],keyval[1]);
            n-=1;
        }
        System.out.println("order of Elements in sets are :");
        System.out.println("Hashmap: "+hm+" -Random order\nTreemap: "+tm+" -Alphabetical Order\nLinkedHashmap: "+lhm+" -Inserted order");
    }
}
