# 控制任务

2024-03-18
@author Jiawei Mao

## 简介

`FutureTask` 是一个可取消的异步计算任务。它实现了 `Runnable` 和 `Future` 接口，提供 `Future` 接口的基本实现。可以使用 `Callable` 或 `Runnable` 对象创建 `FutureTask`。

`FutureTask` 提供取消执行、获取执行结果的方法。其 `done()` 方法用于在任务结束后执行操作，可用于执行后处理操作，如释放资源。在 `FutureTask` 的状态转变为 `isDone`，会自动执行 `done()` 方法。

## 示例

1. 实现 `Callable` 的任务类

```java
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ExecutableTask implements Callable<String> {

    private final String name;

    public ExecutableTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String call() throws Exception {
        try {
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n",
                    this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
        }
        return "Hello, world. I'm " + name;
    }
}
```

2. 使用 `Callable` 对象创建 `FutureTask`

```java
import java.util.concurrent.FutureTask;

public class ResultTask extends FutureTask<String> {

    private final String name;

    public ResultTask(ExecutableTask callable) {
        super(callable);
        this.name = callable.getName();
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            System.out.printf("%s: Has been canceled\n", name);
        } else {
            System.out.printf("%s: Has finished\n", name);
        }
    }
}
```

在任务完成时，`done()` 方法被 `FutureTask` 自动调用。

3. main 类

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
```

```
Task 1: Waiting 5 seconds for results.
Task 2: Waiting 8 seconds for results.
Task 0: Waiting 4 seconds for results.
Task 3: Waiting 8 seconds for results.
Task 4: Waiting 5 seconds for results.
Task 0: Has finished
Task 1: Has been canceled
Task 2: Has been canceled
Task 3: Has been canceled
Task 4: Has been canceled
Hello, world. I'm Task 0
```

