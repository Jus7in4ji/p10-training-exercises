package basics;
public class ConstructorOverLoading{

    String name = "Justin";

    ConstructorOverLoading(){
        System.out.println(this.name);
    }
    ConstructorOverLoading(String newname){
        this.name = newname;

    }
    ConstructorOverLoading(int x ){
        System.out.println(x);
    }
    public static void main(String args[]){
        ConstructorOverLoading cl1 = new ConstructorOverLoading(); // prints name 
        ConstructorOverLoading cl2 = new ConstructorOverLoading(45);// prints number entered
        ConstructorOverLoading cl3 = new ConstructorOverLoading("Justin K A"); // replaces name 
        System.out.println("new name : "+cl3.name);

        
    }
}