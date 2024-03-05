package mjw.java.concurrency.executor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 08 Feb 2024, 16:21
 */
public class RejectedTaskController implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 输出被拒绝 task 的名称和 executor 的状态
        System.out.printf("RejectedTaskController: The task %s has been rejected\n",
                r.toString());
        System.out.printf("RejectedTaskController: %s\n",
                executor.toString());
        System.out.printf("RejectedTaskController: Terminating: %s\n",
                executor.isTerminating());
        System.out.printf("RejectedTaksController: Terminated: %s\n",
                executor.isTerminated());
    }
}
