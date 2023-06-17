# 线程局部变量

2023-06-15
****
## 1.1. 简介

并发程序的一个关键是共享数据。对扩展 Thread 类或实现 Runnable 接口的对象，在多线程中共享数据特别重要。

如果实现 Runnable 接口创建一个对象，然后使用同一个 Runnable 对象启动多个线程，那么所有线程将共享相同的 Runnable 属性。这意味着一个线程修改 Runnable 属性，其它线程都受影响。

同一个 Runnable 的属性如何不在多个线程中共享，线程局部变量（thread-local variable）实现了该功能，且性能很好。


## 1.2. 示例

下面创建两个程序，一个演示共享变量的问题，一个通过 thread-local 变量结局该问题。

- 共享变量 task，定义为 UnsafeTask

演示当不同线程共享数据时可能出现的问题

```java
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UnsafeTask implements Runnable {

    // 所有线程共享
    private Date startDate;

    // 保存开始 date，并在运行开始和结束时写入 console
    @Override
    public void run() {
        startDate = new Date(); // 初始化 date
        System.out.printf("Starting Thread: %s : %s\n",
                Thread.currentThread().getId(), startDate);
        try {
	        // 随机 sleep 几秒
            TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Thread Finished: %s : %s\n",
                Thread.currentThread().getId(), startDate);
    }
}
```

- 共享变量 task 的 main 

```java
import java.util.concurrent.TimeUnit;

public class Main {
    
    // 创建 1 个 Runnable，10 个线程
    public static void main(String[] args) {
        // 创建 task
        UnsafeTask task = new UnsafeTask();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(task);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(2); // 每个线程相隔 2 秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

```
Starting Thread: 25 : Thu Jun 15 09:06:20 CST 2023
Starting Thread: 26 : Thu Jun 15 09:06:22 CST 2023
Thread Finished: 26 : Thu Jun 15 09:06:22 CST 2023
Starting Thread: 27 : Thu Jun 15 09:06:24 CST 2023
Starting Thread: 28 : Thu Jun 15 09:06:26 CST 2023
Thread Finished: 25 : Thu Jun 15 09:06:26 CST 2023
Starting Thread: 29 : Thu Jun 15 09:06:28 CST 2023
Thread Finished: 27 : Thu Jun 15 09:06:28 CST 2023
Thread Finished: 29 : Thu Jun 15 09:06:28 CST 2023
Starting Thread: 30 : Thu Jun 15 09:06:30 CST 2023
Thread Finished: 28 : Thu Jun 15 09:06:30 CST 2023
Thread Finished: 30 : Thu Jun 15 09:06:30 CST 2023
Starting Thread: 31 : Thu Jun 15 09:06:32 CST 2023
Thread Finished: 31 : Thu Jun 15 09:06:32 CST 2023
Starting Thread: 32 : Thu Jun 15 09:06:34 CST 2023
Starting Thread: 33 : Thu Jun 15 09:06:36 CST 2023
Starting Thread: 34 : Thu Jun 15 09:06:38 CST 2023
Thread Finished: 32 : Thu Jun 15 09:06:38 CST 2023
Thread Finished: 34 : Thu Jun 15 09:06:38 CST 2023
Thread Finished: 33 : Thu Jun 15 09:06:38 CST 2023
```

如果 startDate 没有被其它线程修改，开始和结束输出的时间应该相同。然而，以线程 25 为例，其结束时 startDate 变了，和线程 28  的一样，说明其 startDate 被线程 28 修改了。线程实现一个线程安全版本。

- 定义 SafeTask

除了将 Date 替换为 ThreadLocal，其它与 UnsafeTask 一样

```java
import java.util.Date;
import java.util.concurrent.TimeUnit;

// 使用 ThreadLocal 变量
public class SafeTask implements Runnable {

    /**
     * ThreadLocal shared between the Thread objects
     */
    private static ThreadLocal<Date> startDate = new ThreadLocal<Date>() {
        protected Date initialValue() { // 该方法返回实际日期
            return new Date();
        }
    };

    @Override
    public void run() {
        // Writes the start date
        System.out.printf("Starting Thread: %s : %s\n",
                Thread.currentThread().getId(), startDate.get());
        try {
            TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Writes the start date
        System.out.printf("Thread Finished: %s : %s\n",
                Thread.currentThread().getId(), startDate.get());
    }
}
```

- SafeTask 的 main 实现

除了将 UnsafeTask 替换为 SafeTask，其它与 UnsafeTask 的 main 一样。

```java
import java.util.concurrent.TimeUnit;

public class SafeMain {

    public static void main(String[] args) {
        SafeTask task = new SafeTask();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(task);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.start();
        }
    }
}
```

```
Starting Thread: 25 : Thu Jun 15 09:15:18 CST 2023
Starting Thread: 26 : Thu Jun 15 09:15:20 CST 2023
Starting Thread: 27 : Thu Jun 15 09:15:22 CST 2023
Thread Finished: 27 : Thu Jun 15 09:15:22 CST 2023
Starting Thread: 28 : Thu Jun 15 09:15:24 CST 2023
Thread Finished: 26 : Thu Jun 15 09:15:20 CST 2023
Thread Finished: 25 : Thu Jun 15 09:15:18 CST 2023
Starting Thread: 29 : Thu Jun 15 09:15:26 CST 2023
Thread Finished: 29 : Thu Jun 15 09:15:26 CST 2023
Starting Thread: 30 : Thu Jun 15 09:15:28 CST 2023
Starting Thread: 31 : Thu Jun 15 09:15:30 CST 2023
Starting Thread: 32 : Thu Jun 15 09:15:32 CST 2023
Thread Finished: 28 : Thu Jun 15 09:15:24 CST 2023
Starting Thread: 33 : Thu Jun 15 09:15:34 CST 2023
Starting Thread: 34 : Thu Jun 15 09:15:36 CST 2023
Thread Finished: 31 : Thu Jun 15 09:15:30 CST 2023
Thread Finished: 30 : Thu Jun 15 09:15:28 CST 2023
Thread Finished: 33 : Thu Jun 15 09:15:34 CST 2023
Thread Finished: 34 : Thu Jun 15 09:15:36 CST 2023
Thread Finished: 32 : Thu Jun 15 09:15:32 CST 2023
```

可以发现，现在每个线程从开始到结束 date 不变。

## 1.3. 总结

`ThreadLocal` 为每个使用 thread-local 变量的线程都单独存储一个变量值：

- 使用 `get()` 返回值
- 使用 `set()` 设置值

第一次调用 `get()` 时，由于 thread-local 还没设置值，它会调用 `initialValue()` 获得初始值。

ThreadLocal 还提供了 remove() 方法，用于删除调用线程存储在其中的变量值。

#TODO
另外，Java 还提供了 `InheritableThreadLocal` 类用于支持可继承的 thread-local 变量。例如，如果线程 A 有一个 thread-local 变量，然后线程 A 创建了线程 B..
