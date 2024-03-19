package mjw.java.concurrency.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Mar 2024, 3:45 PM
 */
public class Main2 {

    public static void main(String[] args) {
        DocumentMock mock = new DocumentMock();
        // 生成文档，100 行，每行 1000 个单次
        String[][] document = mock.generateDocument(100, 1000, "the");
        // 创建任务
        DocumentTask task = new DocumentTask(document, 0, 100, "the");
        // 使用默认的 ForkJoinPool
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.execute(task);
        // 查看任务进展，直到任务结束
        do {
            System.out.printf("******************************************\n");
            System.out.printf("Main: Active Threads: %d\n",
                    commonPool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n",
                    commonPool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n",
                    commonPool.getStealCount());
            System.out.printf("******************************************\n");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());
        // 关闭 pool
        commonPool.shutdown();
        // 等待任务结束
        try {
            commonPool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.printf("Main: The word appears %d in the document", task.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
