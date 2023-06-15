package mjw.study.concurrency;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ConsoleClock implements Runnable {

    @Override
    public void run() {
        // 循环 10 此，每次创建一个 Date 对象
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n", new Date());
            try {
                // 睡 1 秒，挂起线程
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) { // sleep 可能抛出该异常
                System.out.println("The FileClock has been interrupted.");
            }
        }
    }
}
