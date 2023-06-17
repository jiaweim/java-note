# 条件同步

2023-06-16, 15:50
****
## 1.1. 简介

producer-consumer 是并发程序设计中的一个经典问题。即有一个 buffer，一个或多个 producer 将数据写入buffer，一个或多个 consumer 从 buffer 获取数据。

由于 buffer 是共享数据结构，所以需要同步保护访问。但是这里有更多限制，比如 buffer 满的时候 producer 不能写入数据，buffer 空的时候 consumer 不能获取数据。

对这类情况，Java 在 `Object` 类中提供了 `wait()`, `notify()` 和 `notifyAll()` 方法辅助实现该功能，它们都是 `native` 方法.

`synchronized`  内调用 `wait()`，该线程进入 sleep 状态，并释放该线程持有的对象锁，从而允许其它线程执行该对象的其它 `synchronized` 代码。当其它线程在相同对象保护的代码块中调用 `notify()` 或 `notifyAll()` 唤醒 sleep 线程。

```ad-warning
执行`wait()`, `notify()` 和 `notifyAll()` 需要先获得锁，即只能在 `synchronized` 内部调用，在外部调用抛出 `IllegalMonitorStateException`
```

```ad-tip
`Thread.sleep()` 和 `Object.wait()` 都会暂停当前线程。对 CPU 来说，不管以哪种方式暂停线程，都表示它暂时不需要 CPU 时间，OS 会将 CPU 时间分配给其它线程。区别是，调用 `wait()` 后，需要其它线程调用 `notify()`/`notifyAll()` 才能重新获得 CPU 时间。
```

## 1.2. 示例

使用 `synchronized`, `wait()`, `notify()` 和 `notifyAll()` 实现 producer-consumer 问题。

- `EventStorage` 对象

producer 向其中存储事件，consumer 取事件。为 producer-consumer 问题中的 buffer 实现。

```java
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class EventStorage {

    // buffer 最大尺寸
    private int maxSize;

    // 保存 events
    private Queue<Date> storage;

    public EventStorage() {
        maxSize = 10;
        storage = new LinkedList<>();
    }

    // 创建并保存 event
    public synchronized void set() {
	    // 检查是否满了
        while (storage.size() == maxSize) {
            try {
                wait(); // 如果满了，就等等
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.add(new Date());
        System.out.printf("Set: %d\n", storage.size());
        notify(); // 唤醒在该对象 wait 的所有线程
    }

    // 删除第一个 event
    public synchronized void get() {
	    // 检查是否空了
        while (storage.size() == 0) {
            try {
                wait(); // 空了就等等
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String element = storage.poll().toString();
        System.out.printf("Get: %d: %s\n", storage.size(), element);
        notify(); // 唤醒在该对象 wait 的所有线程
    }
}
```

- producer 实现

```java
public class Producer implements Runnable {

    private EventStorage storage;

    public Producer(EventStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
	    // 生成 100 个 events
        for (int i = 0; i < 100; i++) {
            storage.set();
        }
    }
}
```

- consumer 实现

```java
public class Consumer implements Runnable {

    private EventStorage storage;

    public Consumer(EventStorage storage) {
        this.storage = storage;
    }

    // 调用 100 次 get()
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.get();
        }
    }
}
```

- main 实现

```java
public class Main {

    public static void main(String[] args) {

        // 创建 buffer
        EventStorage storage = new EventStorage();

        // 创建 producer 线程
        Producer producer = new Producer(storage);
        Thread thread1 = new Thread(producer);

        // 创建 consumer 线程
        Consumer consumer = new Consumer(storage);
        Thread thread2 = new Thread(consumer);

        // Starts the thread
        thread2.start();
        thread1.start();
    }
}
```

该示例的关键是 `EventStorage` 的 `set()` 和 `get()` 方法。

首先 `set()` 方法检查 storage 是否满了，如果满了，它调用 `wait()` 方法挂起。当另一个线程调用 `notify()` 时，该线程被唤醒并再次检查条件。这个过程会不断重复，直到 storage 有空闲空间。

`get()` 方法的行为类似。首先，它检查 storage 是否为空，如果时空的，它调用 `wait()` 挂起。当另一个线程调用 `notify()` 把它唤醒，它再次检查条件，直到 storage 有事件。

