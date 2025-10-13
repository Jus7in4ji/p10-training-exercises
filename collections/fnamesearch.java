package collections;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class fnamesearch {
    private List<String> fnames = new ArrayList<>();
    void inslist(String s){
        fnames.add(s);
    }
    void searchlist(){
        Scanner s = new Scanner(System.in);
        System.out.println("Search term: ");
        String str = s.nextLine();
        int count=0;
        ListIterator<String> lit = fnames.listIterator();
        List<String> names = new ArrayList<>();
        while(lit.hasNext()){
            String name = lit.next();
            if (name.contains(str)){
                names.add(name);
                count+=1;
            }
        }
        if(count>0){System.out.println("\tFound "+count+"files containing '"+str+"' in their names\n\tFiles: "+names);}
        else{System.out.println("\tNo files were found");}
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("No of files ");
        int x = Integer.parseInt(sc.nextLine());
        fnamesearch fs = new fnamesearch();
        String str;
        while (x>0){
            System.out.println("Filename: ");
            str = sc.nextLine();
            fs.inslist(str);
            x-=1;
        }
        fs.searchlist();
    }
}
