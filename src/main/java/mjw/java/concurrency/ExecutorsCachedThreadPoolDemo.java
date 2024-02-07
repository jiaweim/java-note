package mjw.java.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 07 Feb 2024, 6:29 PM
 */
public class ExecutorsCachedThreadPoolDemo
{
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();

    }
}
