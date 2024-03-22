# 中断线程

2024-03-21
添加 `Thread` 源码，以及更详细说明。

2023-06-16
@author Jiawei Mao

***
## 简介

包含多个执行线程的 Java 程序，只有在所有线程执行完毕时（确切地说，是所有非守护线程），或者其中一个线程调用 `System.exit()`，才会结束。但是，有时候需要提前结束线程，例如用户取消某个线程正在执行的任务。

Java 没有提供强制关闭线程的机制，而是提供了一种中断机制，告诉线程你想马上结束。该机制的一个特点是，线程需要检查它是否被中断，并且自主决定是否响应中断请求。即线程可以忽略中断请求继续执行。

```java
public class Thread implements Runnable {

    // 记录中断状态
    private volatile boolean interrupted;
    // 请求中断该线程
    public void interrupt() {
		...
    }
    // 当前线程的中断状态
	public boolean isInterrupted() {
        return interrupted;
    }
    // 清除当前线程的中断状态（重置为 false），并返回之前的值
    public static boolean interrupted() {
		...
    }
}
```

**阻塞方法**，如 `Thread.sleep` 和 `Object.wait` 等，都会检查线程何时中断，并且在发现中断时提前返回。它们响应中断时执行的操作包括：

- 清除中断状态
- 抛出 `InterruptedException`

JVM 不能保证阻塞方法检查到中断的速度，但一般都非常快。

**中断操作**：它不会真正中断一个正在运行的线程，而是发出中断请求，然后由线程在合适的时刻中断自己。

有些方法，如 wait, sleep 和 join 等，将严格处理这种请求，当它们收到中断请求或者开始执行时发现某个中断状态，将抛出一个异常。

## 示例

创建线程，使用中断机制使其在五秒后终止。

- 创建一个生成质数的线程

```java
public class PrimeGenerator extends Thread {

    @Override
    public void run() {
        long number = 1L;

        // 无线循环，直到被中断
        while (true) {
            // 将质数输出到控制台
            if (isPrime(number))
                System.out.printf("Number %d is Prime\n", number);

            // 每处理完一个数，检测是否有中断请求
            // 如果中断，则输出信息，结束执行
            if (isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted\n");
                return;
            }
            number++;
        }
    }

    /**
     * 判断是否为质数
     */
    private boolean isPrime(long number) {
        if (number <= 2)
            return true;
        for (long i = 2; i < number; i++) {
            if ((number % i) == 0)
                return false;
        }
        return true;
    }
}
```

- main 实现

```java
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // 载入 PrimeGenerator
        Thread task = new PrimeGenerator();
        task.start();

        // 等待 5 秒
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 请求中断 PrimeGenerator
        task.interrupt();

        // 输出线程状态
        System.out.printf("Main: Status of the Thread: %s\n", task.getState());
        System.out.printf("Main: isInterrupted: %s\n", task.isInterrupted());
        System.out.printf("Main: isAlive: %s\n", task.isAlive());
    }
}
```

- 控制台部分输出

```
Number 251263 is Prime
Number 251287 is Prime
Number 251291 is Prime
Number 251297 is Prime
Main: Status of the Thread: RUNNABLE
Main: isInterrupted: true
Main: isAlive: true
Number 251323 is Prime
The Prime Generator has been Interrupted
```

`Thread` 类使用一个 `boolean` 字段存储线程是否被中断：

- 在 main 线程中调用 task 线程的 `interrupt()` 方法，提示 task 线程该结束了，同时将该属性设置为 `true`
- 在 task 线程中调用 `isInterrupted()` 方法返回该字段的值

`main()` 方法最后的三个语句在线程结束之前执行，因此线程状态为 `RUNNABLE`，`isInterrupted()` 返回 `true`，`isAlive()` 也返回 `true`。

如果在执行最后三个语句之前线程就结束了（比如在 main 中 sleep 一秒），`isInterrupted()` 和 `isAlive()` 都将返回 `false`。

```ad-info
之所以采用这种协作方法，是因为我们极少会要求线程、服务立即停止。因为立即停止可能导致共享数据结构状态的不一致；而使用协作模式，我们可以编码任务，使其清理正在执行的任务后结束。这种方式更灵活，因为任务代码本身更清楚要清理哪些内容。
```

## 总结

- 在其它线程使用 `Thread.interrupt()` 发出中断请求
- 在线程内部使用 `isInterrupted()` 检测是否有中断请求
