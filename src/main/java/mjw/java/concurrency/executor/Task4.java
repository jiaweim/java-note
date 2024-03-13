package mjw.java.concurrency.executor;

import java.util.Date;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 13 Mar 2024, 8:41 PM
 */
public class Task4 implements Runnable {

    private final String name;

    public Task4(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.printf("%s: Executed at: %s\n", name, new Date());
    }
}
