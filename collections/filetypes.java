package collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FilenotExists extends RuntimeException{
    public FilenotExists(){ super("Given file is not present in the database"); }
}

//a program that counts the number of differenet types of files in a database 
public class filetypes {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> namelist = new ArrayList<>();
        String s; // setting initial types 
        int x =0;
        boolean correct;
        System.out.println("\n--MENU--");
        while(x!=6){  
            System.out.println("\n 1. Insert new file\n 2. Remove a file \n 3. Filetypes present\n 4. See all file types and count \n 5. display files present\n 6.exit\n Enter: ");
            correct = false;
            while(!correct){
                try{
                    x = Integer.parseInt(sc.nextLine());
                    correct = true;
                }
                catch(NumberFormatException n){ System.out.println("Please enter a valid integer: "); }
            }
            
            switch (x) {
                case 1-> {
                    System.out.println("Filename: ");
                    s = sc.nextLine();
                    namelist.add(s);
                    map.mapinto(map.getfiltype(s));
                }
                
                case 2->{
                    System.out.println("file you wish to delete: ");
                    s = sc.nextLine();
                    try{
                        if(namelist.contains(s)) {
                            namelist.remove(s);
                            map.mapinto(map.getfiltype(s),"del"); 
                        }
                        else throw new FilenotExists();
                    }
                    catch(Exception e){
                        System.out.println(e);
                    } 
                }

                case 3->{
                    System.out.print("\n-files present of document types:  ");
                    map.dispkeys();
                }

                case 4 -> System.out.println("No of files per filetype:  "+map.filetypes);

                case 5 ->System.out.println("filenames: "+namelist);
                
                case 6 ->System.out.println("Exiting..");
                
                default->System.out.println("Wrong option , try again ");
            }
        }
           
    }
}
    

