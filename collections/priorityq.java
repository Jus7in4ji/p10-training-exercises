package collections;
import java.util.Scanner;
import java.util.Queue;
import java.util.PriorityQueue;
class priorityq {
     public static void main(String args[]){
        Queue<Integer> pq = new PriorityQueue<Integer>();
        Scanner sc = new Scanner(System.in);
        System.out.println("No of elements to be inserted ");
        int n=0,x = sc.nextInt();
        while(n<x){
            System.out.println("element "+(n+1)+": ");
            pq.add(sc.nextInt());
            n+=1;
        }

        System.out.println("peek = "+pq.peek()+"\n queue before poll: "+pq);
        System.out.println("poll = "+pq.poll()+"\n queue after poll: "+pq);

        System.out.println("new top of queue: "+pq.peek());
    }
}