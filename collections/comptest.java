package collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
class files{
    String name;
    Integer fnumbers;

    public files(String s , Integer m) {
        this.name = s;
        this.fnumbers = m;
    }
    
}
 class compintdesc implements Comparator<files>{
    public int compare(files m1, files m2){
        return m2.fnumbers-m1.fnumbers;
    }
}
public class comptest{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("No of filetypes: ");
        int x,nos;
        x = Integer.parseInt(sc.nextLine());
        List<files> sm = new ArrayList<>();
        String fname,filedeets[];
        
        while(x>0){
            System.out.println("Enter filetype  , numbers ");
            fname = sc.nextLine();
            filedeets = fname.split(",");
            nos = Integer.parseInt(filedeets[1].trim());
            sm.add(new files(filedeets[0], nos));
            x-=1;
        }
        sm.sort((a,b)-> a.fnumbers-b.fnumbers); //lamba expression in place of comparator class
        System.out.println("Student orderd on fnumbers in ascending order : ");
        for (files stm: sm){
            System.out.println("FileType: "+stm.name+"\t, Nos present:  "+stm.fnumbers);
        }
        sm.sort(new compintdesc());
        System.out.println("Student orderd on fnumbers in descending order : ");
        for (files stm: sm){
            System.out.println("FileType: "+stm.name+"\t, Nos present:  "+stm.fnumbers);
        }
    }
    
}