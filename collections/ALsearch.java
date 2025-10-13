package collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class ALsearch {
    static void search(String s ,ArrayList<String> AL ){
        boolean found = false;
        for (int i=0 ; i< AL.size(); i++){
            if (AL.get(i).equals(s)){
                found = true;
            }
        }
        if (found){System.out.println(s+" is present in the Arraylist provided ");}
        else{System.out.println(s+" was not found ");}
    }
    public static void main(String[] args) {
        ArrayList<String> strlist = new ArrayList<String>();
        Scanner sc = new Scanner(System.in);
        while (true) { 
            System.out.println("value : ");
            String x = sc.nextLine();
            if (x.toUpperCase().equals("EXIT")){ break;}
            else{strlist.add(x);}
        }
        System.out.println("Enter value to search : ");
        String searchterm = sc.nextLine();
        search(searchterm, strlist);
    }

    } 
