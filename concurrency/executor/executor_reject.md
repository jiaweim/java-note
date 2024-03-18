# 拒绝任务

2024-03-18
@author Jiawei Mao

## 简介

`ThreadPoolExecutor` 构造函数的最后一个参数指定拒绝策略。

当任务数量超过系统实际承载能力，就要用到拒绝策略。拒绝策略是系统超负荷运行的补救措施。线程池中的线程用完了，同时，等待队列也已经排满了，放不下新的任务，此时需要拒绝策略。

JDK 内置的 4 种拒绝策略：

- `AbortPolicy`：直接抛出异常，系统停止工作；
- `CallerRunsPolicy`：如果线程池未关闭，直接在调用者线程中运行当前被丢弃的任务。这样不会真的丢弃任务，但是，任务提交线程的性能会受影响；
- `DiscardOldestPolicy`：丢弃最老的一个任务（即将被执行的任务），并尝试再次提交当前任务；
- `DiscardPolicy`：默默丢弃无法处理的任务，不做任何处理。

以上策略均实现了 `RejectedExecutionHandler` 接口。该接口很简单：

```java
public interface RejectedExecutionHandler {
    void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
}
```

`r` 是请求执行的任务，`executor` 为当前线程池。

## 示例

```java
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RejectThreadPoolDemo {

    static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ":Thread ID:"
                    + Thread.currentThread().getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask task = new MyTask();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, 5, //  5 个常驻线程，最大线程数量 5，类似固定线程池
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10), // 等待队列 10
                Executors.defaultThreadFactory(),
                // 拒绝策略，比 DiscardPolicy 多一点点输出
                (r, executor1) -> System.out.println(r.toString() + " is discard"));
        // MyTask 执行需要 100 毫秒，因此会有大量 MyTask 被丢弃
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            executor.submit(task);
            Thread.sleep(10);
        }
    }
}
```

```
1710728543908:Thread ID:28
1710728543924:Thread ID:29
1710728543939:Thread ID:30
1710728543955:Thread ID:31
java.util.concurrent.FutureTask@136432db[Not completed, task = java.util.concurrent.Executors$RunnableAdapter@2e5c649[Wrapped task = mjw.java.concurrency.executor.RejectThreadPoolDemo$MyTask@383534aa]] is discard
```

