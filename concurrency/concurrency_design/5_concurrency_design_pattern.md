# 并发设计模式

2023-06-13
***
## 简介

在软件工程中，设计模式是对常见问题的解决方案。这些解决方案已经被广泛使用，被证明是对应问题的最优解决方案。使用这些设计模式可以重新造轮子。

并发也有自己的设计模式。下面介绍最有用的并发设计模式及其在 Java 中的实现。

## Signaling

应用场景：一个任务将事件通知给另一个任务。

实现该模式的最简单方法是使用 semaphore 或互斥锁，对应 `ReentrantLock` 和 `Semaphore` 类，甚至可以使用 `Object` 类的 `wait()` 和 `notify()` 方法。

例如：

```java
public void task1() {
	section1();
	commonObject.notify();
}
public void task2() {
	commonObject.wait();
	section2();
}
```

在该情况，`section2()` 方法总是在 `section1()` 方法后面执行。

## Rendezvous

这种设计模式是 Signaling 模式的一般化。在这里，task1 等待 task2 的事件，而 task2 等待 task1 的时间。解决方案和 Signaling 类似，但是需要使用两个对象：

```java
public void task1() {
	section1_1();
	commonObject1.notify();
	commonObject2.wait();
	section1_2();
}
public void task2() {
	section2_1();
	commonObject2.notify();
	commonObject1.wait();
	section2_2();
}
```

这里，`section2_2()` 总会在 `section1_1()` 后面执行，`section1_2()` 则会在 `section2_1()` 后面执行。注意，如果将对 `wait()` 的调用放在` notify()` 前面，则出现死锁。

## Mutex

互斥锁可用来实现临界区，一次只能有一个线程执行被互斥量保护的那部分代码。在 Java 中，可以使用 `synchronized` 关键字、`ReentrantLock` 类和 `Semaphore` 类实现临界区。例如：

```java
public void task() {
	preCriticalSection();
	try {
		lockObject.lock() // 进入临界区
		criticalSection();
	} catch (Exception e) {
	} finally {
	lockObject.unlock(); // 临界区结束
	postCriticalSection();
}
```

## Multiplex

多重锁（Multiplex）模式是互斥锁的推广。在该模式下，可以同时在临界区执行的任务数量是确定的。例如，当资源有多个副本，使用该模型就很有用。在 Java 中实现该模式的最简单方法是使用 `Semaphore` 类，使用可以同时在临界区执行的任务数初始化 `Semaphore`：

```java
public void task() {
	preCriticalSection();
	semaphoreObject.acquire();
	criticalSection();
	semaphoreObject.release();
	postCriticalSection();
}
```

## Barrier

应用场景：需要在某个 common point 同步某些任务的情况。

在所有 task 到达同步点之前，没有 task 可以继续执行。`CyclicBarrier` 类实现了该模式。例如：

```java
public void task() {
	preSyncPoint();
	barrierObject.await();
	postSyncPoint();
}
```

## Double-checked locking

应用场景：获取 lock 然后检查条件。

如果条件为 false，会有获取锁的额外开销。以对象的 lazy 初始化为例。如果你有一个实现 Singleton 模式的类，如下：

```java
public class Singleton{
	private static Singleton reference;
	private static final Lock lock=new ReentrantLock();
	public static Singleton getReference() {
		try {
			lock.lock();
			if (reference==null) {
				reference=new Object();
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			lock.unlock();
		}
		return reference;
	}
}
```

一种解决方案是将 lock 放在条件内：

```java
public class Singleton{
	private Object reference;
	private Lock lock=new ReentrantLock();
	public Object getReference() {
		if (reference==null) {
			lock.lock();
			if (reference == null) {
				reference=new Object();
			}
			lock.unlock();
		}
		return reference;
	}
}
```

这个解决方案仍然存在问题。如果两个 task 同时检查条件，就会同时创建两个对象。这个问题的**最佳解决方案**不使用任何显式同步机制：

```java
public class Singleton {
	private static class LazySingleton {
		private static final Singleton INSTANCE = new Singleton();
	}
	public static Singleton getSingleton() {
		return LazySingleton.INSTANCE;
	}
}
```

该实现依赖于 Java 语言规范（Java Language Specification, JLS）中 JVM 执行的初始化阶段。

当 JVM 载入 `Singleton` 类，对其进行初始化。由于该类没有要初始化的静态变量，因此初始化很快完成。此时 JVM 不会初始化内部静态类 `LazySingleton`，只有在调用 `getSingleton()` 时才会执行 `LazySingleton` 类，第一次调用 `getSingleton()` 时 JVM 会载入和初始化 `LazySingleton` 类。`LazySingleton` 的初始化使得静态变量 `INSTANCE` 的初始化。由于类初始化阶段由 JLS 保证按顺序执行，没有并发，所以在 `getSingleton()` 载入和初始化过程中无需同步。并且在初始化阶段写入 `INSTANCE` 是一个顺序操作，后面任何并发调用 `getSingleton()` 都会返回同一个正确初始化的 `INSTANCE`，无需额外的同步开销。

尽管该实现是一个没有同步开销的、高效的、线程安全的单例模式实现，但是只能用在 `Singleton` 构造函数没有异常的情况。在大多 JVM 实现中，如果 `Singleton` 构造失败，随后使用相同 class-loader 初始化会导致 `NoClassDefFoundError`。

## Read-write lock

当使用锁保护对共享变量的访问时，一次只能有一个 task 访问该变量。但是，有些变量你可能修改很少，而会读取很多次。此时，锁的性能很差，因为读取操作可以并发进行，不会有问题。为了解决该问题，我们可以使用 read-write lock 设计模式。

read-write lock 设计模式定义了一种特殊的锁，该锁有两个内部锁：一个用于 read 操作，一个用于 write 操作：

- 如果一个 task 在执行 read 操作，另一个 task 要执行 read 操作，则可以执行
- 如果一个 task 在执行 read 操作，另一个 task 要执行 write 操作，则 write 操作被阻塞直到 read 操作完成
- 如果一个 task 在执行 write 操作，另一个 task 执行 read 或 write 都被阻塞，知道 write 操作完成

`ReentrantReadWriteLock` 类实现了该设计模式。

## Thread pool

Thread pool 设计模式试图消除为每个要执行的 task 创建线程带来的开销。Thread pool 由一组线程和要执行的任务 queue 组成。线程的数目一般固定。当一个线程执行完 task 时，它不会结束，而是在 queue 中找另一个 task。如果找到其它 task，它继续执行；如果没找到，线程会在线程池中等待，知道下一个 task 插入 queue。

Java 并发 API 中实现 `ExecutorService` 接口的类，内部均使用线程池。
