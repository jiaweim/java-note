
# Thread 方法总结
|方法|说明|
|---|---|
|static void Thread.sleep()|暂停**当前线程**，休眠指定时间。该方法不改变锁行为|
|static void yield()|终止当前线程的执行：即使当前执行线程处于让步状态，如果有其他的可运行线程具有至少与此线程同样高的优先级，那么这些线程接下来会被调度|
|void setPriority(int newPriority)|设置线程的优先级。优先级在 `Thread.MIN_PRIORITY` 与 `Thread.MAX_PRIORITY` 之间。一般使用 `Thread.NORM_PRIORITY`  优先级|
|`static Thread currentThread()`|返回代表当前执行线程的 Thread 对象|
|void interrupt()|向线程发送中断请求。线程的中断状态被设置为 true。如果当前线程被阻塞，抛出 `InterruptedException` 异常抛出|
|`static boolean interrupted()`|检测当前线程是否中断，并清除线程的中断状态，即调用后，线程的状态不再是中断状态|
|`boolean isInterrupted()`|检测当前线程是否被中断，不改变中断状态 flag。如果线程被阻塞，该方法产生 `InterruptedException`|
|isAlive|确定当前线程是否在运行。注意：Sleep 状态也是alive的|


## wait
`wait`, `notify` 和 `nontifyAll` 都是 `Object` 的方法，而且都是 `native` 方法。它们必须在 `synchronized` 方法或代码块中调用，否则报错。

`wait` 方法使线程进入等待状态，并且会释放该线程持有的锁。直到其它线程调用该线程的 `notify()` 或 `notifyAll()` 方法唤醒该线程。





Thread.sleep 和 Object.wait 都会暂停当前线程，对于CPU来说，不管是哪种方式暂停的线程，都表示它暂时不需要CPU执行时间。OS 会将执行时间分配给其他线程。区别是，调用 `wait` 后，需要别的线程 `notify/notifyAll` 才能够重新获得CPU执行时间。


## sleep
使当前线程进入阻塞状态，暂停执行一段时间。这是让出执行时间的一个有效方法。不会释放对象的锁，所以不要让 `sleep` 方法处在 `synchronized` 方法或代码块中，否则造成其他等待获取锁的线程长时间处于等待。

`TimeUnit` 对 `sleep` 方法进行了封装，能指定任意单位进行休眠，所有使用 `Thread.sleep()` 的地方都可以使用 `TieUnit.sleep` 代替。

调用 `sleep` 方法后线程会让出CPU时间，例如：
```java
public class FileClock implements Runnable
{
    @Override
    public void run()
    {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n", new Date());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("The FileClock has been interrupted");
            }
        }
    }

    public static void main(String[] args)
    {
        FileClock clock = new FileClock();
        Thread thread = new Thread(clock);
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}
```
样例输出：
```
Fri Nov 01 14:35:11 IRKT 2019
Fri Nov 01 14:35:12 IRKT 2019
Fri Nov 01 14:35:13 IRKT 2019
Fri Nov 01 14:35:14 IRKT 2019
Fri Nov 01 14:35:15 IRKT 2019
The FileClock has been interrupted
Fri Nov 01 14:35:16 IRKT 2019
Fri Nov 01 14:35:17 IRKT 2019
Fri Nov 01 14:35:18 IRKT 2019
Fri Nov 01 14:35:19 IRKT 2019
Fri Nov 01 14:35:20 IRKT 2019
```
在 `main()` 线程睡眠的 5 秒时间，`FileClock` 执行，刚好循环五次，然后执行 `main()` 中的打断操作，`run()` 方法输出被打断信息。由于捕获异常后没有结束程序，所以 `FileClock` 继续执行。

## yield
`yield` 属于建议式方法，告诉调度器我愿意放弃当前CPU资源，如果CPU资源不紧张，该提醒被忽略。`yield` 会使线程从 RUNNING 状态切换到 RUNNABLE 状态。



## interrupt
调用wait, sleep, join, `InterruptiableChannel` 的IO操作以及 `Selector` 的 wakeup 方法会使当前线程进入阻塞状态，而调用当前线程的 `interrupt` 方法，可以打断阻塞。

上面的方法会使当前线程进入阻塞状态，当另外的线程调用该线程的 `interrupt` 方法，则会打断该阻塞状态。

## join
`join()` 方法让其它线程等待该线程结束再继续执行。

可以认为是和 `sleep` 具有相反功能的方法，`sleep` 让出CPU时间，而该方法获取CPU时间，让其它线程等待其执行结束。

例如，如果子线程需要进行大量耗时的运算，如果主线程需要用到子线程的结果，则子线程调用 `join` 方法，主线程会一直等到子线程执行完才结束。


`join` 方法有三个重载版本，不过其它两个版本都引用下面这个版本，源码如下：
```java
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```
从源码可以看出，如果线程生成了还没启动，`isAlive()` 为 `false`，调用 `join()` 方法无效。

`join` 方法和 `sleep` 方法一样，也是一个可中断的方法，即在其它线程执行了对当前线程的 `interrupt` 操作，它会捕获中断信息，清除 `interrupt` 标识。

`join` 方法使当前线程等待指定时间，从上面源码可以看出，`join` 循环调用 `wait` 方法，

