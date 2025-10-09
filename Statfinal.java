public class Statfinal{
    static int common = 35;
    int unique = 25;
    final int unchanged = 20;
    public static void main(String args[]){
        Statfinal sf1 = new Statfinal();
        sf1.unique += 10;
        System.out.println("values of common: "+sf1.common+ " and unique: "+sf1.unique+" from first object");
        // sf1.unchanged = 30; gives an error , final variables are unchangeable
        sf1.common = 40 ; // modified thru sf1 object 
        
        Statfinal sf2 = new Statfinal(); // new object to test if changes are reflected 
        if (sf1.common == sf2.common){
            System.out.println("\nvalue of common changes throughout all objects : "+sf2.common);}
        System.err.println("unique value from second object : "+ sf2.unique );

    }
}