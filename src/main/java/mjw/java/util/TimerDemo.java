package mjw.java.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 02 8月 2023, 13:58
 */
public class TimerDemo {

    public static void main(String[] args) {
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("Task performed on: " + new Date() + "\n" +
                        "Thread's name: " + Thread.currentThread().getName());
            }
        };
        Timer timer = new Timer("Timer");

        long delay = 1000L;
        timer.schedule(task, delay);
    }
}
