package mjw.java.concurrency.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Mar 2024, 4:28 PM
 */
public class CallableEx {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<Future<Integer>> resultList = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            int number = random.nextInt(10);
            // 创建新的任务
            FactorialCalculator calculator = new FactorialCalculator(number);
            // 提交任务，返回 Future，Future 可用于管理任务
            Future<Integer> result = executor.submit(calculator);
            resultList.add(result);
        }

        do {
            // 查看完成的任务数
            System.out.printf("Main: Number of Completed Tasks: %d\n", executor.getCompletedTaskCount());

            for (int i = 0; i < resultList.size(); i++) {
                Future<Integer> future = resultList.get(i);
                System.out.printf("Main: Task %d: %s\n", i, future.isDone());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (executor.getCompletedTaskCount() < resultList.size());

        // 到这里，所有任务已完成
        System.out.printf("Main: Results\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Integer> result = resultList.get(i);
            try {
                Integer number = result.get(); // get() 阻塞直到 call() 执行完
                System.out.printf("Main: Task %d: %d\n", i, number);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        // 关闭
        executor.shutdown();
    }
}
