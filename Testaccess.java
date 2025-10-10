import pack2.Accessmods;
class Accesschild extends Accessmods{
    public Accesschild() {
        displayx(); // x must be displayed with this method as it is private and cannot be accessed by the child class 
        System.out.println("y = "+this.y); // accessible as y is protected 
        //dispzero(); return seero as it is ot visble . 
    }

    void modify_z2(){
        modify_z1();
    }
    void incrementy(){
        y+=10;
        System.out.println("new y = "+y);    }

}
class Testaccess{
    public static void main(String[] args){
        Accessmods.freecall();
        Accesschild ac1 = new Accesschild();//displayes initial state of variables
        System.out.println("z = "+Accesschild.z); //Accessible direclty as z is public
        ac1.modify_z2();
        ac1.incrementy();
        // ac1.modify_z1();inaccessible due ot it being private and must be invoked thru modify_z2
        

        System.out.println("\nnew object called ");
        Accesschild ac2 = new Accesschild();
        System.out.println("z = "+Accesschild.z); //Accessible direclty as z is public
        ac2.incrementy();
        ac1.modify_z2(); // modifes z in all objects 

        System.out.println("\nnew object called ");
        Accesschild ac3 = new Accesschild();
        System.out.println("z = "+Accesschild.z); //Accessible direclty as z is public
    }
}