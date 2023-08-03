package mjw.study.java.util;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 02 8月 2023, 13:44
 */
public class TimerTest {

    @Test
    public void schedule_task_delay() {
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("Task performed on: " + new Date() + "n" +
                        "Thread's name: " + Thread.currentThread().getName());
            }
        };
        Timer timer = new Timer("Timer");

        long delay = 1000L;
        timer.schedule(task, delay);
    }

}
