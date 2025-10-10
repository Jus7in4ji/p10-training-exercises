class Outerclass{
    int x = 20;
    class Innerclass{
        Innerclass(){
            x+=20;
        }
    }
    static class Inclass{
        void display(){
            System.out.println("Innerclass method called without initializing an outer object");

        }
    }
}
public class OutIn{
    public static void main (String[] args){
        Outerclass.Inclass outin = new Outerclass.Inclass();
        outin.display();
        Outerclass out = new Outerclass(); // outer object needed as innerclass initiates additon on x , which is defined in place by outer
        System.out.println("x in outerclass object : "+out.x);
        Outerclass.Innerclass in = out.new Innerclass();
        System.out.println("x in outerclass object after inner class called: "+out.x);
        //System.out.println("x in innerclass object : "+in.x);  shows error as variables of outerclass do not belong to inner class 

    }
}