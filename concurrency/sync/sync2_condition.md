# 条件同步

2023-08-07, 11:59
add: monitor 原理
2023-06-16, 15:50
****
## 1. Monitor 原理

Monitor 直译为 ”监视器“，在操作系统领域一般翻译为**管程**。

**管程** 指管理共享变量及对共享变量操作的过程，以支持并发。

synchronized 关键字和 wait(), notify(), notifyAll() 这三个方法是 Java 实现管程技术的主要组件。

### 1.1. Monitor 模型

在管程的发展史上，先后出现三种不同的管程模型，分别是 Hasen 模型、Hoare 模型和 MESA 模型。现在广泛使用的时 MESA 模型。

![](Pasted%20image%2020230807104834.png)
### 1.2. wait, notify 和 notifyAll

`wait()` 的使用范式：

```java
while(条件不满足){
    wait();
}
```

- 所有等待线程拥有相同的等待条件：使用 notify
- 所有等待线程被唤醒后，执行相同操作：使用 notify
- 只需要唤醒一个线程：使用 notify
- 其它时候尽量使用 notifyAll

### 1.3. Java 内置管程

- Java 参考 MESA 模型，实现了 MESA 的精简版管程模型 synchronized
- MESA 模型中，条件变量可以有多个，Java 内置管程只有一个条件变量

![](Pasted%20image%2020230807105943.png)
Java 管程实现：

- 在 java.lang.Object 中定义了 wait(), nofity(), notifyAll() 方法
- wait(), notify(), notifyAll() 的具体实现，依赖于 ObjectMonitor 实现
- 获取锁的线程调用 wait()，进入 waitSet
- 调用 notify，线程从 waitSet 移动到 cxq 或 EntryList

### 1.4. ObjectMonitor 的主要数据结构

```java
_header       = NULL; //对象头  markOop
_count        = 0;  
_waiters      = 0,   
_recursions   = 0;   // synchronized是一个重入锁，这个变量记录锁的重入次数 
_object       = NULL;  //存储锁对象
_owner        = NULL;  // 标识拥有该monitor的线程（当前获取锁的线程） 
_WaitSet      = NULL;  // 调用wait阻塞的线程：等待线程组成的双向循环链表，_WaitSet是第一个节点
_WaitSetLock  = 0 ;    
_Responsible  = NULL ;
_succ         = NULL ;
_cxq          = NULL ; // 有线程在执行，新进入的线程会进入这个队列：多线程竞争锁会先存到这个单向链表中 （FILO栈结构：非公平！）
FreeNext      = NULL ;
_EntryList    = NULL ; //存放在进入或重新进入时被阻塞(blocked)的线程 (也是存竞争锁失败的线程)
_SpinFreq     = 0 ;
_SpinClock    = 0 ;
OwnerIsThread = 0 ;
_previous_owner_tid = 0;
```

- owner: 锁的当前持有者，owner 只能指向一个线程
- waitSet: 调用 wait 阻塞的线程
- EntryList: 被阻塞的队列

waitSet 和 EntryList 的区别：EntryList 执行条件都满足了，只需要获取锁。waitSet 条件不满足，条件满足后，重新进入 EntryList 竞争锁。

### 1.5. synchronized 的唤醒机制

![](Pasted%20image%2020230807110440.png)
- 在获取锁时，将当前线程插入到 cxq 的头部
- 在释放锁时，默认策略（QMode=0）
    - 如果 EntryList 为空，则将 cxq 的元素按原有顺序插入 EntryList，并唤醒第一个线程，即当 EntryList 为空，后来的线程先获取锁
    - 如果 EntryList 不为空，直接从 EntryList 唤醒线程

### 1.6. synchronized 的等待机制

看一段代码：

```java
package mjw.study.java.concurrency;

public class SyncQModeDemo {

    public static void main(String[] args) throws InterruptedException {

        SyncQModeDemo demo = new SyncQModeDemo();

        demo.startThreadA();
        // 控制线程执行时间
        Thread.sleep(100);
        demo.startThreadB();
        Thread.sleep(100);
        demo.startThreadC();
    }

    final Object lock = new Object();

    public void startThreadA() {
        new Thread(() -> {
            synchronized (lock) {
                System.out.println("A get lock");
                try {
                    lock.wait(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("A release lock");
            }
        }, "thread-A").start();
    }

    public void startThreadB() {
        new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("B get lock");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("B release lock");
            }
        }, "thread-B").start();
    }

    public void startThreadC() {
        new Thread(() -> {
            synchronized (lock) {
                System.out.println("C get lock");
            }
        }, "thread-C").start();
    }
}
```

```
A get lock
B get lock
B release lock
A release lock
C get lock
```

## 2. 使用

producer-consumer 是并发程序设计中的一个经典问题。即有一个 buffer，一个或多个 producer 将数据写入buffer，一个或多个 consumer 从 buffer 获取数据。

由于 buffer 是共享数据结构，所以需要同步保护访问。但是这里有更多限制，比如 buffer 满的时候 producer 不能写入数据，buffer 空的时候 consumer 不能获取数据。

对这类情况，Java 在 `java.lang.Object` 类中提供了 `wait()`, `notify()` 和 `notifyAll()` 方法辅助实现该功能，它们都是 `native` 方法。这是一个小型的线程间通信的 API：

- `void wait()`：使当前线程 wait，直到其它线程调用该对象的 notify() 或 notifyAll() 唤醒，或其它线程中断该线程。
- `void wait(long timeout)`：使当前线程 wait，直到其它线程调用该对象的 notify() 或 notifyAll() 唤醒，或者等待时间达到 timeout，或其它线程中断该线程。
- `void wait(long timeout, int nanos)`：使当前线程 wait，直到其它线程调用该对象的 notify() 或 notifyAll() 唤醒，或者等待时间达到 timeout 毫秒 + nanos 纳秒，或其它线程中断该线程。
- `void notify()`：唤醒一个在该对象 wait 的线程。如果有多个 wait 线程 ，随机唤醒一个。被唤醒的线程与其它在该对象等待锁的线程一样，即没有特权，不一定是下一个获得该对象锁的线程。
- `void notifyAll()`：唤醒在该对象 wait 的所有线程。其它同 `notify()`。

`synchronized`  内调用 `wait()`，该线程进入 sleep 状态，并释放该线程持有的对象锁，从而允许其它线程执行该对象的其它 `synchronized` 代码。当其它线程在相同对象保护的代码块中调用 `notify()` 或 `notifyAll()` 唤醒 sleep 线程。

```ad-warning
执行`wait()`, `notify()` 和 `notifyAll()` 需要先获得锁，即只能在 `synchronized` 内部调用，在外部调用抛出 `IllegalMonitorStateException`
```

```ad-tip
`Thread.sleep()` 和 `Object.wait()` 都会暂停当前线程。对 CPU 来说，不管以哪种方式暂停线程，都表示它暂时不需要 CPU 时间，OS 会将 CPU 时间分配给其它线程。区别是，调用 `wait()` 后，需要其它线程调用 `notify()`/`notifyAll()` 才能重新获得 CPU 时间。
```

## 3. 示例

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

## 4. 参考

- https://blog.csdn.net/qq_35436158/article/details/122532075