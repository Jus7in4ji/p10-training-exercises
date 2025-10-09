public class Encaps{
    public static void main(String args[]){
        Main encaps = new Main();
        Main oldencaps = new Main();
        System.out.println("attempting to print n ");
        //  System.out.println(encaps.n); is useless as n is private 
        System.out.println("n is :" + encaps.displayn());
        //replace n with x 
        int x= 97;
        encaps.modifyn(x);
        System.out.println("new n is :" + encaps.displayn());
        System.out.println("old n is :" + oldencaps.displayn());


    }

}