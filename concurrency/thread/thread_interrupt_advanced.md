# 响应中断请求

2023-06-14
***
## 简介

在简单程序中，在 Thread 对象中控制中断较容易。如果一个线程实现了复杂的算法，该算法被分成多个方法，或者包含递归调用，就需要使用更好的机制来控制线程的中断。Java 为此提供了 `InterruptedException`，当检测到线程中断请求，可以抛出这个异常，并在 run 方法中捕获它。

## 示例

在一个文件夹及其子文件夹中查找指定名称的文件。演示如何使用 `InterruptedException` 控制线程的中断。

- 搜索目录下文件的任务类

```java
import java.io.File;

public class FileSearch implements Runnable {

    /**
     * 初始目录
     */
    private final String initPath;
    /**
     * 待搜索的文件名
     */
    private final String fileName;

    public FileSearch(String initPath, String fileName) {
        this.initPath = initPath;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        File file = new File(initPath);
        if (file.isDirectory()) { // 检查 initPath 是否为目录
            try {
                directoryProcess(file); // 该方法可能抛出 InterruptedException 异常
            } catch (InterruptedException e) {
                System.out.printf("%s: The search has been interrupted", Thread.currentThread().getName());
                cleanResources();
            }
        }
    }

    /**
     * Method for cleaning the resources. In this case, is empty
     */
    private void cleanResources() {

    }

    /**
     * 处理目录
     *
     * @param file : 待搜索的目录
     * @throws InterruptedException : 线程被中断
     */
    private void directoryProcess(File file) throws InterruptedException {

        File[] list = file.listFiles();
        if (list != null) {
            for (File value : list) {
                if (value.isDirectory()) {
                    // 处理目录，递归
                    directoryProcess(value);
                } else {
                    // 处理文件
                    fileProcess(value);
                }
            }
        }
        // 检查中断
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * 处理文件
     *
     * @throws InterruptedException : 线程被中断
     */
    private void fileProcess(File file) throws InterruptedException {
        // 检查名称是否匹配，如果是，输出到控制台
        if (file.getName().equals(fileName)) {
            System.out.printf("%s : %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }

        // 检查当前线程是否中断
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}
```

- main 实现

```java
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * 在 Windows 的 C:\\Windows 目录搜索 explorer.exe 文件
     * 10 秒后，中断线程
     */
    public static void main(String[] args) {
        FileSearch searcher = new FileSearch("C:\\Windows", "explorer.exe");
        Thread thread = new Thread(searcher);

        // 启用线程
        thread.start();

        // 等待 10 秒
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中断线程
        thread.interrupt();
    }
}
```

```
Thread-0 : C:\Windows\explorer.exe
Thread-0 : C:\Windows\SysWOW64\explorer.exe
Thread-0: The search has been interrupted
```

可以看到，`FileSearch` 在检测到两个 explorer.exe 文件后被中断。

这里使用 `InterruptedException` 异常来控制线程的中断。在耗时方法中检测中断，发现中断请求即抛出 `InterruptedException`。在 `run()` 方法中再捕获该异常，这样不管循环或递归多少次，都能循环中断退出。

## 响应中断请求

除了主动抛出 `InterruptedException`，其它并发相关 API 也可能抛出该异常：

- 如果线程处于 sleeping （调用 `sleep()`）或 waiting （调用 `wait()`）状态，调用线程的 `interrupt()` 方法会打断其状态，抛出 `InterruptedException`
- 如果线程没有处于 sleeping 或 waiting 状态，调用 `interrupt()` 则不会抛出异常，而是将 interrupt flag 设置为 `true`。

调用 wait, sleep, join, `InterruptiableChannel` 的 IO 操作以及 `Selector` 的 `wakeup` 方法会使当前线程进入阻塞状态，而调用当前线程的 `interrupt` 方法，可以打断阻塞。

打断机制使用一个内部的打断标签记录内部的打断状态。

线程如何支持中断，取决于线程正在做什么。如果线程经常调用抛出 `InterruptedException` 的操作，一般直接从 `run()` 方法返回。

- 直接抛出异常，从 `run()`返回

```java
public class InterruptingThread implements Runnable{

    @Override
    public void run(){
        try {
            TimeUnit.SECONDS.sleep(1); // 处于 sleep 状态
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread interrupted: " + e);
        }
    }

    public static void main(String[] args){
        Thread t1 = new Thread(new InterruptingThread());
        t1.start();
        try {
            t1.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

- 可以对 `InterruptedException` 不予处理，程序继续执行

```java
public class InterruptingThread2 implements Runnable{
    @Override
    public void run(){
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            System.out.println("Exception ignored: " + e);
        }
    }

    public static void main(String[] args){
        Thread t = new Thread(new InterruptingThread2());
        t.start();
        t.interrupt();
    }
}
```

```
Exception ignored: java.lang.InterruptedException: sleep interrupted
```

- 如果线程没有处于 sleeping 或 waiting 状态，调用 `interrupt()` 不会抛出异常，而仅仅设置 interrupted flag 为 true

```java
static class NoException implements Runnable{
    @Override
    public void run(){
        for (int i = 0; i <= 10; i++) {
            System.out.println(i);
        }
    }
}

@Test
void test(){
    Thread t = new Thread(new NoException());
    t.start();
    t.interrupt();
    assertTrue(t.isInterrupted());
}
```

```
true
0
1
2
3
4
5
6
7
8
9
10
```

## 总结

- 在算法可能中断的位置检查中断请求，发现中断即抛出 `InterruptedException`
- 打断 sleep 或 wait 状态也会抛出 `InterruptedException` 异常
- 在 `run()` 方法中处理异常
	- 清理并退出，同示例
	- 抛出异常，直接退出
	- 不处理，继续执行
