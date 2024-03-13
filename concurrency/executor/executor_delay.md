# 延迟执行

2024-03-13
@author Jiawei Mao

## 简介

`ScheduledExecutorService` 接口及其实现 `ScheduledThreadPoolExecutor` 提供延迟和周期执行任务的功能。

## 示例

1. 创建任务类

```java
import java.util.Date;
import java.util.concurrent.Callable;

public class Task3 implements Callable<String> {

    private final String name;

    public Task3(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        // 输出当前时间
        System.out.printf("%s: Starting at : %s\n", name, new Date());
        return "Hello, world";
    }
}
```

2. 创建 main 类

```java
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main3 {

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        // 创建 5 个任务
        System.out.printf("Main: Starting at: %s\n", new Date());
        for (int i = 0; i < 5; i++) {
            Task task = new Task("Task " + i);
            // 用 schedule 提交任务
            executor.schedule(task, i + 1, TimeUnit.SECONDS);
        }
        // 请求关闭 executor
        executor.shutdown();
        // 等待所有任务结束
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 结束
        System.out.printf("Main: Ends at: %s\n", new Date());
    }
}
```

这个示例的关键是 `ScheduledExecutorService` 接口及其实现 `ScheduledThreadPoolExecutor`，上面直接使用通常方法 `Executors.newScheduledThreadPool(1)` 创建 `ScheduledThreadPoolExecutor` 实例，参数为线程池的核心线程数。

使用 `schedule()` 方法提交需要延迟执行的任务，该方法有 3 个参数：

- 需要执行的任务；
- 延迟时间数值；
- 延迟时间单位。

示例输出：

```
Main: Starting at: Wed Mar 13 19:38:03 CST 2024
Task 0: Starting at : Wed Mar 13 19:38:04 CST 2024
Task 1: Starting at : Wed Mar 13 19:38:05 CST 2024
Task 2: Starting at : Wed Mar 13 19:38:06 CST 2024
Task 3: Starting at : Wed Mar 13 19:38:07 CST 2024
Task 4: Starting at : Wed Mar 13 19:38:08 CST 2024
Main: Ends at: Wed Mar 13 19:38:08 CST 2024
```

可以看到，每个任务开始执行的时间相差 1 秒。

其它要点：

- `ScheduledThreadPoolExecutor.schedule()` 同时支持 `Runnable` 和 `Callable` 类型任务；
- 虽然 `ScheduledThreadPoolExecutor` 是 `ThreadPoolExecutor` 的子类，因此包含 `ThreadPoolExecutor` 的所有功能，但建议只对 scheduled 任务使用 `ScheduledThreadPoolExecutor`;
- 在调用 `shutdown()` 方法可以配置 `ScheduledThreadPoolExecutor`，假设关闭 executor 时依然有挂起的任务：
  - 默认行为：虽然已经请求关闭 executor，依然执行这些挂起的任务；
  - 调用 `setExecuteExistingDelayedTasksAfterShutdownPolicy()` 可以修改默认行为，传入 `false` 表示调用 `shotdown()` 后，不再执行那些挂起的任务。