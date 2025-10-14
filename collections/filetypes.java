package collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//a program that counts the number of differenet types of files in a database 
public class filetypes {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> namelist = new ArrayList<>();
        String s; // setting initial types 
        int x =0;
        while(x!=6){  
            System.out.println("\n 1. Insert new file\n 2. Remove a file \n 3. Filetypes present\n 4. See all file types and count \n 5. display files present\n 6.exit\n Enter: ");
            x = Integer.parseInt(sc.nextLine());
            switch (x) {
                case 1:
                    System.out.println("Filename: ");
                    s = sc.nextLine();
                    namelist.add(s);
                    map.insert(map.getfiltype(s));
                    break;
                case 2:
                    System.out.println("Type of file you wish to delete: ");
                    s = sc.nextLine();
                    map.rem(s);
                    break;
                case 3:
                    System.out.print("\n-files present of document types:  ");
                    map.dispkeys();
                    break;
                case 4:
                    System.out.println("No of files per filetype:  "+map.filetypes);
                    break;
                case 5:
                    System.out.println("filenames: "+namelist);
                    break;
                case 6:
                    System.out.println("Exiting..");
                    break; 
                default:
                    System.out.println("Wrong option , try again ");
                    break;
            }
        }
           
    }
}
    

