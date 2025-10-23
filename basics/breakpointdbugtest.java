import java.util.Scanner;

public class breakpointdbugtest {
    static String s = "One";
    static void replace(String s1){
        s = s1;

    }
    static void append(String s2){
        s+=s2; 
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        char x;
        boolean exit = false;
        String newstring;

        while (!exit){
            System.out.println("replace s ? (y/n): ");
            x = sc.next().charAt(0);
            sc.nextLine();

            if (Character.toLowerCase(x)=='y'){
                System.out.println("new string : ");
                newstring = sc.nextLine();
                replace(newstring); 
                System.out.println("new string = "+s);
            }

            else if (Character.toLowerCase(x)=='n'){
                System.out.println("append to string ? (y/n): ");
                x = sc.next().charAt(0);
                sc.nextLine();

                if (Character.toLowerCase(x)=='y'){
                    System.out.println("added string : ");
                    newstring = sc.nextLine();
                    append(newstring);
                    System.out.println("new string = "+s);
                }

                else if (Character.toLowerCase(x)=='n'){
                    System.out.println("Do you wish to exit ? (y/n):");
                    x = sc.next().charAt(0);
                    sc.nextLine();

                    if (Character.toLowerCase(x)=='y'){
                        System.out.println("Exiting..");
                        exit = true;
                    }
                }
            }

            else{
                System.out.println("Invalid");
            }
        }
    }
}
