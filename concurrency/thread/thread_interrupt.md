# 中断线程

2023-06-14
***
## 简介

包含多个执行线程的 Java 程序，只有在所有线程执行完毕时（确切地说，是所有非守护线程），或者其中一个线程使用了 `System.exit()`，才会结束。但是，有时候你想提前结束线程，例如用户取消某个线程正在执行的任务。

Java 提供了一种中断机制，告诉线程你想马上结束。该机制的一个特点是，线程需要检查它是否被终端，并且可以决定是否响应终端请求。即线程可以忽略中断请求继续执行。

```ad-note
静态的 `Thread.interrupted` 方法检查线程的打断状态后，将打断状态稍微 `false`；非静态的 `isInterrupted()` 方法则不改变 interrupt 状态标签。
```

## 示例

创建线程，使用中断机制使其在五秒后终止。

- 创建一个生成质数的线程

```java
public class PrimeGenerator extends Thread {

    @Override
    public void run() {
        long number = 1L;

        // 无效循环，直到被中断
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

`Thread` 类使用一个 `boolean` 字段存储线程是否被中断。当调用线程的 `interrupt()` 方法，将该属性设置为 `true`。`isInterrupted()` 方法返回该字段的值。

`main()` 方法最后的三个语句在线程结束之前执行，因此线程状态为 `RUNNABLE`，`isInterrupted()` 返回 `true`，`isAlive()` 也返回 `true`。

如果在执行最后三个语句之前线程就结束了（比如在 main 中 sleep 一秒），`isInterrupted()` 和 `isAlive()` 都将返回 `false`。

## 总结

- 在其它线程使用 `Thread.interrupt()` 发出中断请求
- 在线程内部使用 `isInterrupted()` 检测是否有中断请求
