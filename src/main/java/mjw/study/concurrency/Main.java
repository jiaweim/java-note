package mjw.study.concurrency;

import java.util.concurrent.TimeUnit;

public class Main {

    // 创建 1 个 Runnable，10 个线程
    public static void main(String[] args) {
        // 创建 task
        UnsafeTask task = new UnsafeTask();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(task);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
