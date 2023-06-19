# 基于 Lock 的同步

2023-06-18, 14:23
****
## 1.1. 简介

lock 同步机制比 `synchronized` 更强大、更灵活。它基于 `java.uti.concurrent.locks` 包的 `Lock` 接口及其实现类 `ReentrantLock`。

lock 相对 `synchronized` 关键字具有以下优点：

- `Lock` 的同步代码块结构更灵活。`synchronized` 只能在同一个 `synchronized` 代码块中获取和释放控制，而 `Lock` 获取和释放锁不用在同一个块结构中。
- `Lock` 的功能更多。例如，`tryLock()` 尝试获得锁，如果该锁被其它线程占用，返回 false。对 `synchronized`，如果线程 A 试图执行线程 B 正在执行的 `synchronized` 代码块，线程 A 挂起，直到线程 B 执行完。而 `tryLock()` 直接告诉你是否有其它线程正在运行受该锁保护的代码。
- `ReadWriteLock` 接口允许读写操作分离，允许多个读线程一个写线程。
- `Lock` 的性能比 `synchronized` 好。

## 1.2. Lock 接口

`Lock` 接口的方法说明：

- `void lock()`：获取 lock，当 lock 不可用，调用线程挂起直到 lock 可用。
- `void lockInterruptibly()`：获取 lock，当 lock 不可用，调用线程挂起直到 lock 可用，或调用线程被中断，抛出 `java.lang.InterruptedException`。
- `Condition newCondition()`：返回一个与该 `Lock` 绑定的 `Condition` 实例。如果该 `Lock` 不支持 condition，抛出 `java.lang.UnsupportedOperationException`。
- `boolean tryLock()`：获取 lock，获取成功返回 `true`，获取失败返回 `false`。
- `boolean tryLock(long time, TimeUnit unit)`：获取 lock，获取失败，调用线程等待指定时间、或被中断抛出 `InterruptedException`。
- `void unlock()`：释放锁。

## 1.3. ReentrantLock

`ReentrantLock` 实现了 `Lock` 接口，是一种**可重入互斥锁**。该类持有一个 count 字段，当持有锁的线程调用 `lock()`, `lockUninterruptibly()` 或 `tryLock()` 再次获取锁，count 值 +1；调用 `unlock()` 时 count 值 -1。当 count 值为 0，释放锁。

```ad-note
`ReentrantLock` 提供可重入锁，即线程持有锁时，再次获取锁不会被阻塞，因此可用在递归调用中。
```

`ReentrantLock` 提供了与 `synchronized` 同样的功能，但是更灵活，更强大，性能更好。

`ReentrantLock` 提供了两个构造函数：

- `ReentrantLock()`：等价于 `ReentrantLock(false)`
- `ReentrantLock(boolean fair)`：创建指定公平策略的 `ReentrantLock`。

`fair` 默认为 `false`，即 non-fair 模式。如果有多个线程在等待该锁，该锁需要选择其中一个访问临界区：

- 在 non-fair 模式，该锁随机选择一个线程
- 在 fair 模式，该锁选择等待时间最长的线程

对 `ReentrantLock`：

- 调用线程没有占用 lock 时调用 `unlock()`，抛出 `java.lang.IllegalMonitorStateException`
- 当前线程持有 lock 时 `boolean isHeldByCurrentThread()` 返回 `true`

## 1.4. 使用范例

```java
Lock lock = new ReentrantLock();

lock.lock();
try{
	// 执行临界区操作
	// 更新共享数据

}catch (Exception e){
   e.printStackTrace();
} finally {
   lock.unlock();
}
```

## 1.5. 示例

演示使用 lock 同步代码块：使用 `Lock` 接口及其实现 `ReentrantLock` 类创建临界区，实现一个模拟打印队列的程序。

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
            queueLock.unlock(); // finally 中释放锁
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

## 1.6. 总结

`ReentrantLock` 为可重入锁，允许递归调用。当线程获得 lock 权限并进行递归调用，它会继续持有锁，即递归调用中 lock() 会立刻返回并继续执行。

```ad-warning
**避免死锁**
当多个线程等待一个永远无法开发的锁时，它们一直挂起，就发生死锁。例如，线程 A 持有锁 X、线程 B 持有锁 Y。现在线程 A 试图持有 Y，线程 B 试图持有 X，就陷入死锁。
```

`ReentrantLock` 可代替 `synchronized` 进行同步。
