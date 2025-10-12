
import java.util.LinkedList;
import java.util.Scanner;

public class LLinsert {
    public static void main(String[] args) {
        LinkedList<Integer> lint = new LinkedList<Integer>();
        Scanner sc = new Scanner(System.in);
        int y;
        int x = 0;
        System.out.println("---Menu---");
        while(x!=4){
            System.out.println(" 1. Insert\n 2. Delete\n 3. Display\n 4. Exit");
            x = sc.nextInt();
            if (x==1){
                System.out.println("\t1. First position\n\t2. Last position");
                y = sc.nextInt();
                switch (y) {
                    case 1:
                        System.out.println("input: ");
                        lint.addFirst(sc.nextInt());
                        break;
                    case 2:
                        System.out.println("input: ");
                        lint.addLast(sc.nextInt());
                        break;
                    default:
                        System.out.println("invalid option");
                        break;
                }
            }

            else if (x==2){
                System.out.println("\t1. First position\n\t2. Last position");
                y = sc.nextInt();
                switch (y) {
                    case 1-> lint.removeFirst();
                    case 2-> lint.removeLast();
                    default-> System.out.println("invalid option");
                }
            }

            else if (x==3){
                System.out.println("\t1. First position\n\t2. Last position\n\t3. Full Display");
                y = sc.nextInt();
                switch (y) {
                    case 1-> System.out.println("first element: "+lint.getFirst());
                    case 2-> System.out.println("Last element:  "+lint.getLast());
                    case 3-> System.out.println("List"+lint);
                    default-> System.out.println("invalid option");
                }
            }
            
            else if (x==4){
                System.out.println("Exiting..");
                break;
            }
        }
    }
}
