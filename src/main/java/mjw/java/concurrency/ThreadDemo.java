package mjw.java.concurrency;

public class ThreadDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() ->
                System.out.println("Thread:" + Thread.currentThread()));
        thread.start();
    }
}
