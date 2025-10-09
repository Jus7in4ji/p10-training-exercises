class Supers1{
    Supers1(String str1 , String str2){
        System.out.println(" Use of super and this keyword without inner classes\n Message: "+str1 +str2);
    } 
}
class Thiss extends Supers1{
    Thiss(){
        this("Hello "); //passed to other constructor
    }
    Thiss(String str1){
        super(str1, "World"); // passed to second constructor of parent class
    }
}
public class Supers2{
    public static void main(String args[]){
        Thiss t = new Thiss();// calls second constructor of Supers indirectly 

    }
}