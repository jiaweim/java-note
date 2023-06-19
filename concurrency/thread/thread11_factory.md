# 用工厂设计模式创建线程

2023-06-16
****
## 1.1. 简介

用工厂设计模式创建对象的优点有：

- 修改类比较容易
- 更容易限制创建对象的资源，例如，对指定类型只允许存在 n 个对象
- 容易生成关于创建对象的统计数据

ThreadFactory 接口提供对 Thread 工厂设计模式的实现。

## 1.2. 示例

演示实现 `ThreadFactory` 接口，为 `Thread` 设置个性化名称，同时保存创建的线程的统计信息。

- 实现 `ThreadFactory` 接口

ThreadFactory 只有一个方法 `newThread(Runnable r)`，使用 Runnable 对象创建 Thread。

```java
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;

// 实现 ThreadFactory
public class MyThreadFactory implements ThreadFactory {

    private int counter; // 创建的线程数
    private String name; // 线程名称前缀
    private List<String> stats; // 创建线程的统计信息

    public MyThreadFactory(String name) {
        counter = 0;
        this.name = name;
        stats = new ArrayList<>();
    }

    @Override
    public Thread newThread(Runnable r) {
        // 创建线程
        Thread t = new Thread(r, name + "-Thread_" + counter);
        counter++;
        // 保存线程信息
        stats.add(String.format("Created thread %d with name %s on %s\n", t.getId(), t.getName(), new Date()));
        return t;
    }

    // 返回 ThreadFactory 创建线程的统计信息
    public String getStats() {
        StringBuffer buffer = new StringBuffer();

        for (String stat : stats) {
            buffer.append(stat);
        }

        return buffer.toString();
    }
}
```

- 任务实现

sleep 一秒，不敢其它事。

```java  
import java.util.concurrent.TimeUnit;

public class Task implements Runnable {
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

- main 实现

创建 ThreadFactory，然后用 ThreadFactory 创建 10 个线程。

```java
public class Main {
    public static void main(String[] args) {
        MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
        Task task = new Task();
        Thread thread;

        System.out.printf("Starting the Threads\n");
        for (int i = 0; i < 10; i++) {
            thread = factory.newThread(task);
            thread.start();
        }
        // 输出 ThreadFactory 的统计信息
        System.out.printf("Factory stats:\n");
        System.out.printf("%s\n", factory.getStats());
    }
}
```

```
Starting the Threads
Factory stats:
Created thread 25 with name MyThreadFactory-Thread_0 on Fri Jun 16 10:04:20 CST 2023
Created thread 26 with name MyThreadFactory-Thread_1 on Fri Jun 16 10:04:20 CST 2023
Created thread 27 with name MyThreadFactory-Thread_2 on Fri Jun 16 10:04:20 CST 2023
Created thread 28 with name MyThreadFactory-Thread_3 on Fri Jun 16 10:04:20 CST 2023
Created thread 29 with name MyThreadFactory-Thread_4 on Fri Jun 16 10:04:20 CST 2023
Created thread 30 with name MyThreadFactory-Thread_5 on Fri Jun 16 10:04:20 CST 2023
Created thread 31 with name MyThreadFactory-Thread_6 on Fri Jun 16 10:04:20 CST 2023
Created thread 32 with name MyThreadFactory-Thread_7 on Fri Jun 16 10:04:20 CST 2023
Created thread 33 with name MyThreadFactory-Thread_8 on Fri Jun 16 10:04:20 CST 2023
Created thread 34 with name MyThreadFactory-Thread_9 on Fri Jun 16 10:04:20 CST 2023
```