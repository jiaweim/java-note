package mjw.java.concurrency.executor;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 13 Mar 2024, 7:24 PM
 */
public class Main3 {

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        // 创建 5 个任务
        System.out.printf("Main: Starting at: %s\n", new Date());
        for (int i = 0; i < 5; i++) {
            Task3 task = new Task3("Task " + i);
            // 用 schedule 提交任务
            executor.schedule(task, i + 1, TimeUnit.SECONDS);
        }
        // 请求关闭 executor
        executor.shutdown();
        // 等待所有任务结束
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 结束
        System.out.printf("Main: Ends at: %s\n", new Date());
    }
}
