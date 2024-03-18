package mjw.java.concurrency.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 17 Mar 2024, 8:56 PM
 */
public class RejectThreadPoolDemo {

    static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ":Thread ID:"
                    + Thread.currentThread().getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask task = new MyTask();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, 5, //  5 个常驻线程，最大线程数量 5，类似固定线程池
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10), // 等待队列 10
                Executors.defaultThreadFactory(),
                // 拒绝策略，比 DiscardPolicy 多一点点输出
                (r, executor1) -> System.out.println(r.toString() + " is discard"));
        // MyTask 执行需要 100 毫秒，因此会有大量 MyTask 被丢弃
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            executor.submit(task);
            Thread.sleep(10);
        }
    }
}
