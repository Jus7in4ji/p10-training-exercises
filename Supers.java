public class Supers{
    Supers(){
        System.out.println("regular outer object initialization");
    }
    Supers(String str1 , String str2){
        System.out.println(str1 +str2);
    }
    class Thiss extends Supers{
        Thiss(){
            this("Hello ");
        }
        Thiss(String str1){
            super(str1, "World");
        }
    }

    public static void main(String args[]){
        Supers s = new Supers();
        Supers.Thiss t = s.new Thiss();

    }
}