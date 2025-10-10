package pack2;

public class Accessmods{
    private int x = 10;
    protected int y = 20;
    public static int z =30;
    public static void freecall(){
        System.out.println("called without initialzing class static variable "+z);
    }
    protected void displayx(){
        System.out.println("x = "+x);
    }
    void dispzero(){
        int intnew =0;

        System.out.println(intnew);

    }

    protected void modify_z1(){
        z = z +y;
        System.out.println("z + y = new z = "+z);
    }
}   