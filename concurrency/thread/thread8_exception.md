# 线程异常处理

2023-06-15
****
## 1.1. 简介

Java 异常可以分为两类：

- 必须捕获的异常（checked exception），必须在方法内 catch 或者在方法签名 throws
- 不需要捕获的异常（unchecked exception），不需要捕获

由于线程的 `run()` 不允许 `throws` 异常，所以对 checked 异常需要在 `run()` 方法内部 `catch` 处理。而 unchecked 异常默认将 stack trace 写入控制台并退出程序。

下面介绍如何处理 unchecked 异常，以避免程序直接退出。

## 1.2. 示例

- 处理 unchecked 异常

要处理 unchecked 异常，必须实现 `UncaughtExceptionHandler` 接口，并实现其 `uncaughtException()` 方法。该接口在 Thread 类中定义。

下面创建一个 `ExceptionHandler` 类：

```java
// 处理 Thread 中的 unchecked 异常
public class ExceptionHandler implements UncaughtExceptionHandler {

    /**
     * @param t 抛出异常的 Thread
     * @param e 被抛出的异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("An exception has been captured\n");
        System.out.printf("Thread: %s\n", t.getId());
        System.out.printf("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
        System.out.printf("Stack Trace: \n");
        e.printStackTrace(System.out);
        System.out.printf("Thread status: %s\n", t.getState());
    }
}
```

- 实现抛出 unchecked 异常的类

```java
public class Task implements Runnable {

    @Override
    public void run() {
        // 抛出 NumberFormatException 为 unchecked 异常
        int number = Integer.parseInt("TTT");
        // 这一句永远不会执行
        System.out.printf("Number: %d ", number);
    }
}
```

- main 实现

```java
public class Main {
    
    public static void main(String[] args) {
        // 创建任务
        Task task = new Task();
        // 创建线程
        Thread thread = new Thread(task);
        // 设置 Uncheck 异常处理类
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();

        try {
            thread.join(); // 等待 thread 执行完
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread has finished");
    }
}
```

下面是执行结果。

```
An exception has been captured
Thread: 25
Exception: java.lang.NumberFormatException: For input string: "TTT"
Stack Trace: 
java.lang.NumberFormatException: For input string: "TTT"
	at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
	at java.base/java.lang.Integer.parseInt(Integer.java:652)
	at java.base/java.lang.Integer.parseInt(Integer.java:770)
	at java.module/mjw.study.concurrency.Task.run(Task.java:11)
	at java.base/java.lang.Thread.run(Thread.java:829)
Thread status: RUNNABLE
Thread has finished
```

抛出的异常被 `ExceptionHandler` 捕获，`ExceptionHandler` 将异常相关信息输出到控制台。

在线程中抛出 **unchecked** 异常时，JVM 会检查线程是否有设置 `uncaughtExceptionHandler`：

- 如果有，JVM 将 `Thread` 对象和抛出的 `Exception` 作为参数传递给 `ExceptionHandler`
- 如果没有，JVM 将 stack trace 输出到控制台，并结束抛出异常的线程

## 1.3. 总结

Thread 类还有一个静态方法 `setDefaultUncaughtExceptionHandler()`，该方法为应用中所有线程设置 unchecked 异常处理程序。

当在线程中抛出 unchecked 异常，JVM 会查找三种可能的 exceptionHandler：

1. 查找该线程的 exceptionHandler，同示例
2. 如果没找到，继续找 ThreadGroup 的 exceptionHandler
3. 如果依然没找到，继续找默认的 exceptionHandler，即通过静态方法设置的 exceptionHandler

如果上面三个 exceptionHandler 都没设置，JVM 将 stack trace 写入控制台，结束线程。
