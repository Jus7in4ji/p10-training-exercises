package pack3;

public class Group2 extends  Group1{

    public Group2(String s) {
        super(s);
    }

    public Group2() {
        
    }
    
    
    public void printGP1(){
    System.out.println("stry = "+stry);
    System.out.println("strz = "+strz);
    }
    public static void divide(double x , double y ){
        double div = (double) x/y;
        System.out.println("x/y = "+div);
    }
}