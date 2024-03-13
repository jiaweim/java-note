package mjw.java.concurrency.executor;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 13 Mar 2024, 7:23 PM
 */
public class Task3 implements Callable<String> {

    private final String name;

    public Task3(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        // 输出当前时间
        System.out.printf("%s: Starting at : %s\n", name, new Date());
        return "Hello, world";
    }
}
