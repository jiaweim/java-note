# 暂停线程

2023-06-16, 11:02
****
## 1.1. 简介

有时候需要将线程暂停一段时间。例如，程序中的某个线程每分钟检查一次传感器状态，其余时间它什么都不做（sleep 状态）。在 sleep 状态，线程不会使用 CPU 资源。sleep 之后，当操作系统调度器选择执行线程时，线程继续执行。

`Thread` 的 `sleep()` 方法实现该功能。该方法以数字为参数，表示线程暂停的毫秒数。暂停时间过后，线程继续执行。

`TimeUnit` 对 `sleep` 方法进行了封装，能指定任意单位进行休眠，所有使用 `Thread.sleep()` 的地方都可以使用 `TimeUnit.sleep` 代替。

`sleep()` 不会释放对象锁，所以不要在 `synchronized`  临界区中使用 `sleep()` ，否则可能造成其它等待获取锁的线程长时间处于等待状态。

## 1.2. 示例

使用 `sleep()` 方法，每秒输出当前日期。

- 任务类

```java
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ConsoleClock implements Runnable {

    @Override
    public void run() {
        // 循环 10 此，每次创建一个 Date 对象
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n", new Date());
            try {
                // 睡 1 秒，挂起线程
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) { // sleep 可能抛出该异常
                System.out.println("The FileClock has been interrupted.");
            }
        }
    }
}
```

- main 类

```java
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        // 创建 ConsoleClock 任务对象，并创建 Thread 执行该任务
        ConsoleClock clock = new ConsoleClock();
        Thread thread = new Thread(clock);
        thread.start();
        try {
            // 主线程暂停 5 秒
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中断 ConsoleClock 线程
        thread.interrupt();
    }
}
```

- 输出

```
Wed Jun 14 17:11:51 CST 2023
Wed Jun 14 17:11:52 CST 2023
Wed Jun 14 17:11:53 CST 2023
Wed Jun 14 17:11:54 CST 2023
Wed Jun 14 17:11:55 CST 2023
The FileClock has been interrupted.
Wed Jun 14 17:11:56 CST 2023
Wed Jun 14 17:11:57 CST 2023
Wed Jun 14 17:11:58 CST 2023
Wed Jun 14 17:11:59 CST 2023
Wed Jun 14 17:12:00 CST 2023
```

启动 `ConsoleClock` 线程后，main 线程 sleep 5 秒，然后打断 `ConsoleClock` 线程，因此输出 5 次日期后，`ConsoleClock` 显示了被打断的信息，不过 `ConsoleClock` 面对打断异常并没有结束，而是继续执行。

```ad-note
`yield()` 方法也能让线程放弃 CPU 时间。该方法告诉 JVM 该线程对象放弃 CPU 时间，但是 JVM 不保证会响应该请求。该方法通常只用于调试。
```

## 1.3. 总结

- sleep 状态不占用 CPU 资源，是让出 CPU 资源的有效方法
- 调用 `Thread.sleep()` 使线程进入 sleep 状态
- 调用 `TimeUnit` 的 `sleep()` 方法也能进入 sleep 状态，且能指定时间单位
