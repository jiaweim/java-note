package mjw.java.concurrency.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 15 Mar 2024, 3:56 PM
 */
public class Main5 {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                .newCachedThreadPool();
        Task5 task = new Task5();
        System.out.printf("Main: Executing the Task\n");
        Future<String> result = executor.submit(task);
        // 让 main 线程 sleep 2 秒
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 取消任务
        System.out.printf("Main: Canceling the Task\n");
        result.cancel(true);
        // 查看是否真的取消了
        System.out.printf("Main: Canceled: %s\n", result.isCancelled());
        System.out.printf("Main: Done: %s\n", result.isDone());
        executor.shutdown();
        System.out.printf("Main: The executor has finished\n");
    }
}
