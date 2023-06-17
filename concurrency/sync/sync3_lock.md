# 基于 Lock 的同步

2023-06-17, 09:14
****
## 1.1. 简介

lock 同步机制比 `synchronized` 关键字更强大、更灵活。它基于 java.uti.concurrent.locks 包的 Lock 接口及其实现类 `ReentrantLock`。lock 机制具有以下优点：

- 构建同步代码块的方式更灵活。使用 `synchronized` 关键字只能以结构化的方式控制一个同步代码块，而 Lock 能以更复杂的方式实现临界区。
- Lock 相对 synchronized 提供了更多功能。例如，tryLock() 尝试获得锁，如果该锁因被其它线程持有而无法获得，返回 false。对 synchronized 关键字，如果线程 A 试图执行线程 B 正在执行的 synchronized 代码块，线程 A 挂起，直到线程 B 执行完。而使用 tryLock()，直接告诉你是否有其它线程正在运行受该锁保护的代码。
- `ReadWriteLock` 接口允许读写操作分离，多个 readers 一个 writer。
- Lock 接口性能比 synchronized 关键字更好。

`ReentrantLock` 的构造函数有一个 boolean 类型的 fair 参数，默认为 false，即 non-fair  模式。如果有多个线程在等待该锁，该锁需要选择其中一个访问临界区：

- 在 non-fair 模式，该锁随机选择一个线程
- 在 fair 模式，该锁选择等待时间最长的线程

## 1.2. 示例

演示使用 lock 同步代码块，使用 Lock 接口及其实现 ReentrantLock 类创建临界区，实现一个模拟打印队列的程序。

- 打印队列实现

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 模拟打印队列
public class PrintQueue {
	// lock 对象，控制队列的访问
    private Lock queueLock;

    public PrintQueue(boolean fairMode) {
        queueLock = new ReentrantLock(fairMode);
    }

    /**
     * 打印 job
     * 打印分为两个阶段，演示 fair 属性如何影响锁对线程的选择
     * @param document 待打印文档
     */
    public void printJob(Object document) {
        queueLock.lock(); // 获取锁

		// 模拟打印的过程
        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", 
		            Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock(); // 最后释放锁
        }

		// 重复打印过程，便于演示 fair 参数的作用
        queueLock.lock();
        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", 
		            Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }
}
```

- 打印 Job 实现

模拟将文档发给到队列打印的任务。

```java
public class Job implements Runnable {

    // 打印队列
    private PrintQueue printQueue;
    
    public Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    // 发送文档到打印队列，并等待打印结束
    @Override
    public void run() {
        System.out.printf("%s: Going to print a document\n",
                Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("%s: The document has been printed\n",
                Thread.currentThread().getName());
    }
}
```

- main 实现

```java
import java.util.concurrent.TimeUnit;

public class Main {

    // 运行 10 个打印任务
    public static void main(String args[]) {
		// 测试 fair=false 
        System.out.printf("Running example with fair-mode = false\n");
        testPrintQueue(false);
        // 测试 fair=true
        System.out.printf("Running example with fair-mode = true\n");
        testPrintQueue(true);
    }

