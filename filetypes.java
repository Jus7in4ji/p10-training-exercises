
import java.util.Scanner;
//a program that counts the number of differenet types of files in a database 
public class filetypes {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s;
        map.filetypes.put("PDF", 0); 
        map.filetypes.put("IMAGE", 0); 
        map.filetypes.put("TXT", 0); 
        map.filetypes.put("DOCX", 0); 
        map.filetypes.put("GIF", 0);
        map.filetypes.put("Others", 0); // setting initial types 
        int x =0;
        while(x!=5){  
            System.out.println(" 1. Insert new file\n 2. Remove a file \n3. Filetypes present\n 4. See all file types and count \n 5.exit\n Enter: ");
            x = Integer.parseInt(sc.nextLine());
            switch (x) {
                case 1:
                    System.out.println("Type of file you wish to insert: ");
                    s = sc.nextLine();
                    map.insert(s);
                    break;
                case 2:
                    System.out.println("Type of file you wish to delete: ");
                    s = sc.nextLine();
                    map.rem(s);
                    break;
                case 3:
                    System.out.println("Map: "+map.filetypes.keySet());
                    break;
                case 4:
                    System.out.println("Map: "+map.filetypes);
                    break;
                default:
                    System.out.println("Wrong option , try again ");
                    break;
            }
        }
        System.out.println("Exiting..");    
    }
}
    

