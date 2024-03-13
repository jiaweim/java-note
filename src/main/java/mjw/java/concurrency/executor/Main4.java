package mjw.java.concurrency.executor;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 13 Mar 2024, 8:42 PM
 */
public class Main4 {

    public static void main(String[] args) {
        // 包含 1 个线程的线程池
        ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1);
        System.out.printf("Main: Starting at: %s\n", new Date());

        Task4 task = new Task4("Task");
        // 4 个参数：
        // - 要周期性执行的任务
        // - 延迟执行的时间
        // - 周期长度
        // - 时间单位
        ScheduledFuture<?> result = executor.scheduleAtFixedRate(task, 1,
                2, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            System.out.printf("Main: Delay: %d\n", result.getDelay(TimeUnit.MILLISECONDS));
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        // sleep 5 秒，保证周期任务结束
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Finished at: %s\n", new Date());
    }
}
