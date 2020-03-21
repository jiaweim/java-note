
# Intro
Java 程序在所有的非守护线程执行结束、或者有线程调用 `System.exit()` 方法，才会结束。

即 Java 线程执行结束，会自动关闭。在实际应用中，由于创建线程的成本较高，所以一个线程往往会循环返回执行任务，在需要退出时，可以借助中断线程结束循环，从而使线程执行结束，自动关闭。

Java 没有提供任何强制线程关闭的机制，而是提供了协作模式的打断机制：在一个线程中要求另一个线程停止执行，被要求停止的线程检查自己的状态，如果发现被要求停止，它执行必要的清理工作，结束执行。例如：
```java
public class PrimeGenerator extends Thread
{
    @Override
    public void run()
    {
        long number = 1L;
        while (true) {
            if (isPrime(number))
                System.out.printf("Number %d is Prime\n", number);

            if (isInterrupted()) {
                System.out.println("The Prime Generator has been Interrupted");
                return;
            }
            number++;
        }
    }

    private boolean isPrime(long number)
    {
        if (number <= 2)
            return true;
        for (long i = 2; i < number; i++) {
            if ((number % i) == 0)
                return false;
        }
        return true;
    }

    public static void main(String[] args)
    {
        Thread thread = new PrimeGenerator();
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}
```
在 `main()` 方法中调用线程的 `interrupt()` 方法提示 `PrimeGenerator` 该结束了，在 `run()` 方法中检测到中断申请后 `isInterrupted()` 后，方法返回，停止执行。

之所以采用这种协作方法，是因为我们极少会要求任务、线程或服务立即停止，因为立刻停止可能会导致共享数据结构状态的不一致；而使用协作模式，我们可以编码任务，使其清理当前正在进行任务然后结束。这种方式更为灵活，因为任务代码本身更清楚要执行哪些清理任务。

区分软件好坏的一个重要标志是否能很好的处理失败、关闭和取消任务的情况。下面内容说明取消和打断的原理，并且编码 tasks 和 services 使它们响应取消请求。

# 取消任务
在正常结束前可以被外部代码置入“完成”状态的操作，称为可取消操作（cancellable）。许多地方需要用到可取消操作：
- 用户取消请求；
- 限时任务；
- 应用事件，比如将一个问题拆分为多个任务，当一个任务解决问题，就可以取消其它任务了；
- 错误；
- 关闭。当关闭程序或服务时，则必须对当前正在处理的工作或排队的事件进行处理。比如，可以将当前队列的任务完成后关闭，也可以无效当前执行的任务，直接关闭。

在 Java 中没有抢占式安全终止线程的方法，因此也没有抢占式安全终止任务的方法。只有协作机制。

其中一个协作机制是设置“取消请求”（cancellation requested）标识，任务（task）周期性的检查该标识，如果发现该标识，任务提前结束。

# 捕获 `InterruptedException`
通过捕获 `InterruptedException` 异常执行中断：
```java
public class FileSearch implements Runnable
{
    private String initPath;
    private String fileName;

    public FileSearch(String initPath, String fileName)
    {
        this.initPath = initPath;
        this.fileName = fileName;
    }

    public static void main(String[] args)
    {
        FileSearch search = new FileSearch("C:\\", "unimod.xml");
        Thread thread = new Thread(search);
        thread.start();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }

    @Override
    public void run()
    {
        File file = new File(initPath);
        if (file.isDirectory()) {
            try {
                directoryProcess(file);
            } catch (InterruptedException e) {
                System.out.printf("%s: The search has been interrupted", Thread.currentThread().getName());
            }
        }
    }

    private void directoryProcess(File file) throws InterruptedException
    {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    directoryProcess(file1);
                } else {
                    fileProcess(file1);
                }
            }
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * 比较当前文件的文件名和要查找的文件名，如果匹配，将信息打印出来。
     * 做完比较厚，检查线程是否被中断了，如果是，抛出异常。
     */
    private void fileProcess(File file) throws InterruptedException
    {
        if (file.getName().equals(fileName)) {
            System.out.printf("%s : %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}
```

在 `run()` 方法中捕获了异常，捕获异常后，这里没有中断线程执行，而是输出捕获信息：
```java
try {
    directoryProcess(file);
} catch (InterruptedException e) {
    System.out.printf("%s: The search has been interrupted", Thread.currentThread().getName());
}
```

## 非抛出异常
扩展自 `RuntimeException` 的异常不会抛出，在线程中要处理这些异常，可以实现 `UncaughtExceptionHandler` 接口。
```java
public interface UncaughtExceptionHandler {
    /**
        * Method invoked when the given thread terminates due to the
        * given uncaught exception.
        * <p>Any exception thrown by this method will be ignored by the
        * Java Virtual Machine.
        * @param t the thread
        * @param e the exception
        */
    void uncaughtException(Thread t, Throwable e);
}
```
例如：
```java
public class ExceptionHandler implements Thread.UncaughtExceptionHandler
{
    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        System.out.println("An exception has been captured");
        System.out.printf("Thread: %s\n", t.getId());
        System.out.printf("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
        System.out.println("Stack Trace: ");
        e.printStackTrace(System.out);
        System.out.printf("Thread status: %s\n", t.getState());
    }
}
```
测试：
```java
public static void main(String[] args)
{
    Runnable task = () -> {
        Integer.parseInt("ASV");
    };

    Thread thread = new Thread(task);
    thread.setUncaughtExceptionHandler(new ExceptionHandler());
    thread.start();
}
```

除了 `setUncaughtExceptionHandler` 方法，`Thread` 的静态方法 `setDefaultUncaughtExceptionHandler` 方法为所有线程设置非捕获异常的处理方法。

当出现非捕获异常，JVM 按以下顺序处理：
- 首先查找当前线程的 `UncaughtExceptionHandler`，如果没找到；
- 继续在 Thread 归属的 `ThreadGroup` 中查找，如果依然没找到；
- JVM 使用默认的 `UncaughtExceptionHandler`；
- 如果默认的也没有，JVM输出信息到控制台并退出程序。

# ThreadLocal
`ThreadLocal` 变量是为了实现