    private static void testPrintQueue(boolean fairMode) {
        // 创建队列
        PrintQueue printQueue = new PrintQueue(fairMode);

        // 创建 10 个线程
        Thread[] thread = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        // 启动线程
        for (int i = 0; i < 10; i++) {
            thread[i].start();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 等待所有线程结束
        for (int i = 0; i < 10; i++) {
            try {
                thread[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

```

```

这个示例的关键是 `PrintQueue` 的 `printJob()` 方法。使用 `ReentrantLock` 创建 lock 来实现临界区。使用 lock() 方法获得锁，临界区开始。当线程 A 调用 lock()：

- 如果没有其它线程持有锁，lock() 立即返回，并给线程 A 控制权
- 如果此时另一个线程 B 正持有锁在临界区运行，那么 lock() 让线程 B 进入 sleep 状态，直到线程 B 在临界区执行完毕

在临界区结束时，调用 unlock() 释放锁，从而允许其它线程进入临界区。

如果在临界区结束时不调用 unlock()，那其它等待的线程会持续挂起，导致死锁。

如果在临界区使用 try-catch 语句，那么一定要在 finally 中包含 unlock() 方法。

上面还对 fair 进行了测试。每个打印任务有两个临界区，在执行完第一个临界区后，在 non-fair 模式，锁会随机挑选一个线程持有锁，因此整个任务的顺序比较乱，不过基本是按线程启动的顺序执行：

```
Running example with fair-mode = false
Thread 0: Going to print a document
Thread 0: PrintQueue: Printing a Job during 3 seconds
Thread 1: Going to print a document
Thread 2: Going to print a document
Thread 3: Going to print a document
Thread 4: Going to print a document
Thread 5: Going to print a document
Thread 6: Going to print a document
Thread 7: Going to print a document
Thread 8: Going to print a document
Thread 9: Going to print a document
Thread 0: PrintQueue: Printing a Job during 8 seconds
Thread 0: The document has been printed
Thread 1: PrintQueue: Printing a Job during 3 seconds
Thread 1: PrintQueue: Printing a Job during 5 seconds
Thread 2: PrintQueue: Printing a Job during 4 seconds
Thread 1: The document has been printed
Thread 2: PrintQueue: Printing a Job during 0 seconds
Thread 2: The document has been printed
Thread 3: PrintQueue: Printing a Job during 1 seconds
Thread 3: PrintQueue: Printing a Job during 0 seconds
Thread 3: The document has been printed
Thread 4: PrintQueue: Printing a Job during 3 seconds
Thread 4: PrintQueue: Printing a Job during 5 seconds
Thread 4: The document has been printed
Thread 5: PrintQueue: Printing a Job during 1 seconds
Thread 5: PrintQueue: Printing a Job during 5 seconds
Thread 5: The document has been printed
Thread 6: PrintQueue: Printing a Job during 6 seconds
Thread 6: PrintQueue: Printing a Job during 1 seconds
Thread 6: The document has been printed
Thread 7: PrintQueue: Printing a Job during 6 seconds
Thread 7: PrintQueue: Printing a Job during 6 seconds
Thread 7: The document has been printed
Thread 8: PrintQueue: Printing a Job during 7 seconds
Thread 8: PrintQueue: Printing a Job during 3 seconds
Thread 8: The document has been printed
Thread 9: PrintQueue: Printing a Job during 1 seconds
Thread 9: PrintQueue: Printing a Job during 5 seconds
Thread 9: The document has been printed
```

当使用 fair 模式，请求锁的顺序分别为 Thread 0, Thread 1，以此类推。当 Thread 0 执行完第一个临界区，释放锁，Thread 0 立刻又请求锁，但在 fair 模式，Lock 会选择等待时间最长的线程，因此选择 Thread 1，以此类推。直到所有线程执行完第一个临界区，然后轮流执行第二个临界区，输出如下：

```
Running example with fair-mode = true
Thread 0: Going to print a document
Thread 0: PrintQueue: Printing a Job during 9 seconds
Thread 1: Going to print a document
Thread 2: Going to print a document
Thread 3: Going to print a document
Thread 4: Going to print a document
Thread 5: Going to print a document
Thread 6: Going to print a document
Thread 7: Going to print a document
Thread 8: Going to print a document
Thread 9: Going to print a document
Thread 1: PrintQueue: Printing a Job during 7 seconds
Thread 2: PrintQueue: Printing a Job during 7 seconds
Thread 3: PrintQueue: Printing a Job during 1 seconds
Thread 4: PrintQueue: Printing a Job during 7 seconds
Thread 5: PrintQueue: Printing a Job during 2 seconds
Thread 6: PrintQueue: Printing a Job during 8 seconds
Thread 7: PrintQueue: Printing a Job during 4 seconds
Thread 8: PrintQueue: Printing a Job during 4 seconds
Thread 9: PrintQueue: Printing a Job during 2 seconds
Thread 0: PrintQueue: Printing a Job during 5 seconds
Thread 0: The document has been printed
Thread 1: PrintQueue: Printing a Job during 1 seconds
Thread 1: The document has been printed
Thread 2: PrintQueue: Printing a Job during 8 seconds
Thread 2: The document has been printed
Thread 3: PrintQueue: Printing a Job during 7 seconds
Thread 3: The document has been printed
Thread 4: PrintQueue: Printing a Job during 5 seconds
Thread 4: The document has been printed
Thread 5: PrintQueue: Printing a Job during 9 seconds
Thread 5: The document has been printed
Thread 6: PrintQueue: Printing a Job during 6 seconds
Thread 6: The document has been printed
Thread 7: PrintQueue: Printing a Job during 3 seconds
Thread 8: PrintQueue: Printing a Job during 7 seconds
Thread 7: The document has been printed
Thread 8: The document has been printed
Thread 9: PrintQueue: Printing a Job during 3 seconds
Thread 9: The document has been printed
```

## 1.3. 总结

`Lock` 接口包含一个 `tryLock()` 方法，它与 `lock()` 主要区别在于，如果线程使用 tryLock() 无法获得 lock 权限，该方法立即返回，不会让线程挂起。如果线程获得 lock 权限，tryLock() 返回 true，否则返回 false。还可以设置等待时间，时间一到，如果没有获得 lock 权限，直接返回 false。

ReentrantLock 允许递归调用。当线程获得 lock 权限并进行递归调用，它会继续持有锁，即递归调用中 lock() 会立刻返回并继续执行。

```ad-warning
**避免死锁**
当多个线程等待一个永远无法开发的锁时，它们一直挂起，就发生死锁。例如，线程 A 持有锁 X、线程 B 持有锁 Y。现在线程 A 试图持有 Y，线程 B 试图持有 X，就陷入死锁。
```
