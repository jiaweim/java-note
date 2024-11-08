package mjw.java.concurrency.executor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 08 Feb 2024, 16:14
 */
public class Task implements Runnable {

    // 任务创建时间
    private final Date initDate;
    // 任务名称
    private final String name;

    public Task(String name) {
        this.name = name;
        this.initDate = new Date();
    }

    @Override
    public void run() {
        System.out.printf("%s: Task %s: Created on: %s\n",
                Thread.currentThread().getName(),
                name, initDate);
        System.out.printf("%s: Task %s: Started on: %s\n",
                Thread.currentThread().getName(),
                name, new Date());

        try {
            // sleep 随机时间
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Task %s: Doing a task during %d seconds\n",
                    Thread.currentThread().getName(),
                    name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("%s: Task %s: Finished on: %s\n",
                Thread.currentThread().getName(),
                name, new Date());
    }
}
