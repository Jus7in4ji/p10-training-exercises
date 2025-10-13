package basics;
public class Construct {
    static int count = 0;

    Construct(int x){
        count+=x;
    }
    public static void main(String[] args) {
        Construct c1 = new Construct(3);
        System.out.println("count + 3 = "+ c1.count);

        Construct c2 = new Construct(4);
        System.out.println("count + 4 = "+ c2.count);

        Construct c3 = new Construct(5);
        System.out.println("count + 5 = "+ c3.count);
    }
}
