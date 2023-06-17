# 等待线程结束

2023-06-16, 11:50
****
## 1.1. 简介

有时需要等待指定线程执行结束（`run()` 执行完）。例如，程序需要完成初始化所需资源后才能继续执行。将初始化任务作为单独线程运行，并等待初始化线程完成后再继续运行程序的余下部分。

Thread 的 `join()` 方法提供了该功能。例如，在 `t1` 线程中调用 `t2.join()`，`t1` 会暂停执行，直到 `t2` 执行完毕。

`Thread` 提供了三个重载版本的 `join()` 方法：

```java
public final void join()
public final synchronized void join(long millis)
public final synchronized void join(long millis, int nanos)
```

`join()` 会一直等待，`join(long millis)` 只等待指定毫秒数。例如，如果 `thread1` 中有一个 `thread2.join(1000)`，那么 `thread1` 挂起，直到满足下面两个条件之一：

- `thread2` 执行完毕
- 时间超过了 1000 毫秒

当两个条件之一为 `true`，`join()` 方法返回。通过检查 `thread2` 的线程状态，可以知道是完成执行还是超时了。

## 1.2. 示例

模拟一个包含初始化步骤的程序，演示 `join()` 的功能。

- 模拟初始化任务

```java
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DataSourcesLoader implements Runnable {

    @Override
    public void run() {
		// 开始运行
        System.out.printf("Beginning data sources loading: %s\n", new Date());
        try {
            // sleep 4 秒，模拟初始化时间
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 运行结束
        System.out.printf("Data sources loading has finished: %s\n", new Date());
    }
}
```

- 另一个初始化任务

```java
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NetworkConnectionsLoader implements Runnable {

    @Override
    public void run() {
        // 开始运行
        System.out.printf("Begining network connections loading: %s\n", new Date());
        try {
            // sleep 6 秒，模拟初始化所需时间
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 运行结束
        System.out.printf("Network connections loading has finished: %s\n", new Date());
    }
}
```

- 应用实现

```java
import java.util.Date;

/**
 * 应用：创建和启动两个初始化任务，并等待它们结束
 */
public class Main {

    public static void main(String[] args) {

        // 创建 DataSourceLoader 任务对象
        DataSourcesLoader dsLoader = new DataSourcesLoader();
        Thread thread1 = new Thread(dsLoader, "DataSourceThread");

        // 创建 NetworkConnectionsLoader 任务对象
        NetworkConnectionsLoader ncLoader = new NetworkConnectionsLoader();
        Thread thread2 = new Thread(ncLoader, "NetworkConnectionLoader");

        // 启动两个初始化任务线程
        thread1.start();
        thread2.start();

        // 等待两个线程结束
        try {
            thread1.join(); // 该方法会抛出 InterruptedException 异常
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Configuration has been loaded: %s\n", new Date());
    }
}
```

- 输出

```
Begining network connections loading: Wed Jun 14 17:51:58 CST 2023
Beginning data sources loading: Wed Jun 14 17:51:58 CST 2023
Data sources loading has finished: Wed Jun 14 17:52:02 CST 2023
Network connections loading has finished: Wed Jun 14 17:52:04 CST 2023
Main: Configuration has been loaded: Wed Jun 14 17:52:04 CST 2023
```

首先 `DataSourcesLoader` 执行完，然后 `NetworkConnectionsLoader` 执行完，然后 main 线程继续执行。

## 1.3. 总结

- 线程 `t2` 中调用 `t1.join()`，`t2` 会挂起执行，等待 `t1` 线程执行完
- `join()` 和 `sleep()` 具有近似相反功能，`sleep()` 让出 CPU 时间，而 `join()` 获取 CPU 时间，让其它线程等待其执行结束。
- `join()` 和 `sleep()` 一样是可中断方法，即在其它线程执行了对当前线程的 `interrupt()` 操作，它会捕获中断信息，清除 `interrupt` 标识。
