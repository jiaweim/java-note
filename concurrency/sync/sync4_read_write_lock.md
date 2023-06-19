# 读写锁

2023-06-17, 09:55
****
## 1.1. 简介

`ReadWriteLock` 及其实现 `ReentrantReadWriteLock` 是可谓是最重要的改进。该类有两个锁：一个用于读，一个用于写。可以有多个线程同时使用读操作，但只能有一个线程使用写操作。如果一个线程正在做写操作，其它线程不能读或写。

`ReadWriteLock` 主要声明了两个方法：

- `Lock readLock()`：返回读锁
- `Lock writeLock()`：返回写锁

## 1.2. ReentrantReadWriteLock

ReentrantReadWriteLock 实现了 ReadWriteLock 接口，是一个可重入的读写锁。

ReentrantReadWriteLock 提供了两个构造函数：

- `ReentrantReadWriteLock()`：创建 ReentrantReadWriteLock，等价于 `ReentrantReadWriteLock(false)`
- `ReentrantReadWriteLock(boolean fair)`：指定公平策略

ReentrantReadWriteLock 方法：

- ReentrantReadWriteLock.ReadLock readLock()：返回读锁
- ReentrantReadWriteLock.WriteLock writeLock()：返回写锁
- `int getReadHoldCount()`：调用线程持有的可重入 read 数，当调用线程不持有 read 锁，该值为 0。
- `int getWriteHoldCount()`：调用线程持有的可重入 write 数。

## 1.3. 示例

演示 `ReadWriteLock` 的使用。

- `PricesInfo` 保存两个商品的价格

```java
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// 该类保存两个商品价格。writer 写入值，reader 读取值
public class PricesInfo {

    private double price1;
    private double price2;

    // 控制访问 prices 的锁
    private ReadWriteLock lock;

    public PricesInfo() {
        price1 = 1.0;
        price2 = 2.0;
        lock = new ReentrantReadWriteLock();
    }

    // 返回第一个价格
    public double getPrice1() {
	    // 多线程共享字段，临界区访问
        lock.readLock().lock();
        double value = price1;
        lock.readLock().unlock();
        return value;
    }

	// 返回第二个价格
    public double getPrice2() {
        lock.readLock().lock();
        double value = price2;
        lock.readLock().unlock();
        return value;
    }

    /**
     * 写入值
     * @param price1 The price of the first product
     * @param price2 The price of the second product
     */
    public void setPrices(double price1, double price2) {
	    // write 锁是独占的
        lock.writeLock().lock();
        System.out.printf("%s: PricesInfo: Write Lock Acquired.\n", new Date());
        try {
	        // 睡 5 秒，它依然持有锁，其它线程 read, write 锁都无法持有
            TimeUnit.SECONDS.sleep(10); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.price1 = price1;
        this.price2 = price2;
        System.out.printf("%s: PricesInfo: Write Lock Released.\n", new Date());
        lock.writeLock().unlock();
    }
}
```

- read task 实现

```java
import java.util.Date;

public class Reader implements Runnable {

    private PricesInfo pricesInfo;

    public Reader(PricesInfo pricesInfo) {
        this.pricesInfo = pricesInfo;
    }

    @Override
    public void run() {
	    // 读取 10 次价格
        for (int i = 0; i < 20; i++) {
            System.out.printf("%s: %s: Price 1: %f\n", new Date(),
                    Thread.currentThread().getName(), pricesInfo.getPrice1());
            System.out.printf("%s: %s: Price 2: %f\n", new Date(),
                    Thread.currentThread().getName(), pricesInfo.getPrice2());
        }
    }
}
```

- write task 实现

```java
import java.util.Date;

public class Writer implements Runnable {

    private PricesInfo pricesInfo;

    public Writer(PricesInfo pricesInfo) {
        this.pricesInfo = pricesInfo;
    }

    @Override
    public void run() {
	    // 设置三次
        for (int i = 0; i < 3; i++) {
            System.out.printf("%s: Writer: Attempt to modify the prices.\n", new Date());
            pricesInfo.setPrices(Math.random() * 10, Math.random() * 8);
            System.out.printf("%s: Writer: Prices have been modified.\n", new Date());
            try {
                Thread.sleep(2); // sleep 2 毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

- main 实现

```java
public class Main {

    public static void main(String[] args) {

        // Creates an object to store the prices
        PricesInfo pricesInfo = new PricesInfo();
	
        Reader[] readers = new Reader[5];
        Thread[] threadsReader = new Thread[5];

        // 创建 5 个 reader 线程
        for (int i = 0; i < 5; i++) {
            readers[i] = new Reader(pricesInfo);
            threadsReader[i] = new Thread(readers[i]);
        }
		
        // 创建一个 writer 线程
        Writer writer = new Writer(pricesInfo);
        Thread threadWriter = new Thread(writer);

        for (int i = 0; i < 5; i++) {
            threadsReader[i].start();
        }
        threadWriter.start();
    }
}
```

下面是部分输出：

```
Sat Jun 17 09:44:52 CST 2023: Thread-2: Price 1: 3.521531
Sat Jun 17 09:44:52 CST 2023: Thread-2: Price 2: 1.176598
Sat Jun 17 09:44:52 CST 2023: Writer: Attempt to modify the prices.
Sat Jun 17 09:44:52 CST 2023: PricesInfo: Write Lock Acquired.
Sat Jun 17 09:44:52 CST 2023: Thread-3: Price 2: 1.176598
Sat Jun 17 09:44:52 CST 2023: Thread-2: Price 1: 3.521531
Sat Jun 17 09:44:52 CST 2023: Thread-4: Price 2: 1.176598
Sat Jun 17 09:44:52 CST 2023: Thread-0: Price 2: 1.176598
Sat Jun 17 09:44:52 CST 2023: Thread-1: Price 1: 3.521531
Sat Jun 17 09:45:02 CST 2023: PricesInfo: Write Lock Released.
Sat Jun 17 09:45:02 CST 2023: Writer: Prices have been modified.
Sat Jun 17 09:44:52 CST 2023: Thread-3: Price 1: 3.564569
Sat Jun 17 09:44:52 CST 2023: Thread-1: Price 2: 2.916029
Sat Jun 17 09:44:52 CST 2023: Thread-0: Price 1: 3.564569
```

当 writer 线程获得锁时，reader 线程无法读取数据。在 "Write Lock Acquired" 后面有 reader 的输出，但它们其实在此之前执行，只是没有在控制台输出。

## 1.4. 总结

`ReentrantReadWriteLock` 有两个锁：

- `readLock()` 返回 reader 锁
- `writeLock()` 返回 writer 锁

两个锁都实现了 `Lock` 接口，因此可以使用 `lock()`, `unlock()` 和 `tryLock()` 方法。

