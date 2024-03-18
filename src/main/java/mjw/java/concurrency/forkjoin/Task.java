package mjw.java.concurrency.forkjoin;

import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 18 Mar 2024, 8:54 PM
 */
public class Task extends RecursiveAction {

    private List<Product> products;
    // 指定要处理的元素范围
    private int first;
    private int last;
    // 更新价格值
    private double increment;

    public Task(List<Product> products, int first, int last,
            double increment) {
        this.products = products;
        this.first = first;
        this.last = last;
        this.increment = increment;
    }

    @Override
    protected void compute() {
        // 如果 first 和 last 差别小于 10，更新价格
        if (last - first < 10) {
            updatePrices();
        } else { // 否则，创建子任务
            int middle = (last + first) / 2;
            System.out.printf("Task: Pending tasks:%s\n",
                    getQueuedTaskCount());
            Task t1 = new Task(products, first, middle + 1, increment);
            Task t2 = new Task(products, middle + 1, last, increment);
            // invokeAll 运行这两个子任务
            invokeAll(t1, t2);
        }
    }

    private void updatePrices() {
        for (int i = first; i < last; i++) {
            Product product = products.get(i);
            product.setPrice(product.getPrice() * (1 + increment));
        }
    }
}
