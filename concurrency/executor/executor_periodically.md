# 周期执行

2024-03-13
@author Jiawei Mao

## 简介

`ThreadPoolExecutor` 对传入的任务，会尽快执行；任务结束，立即将其从 executor 删除；如果想执行相同任务，需要重新将其发送到 executor。

`ScheduledThreadPoolExecutor` 提供周期性执行任务的功能。

## 示例

1. 要周期性执行的任务类

```java
public class Task4 implements Runnable {

    private final String name;

    public Task4(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.printf("%s: Executed at: %s\n", name, new Date());
    }
}
```

2. main 类

```java
public class Main4 {
    public static void main(String[] args) {
        // 包含 1 个线程的线程池
        ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1);
        System.out.printf("Main: Starting at: %s\n", new Date());

        Task4 task = new Task4("Task");
        // 4 个参数：
        // - 要周期性执行的任务
        // - 延迟执行的时间
        // - 周期长度
        // - 时间单位
        ScheduledFuture<?> result = executor.scheduleAtFixedRate(task, 1,
                2, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            System.out.printf("Main: Delay: %d\n",
                              	result.getDelay(TimeUnit.MILLISECONDS));
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        // sleep 5 秒，保证周期任务结束
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Finished at: %s\n", new Date());
    }
}
```

需要注意的是，`scheduleAtFixedRate` 的周期，是任务开始的时间间隔，如果一个任务执行时间为 5 秒，周期为 3 秒，那么同一时间可能有 2 个任务在执行。

`scheduleAtFixedRate()` 返回 `ScheduledFuture` 对象：

- 上例执行的 Runnable 任务，没有返回值，所以其类型化参数为 `?`；
- `ScheduledFuture.getDelay()` 返回 scheduled 任务到下一次执行需要的时间。 

示例输出：

```
Main: Starting at: Wed Mar 13 20:54:33 CST 2024
Main: Delay: 999
Main: Delay: 496
Main: Delay: -8
Task: Executed at: Wed Mar 13 20:54:34 CST 2024
Main: Delay: 1489
Main: Delay: 975
Main: Delay: 473
Task: Executed at: Wed Mar 13 20:54:36 CST 2024
Main: Delay: 1971
Main: Delay: 1458
Main: Delay: 945
Main: Delay: 431
Task: Executed at: Wed Mar 13 20:54:38 CST 2024
Main: Finished at: Wed Mar 13 20:54:43 CST 2024
```

可以看到，任务每 2 秒执行一次，每 500 毫秒输出一次 delay 时间。

其它要点：

- `ScheduledThreadPoolExecutor.scheduleWithFixedRate()`  指定的时间间隔，为任务结束到任务开始的间隔；