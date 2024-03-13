# 创建线程池

2024-03-06
@author Jiawei Mao

***

## 简介

创建 `ThreadPoolExecutor` 有两种方式：

- 使用 `ThreadPoolExecutor` 提供的 4 个构造函数
- 使用工厂类 `Executors`

创建 `Executor` 后，就可以执行 `Runnable` 或 `Callable` 对象。

关闭 `Executor`：

- 使用 `shutdown()` 关闭 `Executor`。`executor` 会等待正在运行或等待执行的任务完成，然后结束。

- 如果在调用 `shutdown()` 之后、executor 关闭之前提交任务，executor 会拒绝该任务。

下面通过示例介绍如何使用 `Executors` 类创建 `ThreadPoolExecutor` 对象，如何提交任务和拒绝任务。

## 示例

1. 创建 `Task` 任务类，实现 `Runnable` 接口

```java
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Task implements Runnable {

    // 任务创建时间
    private final Date initDate;
    // 任务名称
    private final String name;

    public Task(String name) {
        this.name = name;
        this.initDate = new Date();
    }

    @Override
    public void run() {
        System.out.printf("%s: Task %s: Created on: %s\n",
                Thread.currentThread().getName(),
                name, initDate);
        System.out.printf("%s: Task %s: Started on: %s\n",
                Thread.currentThread().getName(),
                name, new Date());
        
        try {
            // sleep 一段随机时间
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Task %s: Doing a task during %d seconds\n",
                    Thread.currentThread().getName(),
                    name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("%s: Task %s: Finished on: %s\n",
                Thread.currentThread().getName(),
                name, new Date());
    }
}
```

2. 创建 `RejectedTaskController`，实现 `RejectedExecutionHandler` 接口。

```java
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

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
```

3. 实现 `Server` 类，该类使用 executor 执行接收到的任务

```java
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private final ThreadPoolExecutor executor;

    public Server() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        RejectedTaskController controller = new RejectedTaskController();
        executor.setRejectedExecutionHandler(controller);
    }

    public void executeTask(Task task) {
        System.out.printf("Server: A new task has arrived\n");
        executor.execute(task);

        System.out.printf("Server: Pool Size: %d\n",
                executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n",
                executor.getActiveCount());
        System.out.printf("Server: Task Count: %d\n",
                executor.getTaskCount());
        System.out.printf("Server: Completed Tasks: %d\n",
                executor.getCompletedTaskCount());
    }

    public void endServer() {
        executor.shutdown();
    }
}
```

4. 实现 `Main` 类，在 `main` 方法中创建 100 个 tasks 并发送给 `Executor`

```java
public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        System.out.printf("Main: Starting.\n");
        for (int i = 0; i < 100; i++) {
            Task task = new Task("Task " + i);
            server.executeTask(task);
        }
        // 关闭 Server
        System.out.printf("Main: Shutting down the Executor.\n");
        server.endServer();

        // 发送一个新的 task，该 task 会被拒绝
        System.out.printf("Main: Sending another Task.\n");
        Task task = new Task("Rejected task");
        server.executeTask(task);
        
        System.out.printf("Main: End.\n");
    }
}
```

这个示例的关键是 Server 类。该类创建并使用 `ThreadPoolExecutor` 执行任务。

虽然 `ThreadPoolExecutor` 提供了 4 个构造函数，但是使用起来有点复杂，用 `Executors` 的工厂方法就简单多了。

上面用 `Executors.newFixedThreadPool()` 创建一个具有最大线程数的 `Executor`。如果任务数超过线程数，剩余的任务阻塞，直到有可用的空闲线程。该方法的参数为最大线程数，上例用 `Runtime.getRuntime().availableProcessors()` 返回 JVM 可用的处理器数，该数字通常与计算机的内核数一致。

重用线程的优点是减少了创建线程所花费的时间。缺点的线程池为任务提供了固定的驻留线程，如果向 `Executor `发送太多任务，有可能导致系统过载。

创建 `Executor` 后，可以使用 `execute()` 方法发送 `Runnable` 或 `Callable` 类型的任务来执行。在上例，发送的 `Task` 任务实现了 `Runnable` 接口。

上面还输出了 `Executor` 的一些日志消息。具体来说：

- `getPoolSize()`：线程池中实际的线程数；
- `getActiveCount()`：线程池中正在在还行任务的线程数；
- `getTaskCount()`：计划执行的任务总数。由于任务和线程的状态在计算过程中动态变化，所以返回的是近似值；
- `getCompletedTaskCount()`：已完成任务数。

`ThreadPoolExecutor` 以及其它的 `Executor` 实现，都必须显式结束。如果 executor 没有任务要执行，它会继续等待新任务，而不会自动结束。Java 应用只有在所有非守护线程执行完毕后才会结束，因此，如果不终止 executor，Java 程序不会结束。

调用 `ThreadPoolExecutor.shutdown()` 关闭 executor。当 executor 完成所有任务，调用 `shutdown()` 后，再次发送任务到 executor，executor 会拒绝并抛出 `RejectedExecutionException`，或者你单独实现了一个拒绝策略（如上所示）。实现拒绝策略，需要实现 `RejectedExecutionHandler` 接口，该接口只有一个方法 `rejectedExecution()`，包含两个参数：

- `Runnable` 对象，即被拒绝的任务；
- `Executor` 对象，即 executor 的引用。

对每个被 executor 拒绝的任务，都会调用该方法。通过 `ThreadPoolExecutor.setRejectedExecutionHandler()` 设置拒绝策略。

部分输出：

```
Server: A new task has arrived
Server: Pool Size: 32
Server: Active Count: 32
Server: Task Count: 100
Server: Completed Tasks: 3
Main: Shutting down the Executor.
Main: Sending another Task.
Server: A new task has arrived
RejectedTaskController: The task mjw.java.concurrency.executor.Task@5679c6c6 has been rejected
RejectedTaskController: java.util.concurrent.ThreadPoolExecutor@27ddd392[Shutting down, pool size = 32, active threads = 32, queued tasks = 65, completed tasks = 3]
RejectedTaskController: Terminating: true
RejectedTaksController: Terminated: false
```

