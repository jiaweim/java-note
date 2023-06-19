# 守护线程

2023-06-14
****
## 1.1. 简介

Java 提供了两种类型的线程：

- 用户线程（user）
- 守护线程（daemon）

用户线程具有较高的优先级，守护线程优先级低，一般为用户线程服务。守护线程特征：

- 守护线程一般用于处理后台工作，为其它线程提供服务，比如 JDK 的垃圾回收线程
- 守护线程优先级很低，在相同程序中，只要有其它非守护线程在执行，守护线程不执行
- 由于守护线程是服务线程，只在用户线程运行时才需要，因此**守护线程不阻止 JVM 退出**，在 JVM 中运行的线程只剩下守护线程时，程序自动退出
- 守护线程一般包含无线循环，等待服务请求或者执行线程任务。
- 守护线程**不能用于执行重要的任务**，不能用于访问固定资源，如文件、数据库，因为它随时可能中断。

不过总有意外，一些设计不好的守护线程可能会阻止 JVM 退出。比如在运行的守护线程中调用 `Thread.join()` 可能会阻止 JVM 关闭。

## 1.2. 示例

下面创建两类线程，一类 user 线程，负责将事件写入 queue，一个 daemon 线程，负责清理 queue，删除 10 秒前生成的事件。

-  Event 类

仅用于保存信息，包含事件的日期和信息。

```java
import java.util.Date;

// 保存 event 信息
public class Event {

    // Date of the event
    private Date date;
    
    // Message of the event
    private String event;

     // 返回 Date
    public Date getDate() {
        return date;
    }

    // 设置 Date
    public void setDate(Date date) {
        this.date = date;
    }

    // 返回信息
    public String getEvent() {
        return event;
    }

    // 设置信息
    public void setEvent(String event) {
        this.event = event;
    }
}
```

- WriterTask，负责生成事件

```java
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

 // 每秒生成一个 event
public class WriterTask implements Runnable {

    // 存储生成的 events
    Deque<Event> deque;

    public WriterTask(Deque<Event> deque) {
        this.deque = deque;
    }

    @Override
    public void run() {

        // 输出 100 个 events
        for (int i = 1; i < 100; i++) {
            // 创建并初始化 event 对象
            Event event = new Event();
            event.setDate(new Date());
            event.setEvent(String.format("The thread %s has generated the event %d event",
                    Thread.currentThread().getId(), i));

            // 添加到 queue
            deque.addFirst(event);
            try {
                // sleep 一秒
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

- CleanerTask，负责清理事件

```java
import java.util.Date;
import java.util.Deque;

public class CleanerTask extends Thread {

    // 保存 events
    private Deque<Event> deque;

    public CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        // 将其设置为 daemon 线程
        setDaemon(true);
    }

    @Override
    public void run() {
        // daemon 线程常包含无限循环，获取当前日期，然后清理 deque
        while (true) {
            Date date = new Date();
            clean(date);
        }
    }

    // 查看 deque 中的事件，删除 10 秒前的 events
    private void clean(Date date) {
        long difference;
        boolean delete;
        if (deque.size() == 0) {
            return;
        }

        delete = false;
        do {
	        // 获取最后一个 event
            Event e = deque.getLast();
            difference = date.getTime() - e.getDate().getTime();
            if (difference > 10000) { // 与当前时间相差 10 秒以上
                System.out.printf("Cleaner: %s\n", e.getEvent());
                deque.removeLast();
                delete = true;
            }
        } while (difference > 10000);

        if (delete) {
            System.out.printf("Cleaner: Size of the queue: %d\n", deque.size());
        }
    }
}
```

- main 实现

```java  
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Main {
    public static void main(String[] args) {

        // 创建 event 数据结构，使用 ConcurrentLinkedDeque 保证多线程安全
        Deque<Event> deque = new ConcurrentLinkedDeque<>();

        // 有多少处理器，就创建多少 WriterTask 线程
        WriterTask writer = new WriterTask(deque);
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            Thread thread = new Thread(writer);
            thread.start();
        }

        // 创建一个 CleanerTask 并启动
        CleanerTask cleaner = new CleanerTask(deque);
        cleaner.start();
    }
}
```

有多少处理器，就生成多少个 `WriterTask`，每个 `WriterTask` 每秒生成一个事件，各生成 100 个。

所有 `WriterTask` 和 `CleanerTask` 通过 `ConcurrentLinkedDeque` 共享事件列表。

假设你的电脑有 4 个处理器，10 秒生成 40 个事件。在这 10 秒内，4 个 WriterTask sleep 时 CleanerTask 执行，但是由于时间没有超过 10 秒，所以不会删除任何 event。10 秒之后，CleanerTask 每秒删除 4 个 events，WriterTask 写入另外 4 个 events；因此总的 events 大约增加到 36 就不变了。

```
Cleaner: The thread 27 has generated the event 1 event
Cleaner: The thread 26 has generated the event 1 event
Cleaner: The thread 25 has generated the event 1 event
Cleaner: The thread 28 has generated the event 1 event
Cleaner: Size of the queue: 36
Cleaner: The thread 25 has generated the event 2 event
Cleaner: The thread 26 has generated the event 2 event
Cleaner: The thread 28 has generated the event 2 event
Cleaner: The thread 27 has generated the event 2 event
Cleaner: Size of the queue: 36
Cleaner: The thread 28 has generated the event 3 event
Cleaner: The thread 25 has generated the event 3 event
Cleaner: The thread 26 has generated the event 3 event
Cleaner: The thread 27 has generated the event 3 event
Cleaner: Size of the queue: 36
...
Cleaner: The thread 26 has generated the event 90 event
Cleaner: Size of the queue: 39
Cleaner: The thread 25 has generated the event 90 event
Cleaner: Size of the queue: 38
Cleaner: The thread 27 has generated the event 90 event
Cleaner: Size of the queue: 37
Cleaner: The thread 28 has generated the event 90 event
Cleaner: Size of the queue: 36
```

## 1.3. 总结

- 通过 `Thread.setDaemon()` 设置守护线程
- 通过 `thread.isDaemon()` 检查是否为守护线程
- `setDaemon()` 只能在还没调用 `start()` 前调用，否则抛出异常
