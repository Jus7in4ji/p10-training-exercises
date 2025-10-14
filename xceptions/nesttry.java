package xceptions;

import java.util.Scanner;

public class nesttry {
    final float a = 630;
    void divide(int x)throws ArithmeticException{
        if (x==0) throw new ArithmeticException();
        System.out.println(a+"/"+x+" = "+ (a/x));
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        nesttry nt = new nesttry();
        int x,nums []= {32,0,8,6,12};
        System.out.println("Index of Number you wish to divide "+nt.a+" by ");
        try {
            try{
                x = nums[sc.nextInt()];
                nt.divide(x);
                nt = null;// made null so that garbage collector has object to deallocate memory from 
                System.gc();
                }
            catch(ArithmeticException a){
                System.out.println(a);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
        }
        finally{
            System.out.println("\n Exiting..");
        }
    }
    @Override protected void finalize()
    {
        System.out.println(" Finalize method called\n");
    }
}
