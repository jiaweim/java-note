package mjw.java.concurrency.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 13 Mar 2024, 6:52 PM
 */
public class Task2 implements Callable<Result> {

    private final String name;

    public Task2(String name) {
        this.name = name;
    }

    @Override
    public Result call() throws Exception {
        System.out.printf("%s: Staring\n", this.name);
        // 随机 sleep 一段时间
        try {
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n",
                    this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 计算 5 个随机数的加和，作为 Result
        int value = 0;
        for (int i = 0; i < 5; i++) {
            value += (int) (Math.random() * 100);
        }
        Result result = new Result();
        result.setName(this.name);
        result.setValue(value);
        System.out.println(this.name + ": Ends");

        return result;
    }
}
