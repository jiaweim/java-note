package mjw.java.concurrency.executor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 18 Mar 2024, 11:20 AM
 */
public class Main6 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        ResultTask[] resultTasks = new ResultTask[5];
        for (int i = 0; i < 5; i++) {
            ExecutableTask task = new ExecutableTask("Task " + i);
            resultTasks[i] = new ResultTask(task);
            executor.submit(resultTasks[i]);
        }
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        // 取消任务
        for (ResultTask resultTask : resultTasks) {
            resultTask.cancel(true);
        }

        for (ResultTask resultTask : resultTasks) {
            try {
                // 看看没有被取消任务的结果
                if (!resultTask.isCancelled()) {
                    System.out.printf("%s\n", resultTask.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }
}
