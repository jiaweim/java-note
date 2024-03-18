package mjw.java.concurrency.forkjoin;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 18 Mar 2024, 8:58 PM
 */
public class Main {

    public static void main(String[] args) {
        // 创建 10000 个 products
        ProductListGenerator generator = new ProductListGenerator();
        List<Product> products = generator.generate(10000);
        // 创建 task，更新所有 products 的价格
        Task task = new Task(products, 0, products.size(), 0.20);
        // 创建 ForkJoinPool
        ForkJoinPool pool = new ForkJoinPool();
        // 执行任务
        pool.execute(task);
        // 每 5 毫秒查看一个线程池状态
        do {
            System.out.printf("Main: Thread Count:%d\n",
                    pool.getActiveThreadCount());
            System.out.printf("Main: Thread Steal:%d\n",
                    pool.getStealCount());
            System.out.printf("Main: Parallelism:%d\n",
                    pool.getParallelism());
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());
        // 关闭线程池
        pool.shutdown();
        // 使用 isCompletedNormally() 查看任务是否正常完成
        if (task.isCompletedNormally()) {
            System.out.printf("Main: The process has completed normally.\n");
        }
        // 执行完，所有 product 价格应该都是 12
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getPrice() != 12) {
                System.out.printf("Product %s: %f\n",
                        product.getName(), product.getPrice());
            }
        }
        System.out.println("Main: End of the program.\n");
    }
}
