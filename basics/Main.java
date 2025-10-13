package basics;
public class Main {
    private int n = 7;
    public static void main(String[] args) {
        System.err.println("hello world ");
    }
    
    public int returnlength( String x){
        return x.length();
    }
    public int displayn(){
        return n ;

    }
    public void modifyn(int m){
        this.n = m; // change instance of n to m for the object 
    }
}
