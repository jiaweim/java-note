package mjw.java.concurrency.executor;

import java.util.concurrent.Callable;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 15 Mar 2024, 3:54 PM
 */
public class Task5 implements Callable<String> {
    @Override
    public String call() throws Exception {
        while (true) {
            System.out.printf("Task: Test\n");
            Thread.sleep(100);
        }
    }
}
