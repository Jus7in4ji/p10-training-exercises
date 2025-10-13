package pack2;

public class Dvision{
    public float div(int a , int b){
        float ans;
        try {
            ans =(float) a / b;
        }
        catch (ArithmeticException e) {
            ans = 0;
        }
        return ans;
    }
    public int divint(int a , int b){
        int ans;
        try {
            ans = a / b;
        }
        catch (ArithmeticException e) {
            ans = 0;
        }
        return ans;
    }
}