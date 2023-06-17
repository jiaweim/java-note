# 线程分组

2023-06-16
****
## 1.1. 简介

Java 提供了线程分组功能，`java.lang.ThreadGroup` 类实现了该功能。通过将多个线程放在一组，可以对多个线程同时操作，例如一次操作打断所有线程。

`ThreadGroup` 可以包含线程和其它 `ThreadGroup` 对象。除了初始的 `ThreadGroup`，其它 `ThreadGroup` 都有一个父 `ThreadGroup`，组成 tree 结构。

在创建线程时若没有指定分组，会自动分组到父线程所在的分组。所谓父线程，就是启动该线程的线程，比如，在 `main` 中调用 `.start()`，其父线程就是 `main` 线程。例如：

```java
@Test
public void testThreadGroup(){
    Thread t1 = new Thread("t1");
    ThreadGroup group = new ThreadGroup("group1");
    Thread t2 = new Thread(group, "t2");
    ThreadGroup testGroup = Thread.currentThread().getThreadGroup();
    assertEquals(testGroup.getName(), "main");
    assertSame(testGroup, t1.getThreadGroup());
    assertSame(group, t2.getThreadGroup());
    assertNotEquals(testGroup, t2.getThreadGroup());
}
```

可以发现，testGroup 属于 main `ThreadGroup`，`t1` 由于没有指定分组，默认属于 main 分组，`t2` 指定了分组，所以归属于指定的 `testGroup`。

我们已了解如何捕获线程中的 unchecked 异常，`ThreadGroup` 也有类似功能。

## 1.2. 示例

演示如何使用 `ThreadGroup`，以及为 `ThreadGroup` 设置 unchecked 异常处理器。

- 扩展 `ThreadGroup` 实现 `MyThreadGroup`

覆盖 `uncaughtException()` 以处理 `ThreadGroup` 所含线程抛出的 unchecked 异常。

```java
public class MyThreadGroup extends ThreadGroup {
    public MyThreadGroup(String name) {
        super(name);
    }

    // 当 ThreadGroup 中某个线程抛出 unchecked 异常，该方法被调用
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 输出抛出异常的线程的名称
        System.out.printf("The thread %s has thrown an Exception\n", t.getId());
        // 输出异常的 stack trace
        e.printStackTrace(System.out);
        // 中断 ThreadGroup 的其它线程
        System.out.printf("Terminating the rest of the Threads\n");
        interrupt(); // 这是 ThreadGroup 的方法，用于中断线程组中所有线程
    }
}
```

- 实现 `Task` 类

在 `run()` 中抛出 `AritmethicException`：用 1000 除以随机数，当随机到 0，抛出该异常。

```java
import java.util.Random;

public class Task implements Runnable {

    @Override
    public void run() {
        int result;
        // 创建一个随机数生成器
        Random random = new Random(Thread.currentThread().getId());
        while (true) {
            // 生成一个随机数，计算除法
            result = 1000 / ((int) (random.nextDouble() * 1000000000));
            // 检查是否有中断请求
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("%d : Interrupted\n",
                        Thread.currentThread().getId());
                return;
            }
        }
    }
}
```

- main 实现

```java
public class Main {
    public static void main(String[] args) {
        // 创建这些线程
        int numberOfThreads = 2 * Runtime.getRuntime().availableProcessors();

        // 创建 MyThreadGroup
        MyThreadGroup threadGroup = new MyThreadGroup("MyThreadGroup");

        // 创建 Task 对象
        Task task = new Task();

        // 创建线程，放进 ThreadGroup
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(threadGroup, task);
            t.start();
        }

        // 输出 ThreadGroup 信息
        System.out.printf("Number of Threads: %d\n", threadGroup.activeCount());
        System.out.printf("Information about the Thread Group\n");
        threadGroup.list();

        // 输出 ThreaGroup 中线程的信息
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        for (int i = 0; i < threadGroup.activeCount(); i++) {
            System.out.printf("Thread %s: %s\n",
                    threads[i].getName(), threads[i].getState());
        }
    }
}
```

```
Number of Threads: 4
Information about the Thread Group
mjw.study.concurrency.MyThreadGroup[name=MyThreadGroup,maxpri=10]
    Thread[Thread-0,5,MyThreadGroup]
    Thread[Thread-1,5,MyThreadGroup]
    Thread[Thread-2,5,MyThreadGroup]
    Thread[Thread-3,5,MyThreadGroup]
Thread Thread-0: RUNNABLE
Thread Thread-1: RUNNABLE
Thread Thread-2: RUNNABLE
Thread Thread-3: RUNNABLE
The thread 25 has thrown an Exception
java.lang.ArithmeticException: / by zero
	at java.module/mjw.study.concurrency.Task.run(Task.java:14)
	at java.base/java.lang.Thread.run(Thread.java:829)
Terminating the rest of the Threads
28 : Interrupted
27 : Interrupted
26 : Interrupted
```

`ThreadGroup` 的 `list() `输出包含的线程信息。然后 thread 25 抛出异常，并中断其它线程：

```
The thread 25 has thrown an Exception
java.lang.ArithmeticException: / by zero
	at java.module/mjw.study.concurrency.Task.run(Task.java:14)
	at java.base/java.lang.Thread.run(Thread.java:829)
Terminating the rest of the Threads
28 : Interrupted
27 : Interrupted
26 : Interrupted
```

当线程中抛出 unchecked 异常，JVM 会寻找三个可能的 exceptionHandler。

首先，查找线程的 unchecked 异常的 exceptionHandler；如果没有设置该 handler，继续找该线程所属 TheadGroup 的 exceptionHandler，即本节内容；如果该 handler 也没有，JVM 查找默认的 unchecked 异常的 exceptionHandler，即通过静态方法设置的 exceptionHandler。

如果三个 handler 都没有，JVM 将异常的 stack trace 信息输出到控制台，并结束抛出异常的线程。

## 1.3. 总结

`ThreadGroup` 有两个构造函数

- `ThreadGroup(ThreadGroup parent, String name)`, 指定名称和父线程组
- `ThreadGroup(String name)`, 采用当前线程所在线程组为父线程组

**ThreadGroup 方法**

| 方法                     | 说明                                            |
| ------------------------ | ----------------------------------------------- |
| `checkAccess()`            | 当前运行的线程是否有权限修改线程组              |
| `activeCount()`            | 线程组及其子线程组中活动线程数目            |
| `activeGroupCount()`       | 线程组及其子线程组中活动线程组数目          |
| `destroy()`                | 销毁当前线程组及其所有子线程组                  |
| `enumerate(Thread[] list)` | 将线程组及其子组中所有活动线程复制到指定数组    |
| `getMaxPriority()`         | 线程组最大优先级                                |
| `getName()`                | 线程组名称                                      |
| `getParent()`              | 返回父线程组                                    |
| `interrupt()`              | 中断线程组中所有线程                            |
| `isDaemon()`               | 是否是守护线程组                                |
| `isDestroyed()`            | 线程组是否被销毁                                |
| `list()`                   | 将线程组相关信息打印到标准输出                  |
| `parentOf(ThreadGroup g)`  | 如果线程组是参数线程组，或其父线程组，返回 true |
