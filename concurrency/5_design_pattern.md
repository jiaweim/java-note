# Concurrency design patterns

## Signaling

线程信使使线程之间能互相发送信号。例如，可以让一个线程等待另一个线程的信号，如，线程B等待线程A的信号，以表示数据已准备好。

## 互斥锁(Mutex)

互斥锁是实现临界区的一种方式。在 Java 中，可以使用 `synchronized` 关键字、`ReentrantLock` 类和 `Semaphore` 类实现临界区。

## 多重锁（Multiplex）

多重锁（Multiplex）设计模式是互斥锁的泛化形式。对该锁，可以有指定数目的线程同时在临界区运行。在 Java 中实现该模式的最简单方法是使用 `Semaphore` 类。

## Initialization on demand holder idiom

在软件工程中，initialization on demand holder idiom 是一种延迟单例初始化（lazy loaded singleton）。在 Java 中，这种设计模式可以对静态字段延迟初始化，在保证并发安全的同时具有良好的性能。例如：

```java
public class Something {
    private Something() {}

    private static class LazyHolder {
        static final Something INSTANCE = new Something();
    }

    public static Something getInstance() {
        return LazyHolder.INSTANCE;
    }
}
```

该模式的实现依赖于Java语言规范（Java Language Specification, JLS）中JVM执行的初始化阶段。

当 JVM 载入 `Something`类，对其进行初始化。由于该类没有要初始化的静态变量，因此初始化很快完成。此时JVM不会初始化内部静态类 `LazyHolder`，只有在调用`Something`类的 `getInstance()` 才会执行 `LazyHolder` 类，而在第一次时 JVM 会载入和初始化 `LazyHolder` 类。`LazyHolder` 的初始化使得静态变量 `INSTANCE`初始化。由于类初始化阶段是由 JLS 保证顺序执行，没有并发，所以在 `getInstance()` 方法载入和初始化过程中中无需同步。并且在初始化阶段写入 `INSTANCE` 是一个顺序操作，后面任何并发调用 `getInstance` 总会返回同一个正确初始化的 `INSTANCE`，无需额外的同步开销。

尽管该实现是一个没有同步开销的、高效的、线程安全的单例模式实现，但是只能用在 `Something` 构造函数没有异常的情况。在大多 JVM 实现中，如果 `Something` 构造失败，随后使用相同 class-loader 初始化会导致 `NoClassDefFoundError`。
