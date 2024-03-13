package mjw.java.concurrency.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 13 Mar 2024, 6:54 PM
 */
public class Main2 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        // 创建 10 个 Task2
        List<Task2> taskList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Task2 task = new Task2("Task-" + i);
            taskList.add(task);
        }

        List<Future<Result>> resultList = null;
        try {
            // 所有任务完成，才返回
            resultList = executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();

        System.out.println("Main: Printing the results");
        for (Future<Result> future : resultList) {
            try {
                Result result = future.get();
                System.out.println(result.getName() + ": " + result.getValue());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
