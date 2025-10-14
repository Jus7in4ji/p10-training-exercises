package collections;

import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;

public class navmap {
    public static void main(String[] args) {
        NavigableMap<String , Integer> nm = new TreeMap<>();
        Scanner sc = new Scanner(System.in);
        int x,randint;
        String name,entry[];
        System.out.println("No of entries: ");
        x = Integer.parseInt(sc.nextLine());
        while(x>0){
            System.out.println("Enter name , number ");
            name = sc.nextLine();
            entry = name.split(",");
            randint = Integer.parseInt(entry[1].trim());
            nm.put(entry[0], randint);
            x-=1;
        }
        System.out.println(nm);
        System.err.println("random values used to find ceiling , higher/lower entry values of the map ");
        System.out.println("lowerkey l: " + nm.lowerKey("l"));
        System.out.println("ceilingkey g: "+nm.ceilingKey("g")); 
        System.out.println("highe entry c: "+ nm.higherEntry("c"));
        System.out.println("lower enrty f: "+ nm.lowerEntry("f"));
        System.out.println("Map in descending order :");
        System.out.println(nm.descendingMap());

    }
}
