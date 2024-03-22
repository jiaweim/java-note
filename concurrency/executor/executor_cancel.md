# 取消任务

2024-03-15
@author Jiawei Mao
## 简介

如何取消发送到 executor 的任务：通过 `Future.cancel()` 方法取消任务。

## 示例

1. 任务类

```java
public class Task5 implements Callable<String> {
    @Override
    public String call() throws Exception {
        // 加个无限循环，这个任务需要被取消
        while (true) {
            System.out.printf("Task: Test\n");
            Thread.sleep(100);
        }
    }
}
```

2. main 类

```java
public class Main5 {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                .newCachedThreadPool();
        Task5 task = new Task5();
        System.out.printf("Main: Executing the Task\n");
        Future<String> result = executor.submit(task);
        // 让 main 线程 sleep 2 秒
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 取消任务
        System.out.printf("Main: Canceling the Task\n");
        result.cancel(true);
        // 查看是否真的取消了
        System.out.printf("Main: Canceled: %s\n", result.isCancelled());
        System.out.printf("Main: Done: %s\n", result.isDone());
        executor.shutdown();
        System.out.printf("Main: The executor has finished\n");
    }
}
```

```
Main: Executing the Task
Task: Test
...
Task: Test
Main: Canceling the Task
Main: Canceled: true
Main: Done: true
Main: The executor has finished
```

根据 `cancel()` 的参数和 task 的状态不同，`cancel()` 的行为不同：

- 如果任务已完成，或之前已经取消，或者因为其它原因无法取消，`cancel()` 会返回 `false`，表示该取消操作失败；
- 如果任务在 executor 中挂起，则该任务被取消，不会被执行。
- 如果任务正在执行，则取决于 `cancel()` 的参数
  - `true`，表示强制取消
  - `false`，表示既然已经在运行，那就等你运行完吧

对已经 `cancel` 的 `Future` 对象，调用 `get()` 抛出 `CancellationException`。

