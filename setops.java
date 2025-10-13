
import java.util.HashSet;
import java.util.Scanner;

/*
add()  add element if not already in set 
remove()	Removes the element from the set
contains()	Checks if the set contains the element
size()	    Returns the number of elements
clear()	    Removes all elements
*/
public class setops {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashSet<Integer> hs = new HashSet<>();
        System.out.println("Set operations using hashset");
        int x = 0;
        while(x!=7){
            System.out.println(" 1. Add to set\n 2. Remove from set\n 3. check if set contains element\n 4. no of elemenets in set\n 5. Clear set\n 6. Display set \n 7. Exit\nChoose: ");
            x = sc.nextInt();
            switch (x) {
                case 1 ->{
                    System.out.println("Element to insert: ");
                    hs.add(sc.nextInt());
                }
                case 2 ->{
                    System.out.println("Element to be removed: ");
                    hs.remove(sc.nextInt());
                }
                case 3 ->{
                    System.out.println("Element to check for : ");
                    if(hs.contains(sc.nextInt())){System.out.println("Element is present");}
                    else{System.out.println("Element not found");}
                }
                case 4 ->System.out.println("No of elemnts in the set are: "+hs.size());
                case 5 -> hs.clear();
                case 6-> System.out.println(hs);
            }
        }
    }
}
