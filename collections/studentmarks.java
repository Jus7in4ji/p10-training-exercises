package collections;
import java.util.Map;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;

class marksmap{
    private TreeMap<String, Integer> stmarks = new TreeMap<>();
    void entermarks(String s, Integer i ){
        stmarks.put(s, i);
    }

    void display(){
        Iterator<Map.Entry<String,Integer>> i= stmarks.entrySet().iterator();
        System.out.println("All students data: ");
        while(i.hasNext()){
            Map.Entry<String,Integer> mp = i.next();
            System.out.println("Name = "+mp.getKey()+" Percentage = "+mp.getValue()+"%");
        }
    }

    void displaywfilter(){
        Scanner s = new Scanner(System.in);
        int x;
        System.out.println("Pass Percentage cutoff: ");
        x = s.nextInt();
        Iterator<Map.Entry<String,Integer>> i= stmarks.entrySet().iterator();
        System.out.println("All students who have passed: ");
        while(i.hasNext()){
            Map.Entry<String,Integer> mp = i.next();
            if(mp.getValue()>x){
                System.out.println("Name = "+mp.getKey()+", Percentage = "+mp.getValue()+" %");
            }
        }
    }
}

public class studentmarks {
    public static void main(String[] args) {
        marksmap mm = new marksmap();
        Scanner sc = new Scanner(System.in);
        int n,marks;
        String stud,studnmark[];
        System.out.println("No of students in your class: ");
        n= Integer.parseInt(sc.nextLine());
        while(n>0){
            System.out.println("Enter name , marks percentage: ");
            stud = sc.nextLine();
            studnmark = stud.split(",");
            marks = Integer.parseInt(studnmark[1].trim());
            mm.entermarks(studnmark[0], marks);
            n-=1;
        }
        mm.displaywfilter();
        mm.display();

    }
}
