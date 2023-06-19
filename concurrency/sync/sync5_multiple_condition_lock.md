# 多条件锁

2023-06-17, 12:12
****
## 1.1. 简介

锁可以与一个或多个条件关联，这些条件通过 `Condition` 接口声明。`Condition` 在线程获取锁之前检查是否符合条件，如果为 `false`，线程被挂起，直到被另一个线程唤醒。

可以认为 `Lock` 是 `synchronized` 的加强版，而 `Condition` 是 wait/notify 的加强版。

`Condition` 与 `Lock` 绑定，通过 `Lock` 的 `newCondition()` 获得 `Condition` 实例。

`Condition` 声明了如下方法：

- `void await()`：调用线程 wait，直到被中断、或被其它线程在相同 condition 下调用`signal()` 或 `signalAll()` 唤醒
- `boolean await(long time, TimeUnit unit)`：调用线程 wait，直到被中断、其它线程在相同 condition 下调用 `signal()` 或 `signalAll()` 唤醒、或时间到了
- `long awaitNanos(long nanosTimeout)`：同上
- `void awaitUninterruptibly()`：调用线程 wait，直到被其它线程调用 `signal()` 或 `signalAll()` 唤醒，wait 状态不会被中断。
- `awaitUntil(Date date)`：调用线程 wait，直到被中断、其它线程在相同 condition 下调用 signal() 或 signalAll() 唤醒、或时间到了。
- `void signal()`：唤醒一个 wait 线程
- `void signalAll()`：唤醒所有 wait 线程

## 1.2. 示例

使用 Lock 和 Condition 实现典型的 producer-consumer 问题。

- FileMock 实现

模拟文本文件。


```java
public class FileMock {

    // 文件内容
    private String[] content;
    // 正在处理的 line
    private int index;

    /**
     * 生成随机数据。
     *
     * @param size:   文本行数
     * @param length: 每行的文本长度
     */
    public FileMock(int size, int length) {
        content = new String[size];
        for (int i = 0; i < size; i++) {
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                int randomCharacter = (int) (Math.random() * 255);
                buffer.append((char) randomCharacter);
            }
            content[i] = buffer.toString();
        }
        index = 0;
    }

    // 检查文件中是否还有更多内容，达到文件末尾返回 false
    public boolean hasMoreLines() {
        return index < content.length;
    }

    // 返回 index 处的文本，然后 index++
    public String getLine() {
        if (this.hasMoreLines()) {
            System.out.println("Mock: " + (content.length - index));
            return content[index++];
        }
        return null;
    }
}
```

- buffer 实现

```java
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {

    // buffer，存储共享数据
    private final LinkedList<String> buffer;

    // buffer 大小
    private final int maxSize;

    // Lock：控制对 buffer 的访问
    private final ReentrantLock lock;

    // 重点：设置访问条件
    private final Condition lines;
    private final Condition space;

    // writer 是否会继续写入
    private boolean pendingLines;

    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        lines = lock.newCondition();
        space = lock.newCondition();
        pendingLines = true;
    }

    // 插入一行到 buffer
    public void insert(String line) {
	    // 获取锁
        lock.lock();
        try {
	        // 如果 buffer 满了，space 条件等待，其它线程在 space 条件
	        // 调用 signal() 或 signalAll() 唤醒
            while (buffer.size() == maxSize) {
                space.await();
            }
            // 被唤醒或 buffer 有空闲，插入内容
            buffer.offer(line);
            System.out.printf("%s: Inserted Line: %d\n", Thread.currentThread().getName(), buffer.size());
            lines.signalAll(); // 唤醒 lines 条件下的线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock(); // finally 中解锁
        }
    }

    // 返回 buffer 的第一行内容
    public String get() {
        String line = null;
        lock.lock(); // 获取锁
        try {
	        // 检查 buffer 是否为空
            while ((buffer.size() == 0) && (hasPendingLines())) {
                lines.await(); // 如果是空的，在 lines 条件下挂起
            }

			// 如果其它线程调用 lines 条件的 signal() 或 signalAll()
			// 线程被唤醒
            if (hasPendingLines()) {
                line = buffer.poll(); // 获取第一行
                System.out.printf("%s: Line Readed: %d\n", Thread.currentThread().getName(), buffer.size());
                space.signalAll(); // 唤醒 space 条件下的线程
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock(); // finally 中解锁
        }
        return line;
    }

    // 该方法由 producer 调用，当不继续生成内容时调用
    public synchronized void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }

    // 当还有 lines，返回 true
    public synchronized boolean hasPendingLines() {
        return pendingLines || buffer.size() > 0;
    }
}
```

- producer 实现

```java
// 从 FileMock 提取内容，保存到 buffer
public class Producer implements Runnable {
    // 模拟的文件
    private FileMock mock;

    // buffer
    private Buffer buffer;

    public Producer(FileMock mock, Buffer buffer) {
        this.mock = mock;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        buffer.setPendingLines(true);
        while (mock.hasMoreLines()) {
            String line = mock.getLine();
            buffer.insert(line);
        }
        buffer.setPendingLines(false);
    }
}
```

- consumer 实现

```java
import java.util.Random;

// 从 buffer 读取 line
public class Consumer implements Runnable {

    private Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
	    // 当 buffer 不为空，读取内容
        while (buffer.hasPendingLines()) {
            String line = buffer.get();
            processLine(line);
        }
    }

    // 睡眠一下模拟处理文本
    private void processLine(String line) {
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

- main 实现

```java
public class Main {
    public static void main(String[] args) {
        // 101 行文本，每行 10 个字符
        FileMock mock = new FileMock(101, 10);

        // buffer，最多包含 20 行
        Buffer buffer = new Buffer(20);

        // 创建 1 个 producer 线程
        Producer producer = new Producer(mock, buffer);
        Thread producerThread = new Thread(producer, "Producer");

        // 创建 3 个 consumer 线程
        Consumer consumers[] = new Consumer[3];
        Thread consumersThreads[] = new Thread[3];

        for (int i = 0; i < 3; i++) {
            consumers[i] = new Consumer(buffer);
            consumersThreads[i] = new Thread(consumers[i], "Consumer " + i);
        }

        // 启动
        producerThread.start();
        for (int i = 0; i < 3; i++) {
            consumersThreads[i].start();
        }
    }
}
```

`Condition` 对象与 `Lock` 绑定，通过 `Lock`` `的 `newCondition()` 创建。因此要操作 `Condition`，首先要获得与之关联的 `Lock`。

当一个线程调用 `Condition` 的 `await()` 方法，它会自动释放锁，这样其它线程就能获取该锁，执行与该锁绑定的临界区。

```ad-warning
当一个线程调用 `Condition` 的 `signal()` 或 `signalAll()` 方法，等待该 condition 的一个或多个线程被唤醒，但并不能保证使这些挂起的条件为 true。所以必须把 `await()` 放在 `while` 循环中，在 condition 为 true 之前不能离开该循环。当条件为 `false`，继续调用 `await()`。
```

要小心使用 `await()` 和 `signal()`。如果调用了 `await()`，但是不在相同 condition 下调用 `signal()`，该线程会一直挂起。

调用 `await()` 线程挂起后，其 sleep 状态可以中断，抛出 `InterruptedException`，因此需要处理该异常。
