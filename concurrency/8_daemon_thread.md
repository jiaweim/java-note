# 守护线程（Daemon Thread）

Java 提供了两种类型的线程：用户线程和守护线程。

用户线程具有较高的优先级，守护线程优先级低，一般为用户线程服务。

守护线程特征：

- 守护线程一般用于处理一些后台工作的线程，为其它线程提供服务，比如JDK的垃圾回收线程。
- 守护线程优先级很低，在相同程序中，只要有其它非守护线程在执行，守护线程不执行。
- 由于守护线程是服务线程，只在用户线程运行时才需要，因此守护线程不阻止JVM退出，在 JVM 中运行的线程只剩下守护线程时，程序自动退出。
- 守护线程一般有无线循环，等待服务请求或者执行线程任务。
- 守护线程不能用于执行重要的任务，不能用于访问固定资源，如文件、数据库，因为它随时可能中断。

不过总有意外，一些设计不好的守护线程可能会阻止 JVM 退出。比如在运行的守护线程中调用 `Thread.join()` 可能会阻止 JVM 关闭。

## 创建守护线程

通过 `Thread.setDaemon()` 方法设置守护线程。

通过 `thread.isDaemon()` 检查是否为守护线程。

示例：

```java
Thread thread = new Thread(() -> {
    while (true) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
});
thread.start();
assertFalse(thread.isDaemon());
```

线程会继承创建它的线程的守护线程状态。由于 main 线程是用户线程，所以在 `main()` 方法中创建的线程默认是用户线程。

下面来模拟一下守护线程，创建两个线程，一个正常向队列中添加任务，一个守护线程清除10秒钟之前的任务：
任务：

```java
public class Event{
    private Date date;
    private String event;

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public String getEvent(){
        return event;
    }

    public void setEvent(String event){
        this.event = event;
    }
}
```

添加任务线程：

```java
public class WriterTask implements Runnable{

    private Deque<Event> deque;

    public WriterTask(Deque<Event> deque){
        this.deque = deque;
    }

    @Override
    public void run(){

        for (int i = 0; i < 100; i++) {
            Event event = new Event();
            event.setDate(new Date());
            event.setEvent(String.format("The thread %s has generated an event", Thread.currentThread().getId()));
            deque.addFirst(event);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

清除超时任务的守护线程：

```java
public class CleanerTask extends Thread{

    private Deque<Event> deque;

    public CleanerTask(Deque<Event> deque){

        this.deque = deque;
        setDaemon(true);
    }

    @Override
    public void run(){
        while (true) {
            Date date = new Date();
            clean(date);
        }
    }

    private void clean(Date date)
    {
        if (deque.isEmpty())
            return;

        boolean delete = false;
        long diff;
        do {
            Event e = deque.getLast();
            diff = date.getTime() - e.getDate().getTime();
            if (diff > 10000) {
                System.out.printf("Cleaner: %s\n", e.getEvent());
                deque.removeLast();
                delete = true;
            }
        } while (diff > 10000);
        if (delete) {
            System.out.printf("Cleaner: Size of the queue: %d\n", deque.size());
        }
    }
}
```

测试代码：

```java
public static void main(String[] args)
{
    Deque<Event> deque = new ConcurrentLinkedDeque<>();
    WriterTask writerTask = new WriterTask(deque);
    for (int i = 0; i < 3; i++) {
        Thread thread = new Thread(writerTask);
        thread.start();
    }
    CleanerTask cleanerTask = new CleanerTask(deque);
    cleanerTask.start();
}
```

在执行之后，列表的大小基本维持恒定。

## 守护线程只能在启动前设置

`setDaemon()` 方法只能在还没调用 `start()` 启动前调用，否则抛出异常。例如：

```java
Thread thread = new Thread(() -> {
    long startTime = System.currentTimeMillis();
    while (true) {
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + ": New Thread is running..." + i);
            try {
                //Wait for one sec so it doesn't print too fast
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // prevent the Thread to run forever. It will finish it's execution after 2 seconds
        if (System.currentTimeMillis() - startTime > 2000) {
            Thread.currentThread().interrupt();
            break;
        }
    }
});
thread.start();
assertThrows(IllegalThreadStateException.class, () -> thread.setDaemon(true));
```
