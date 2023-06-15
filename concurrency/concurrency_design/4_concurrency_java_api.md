# Java 并发 API 概览

2023-06-13
***
## 简介

Java 包含丰富的并发 API，包括管理并发基本元素的 `Thread`, `Lock` , `Semaphore` 类，实现高级同步机制的 executor 框架以及新的 `Stream` API 等。

## 基本并发类

Java 并发 API 的基本类有：

- `Thread` 类：表示执行 Java 并发程序的所有线程
- `Runnable` 接口：创建并发应用的另一种方式
- `ThreadLocal` 类：用于保存线程的本地变量
- `ThreadFactory` 接口：这是工程设计模式的接口，用于创建自定义线程

## 同步机制

Java 并发 API 包含多种同步机制，用于实现：

- 访问共享资源的临界区
- 在一个 common point 同步不同任务

下面是 Java API 包含的一些同步机制：

- `synchronized` 关键字：`synchronized` 关键字可以将代码块或整个方法定义为临界区。
- `Lock` 接口：`Lock` 提供的同步操作比 `synchronized` 更灵活。有不同类型的锁：和条件关联的 `ReentrantLock`；分离读写操作的 `ReentrantReadWriteLock`；以及 Java 8 引入的包含三种控制读写模式的 `StampedLock`。
- `Semaphore` 类：实现了经典的信号量同步机制。Java 支持 binary 和通用信号量。
- `CountDownLatch` 类：允许一个任务等待多个操作的结束。
- `CyclicBarrier` 类：在 common point 同步多个线程。
- `Phaser` 类：可以分阶段控制任务的执行，在所有任务到达当前阶段之前，不会有任务进入下一个阶段。

## Executor

executor 框架，将线程的创建、管理与并发任务的实现分离。你不需要担心线程的创建和管理，只需要创建任务，然后将其发送给 executor 执行。该框架包含的主要类有：

- `Executor` 和 `ExecutorService` 接口：包含所有 executors 通用的 `execute()` 方法
- `ThreadPoolExecutor`：用于获取带线程池的 executor
- `ScheduledThreadPoolExecutor`：允许延迟或定期执行任务的一种特殊 executor
- `Executors`：辅助创建 executor 的工具类
- `Callable` 接口：`Runnable` 接口的替代品，可以返回值
- `Future` 接口：可以从 `Callable` 接口获取返回值，并控制其状态

## fork/join 框架

fork/join 框架定义了一种特殊的 executor，专门为 divide and conquer 技术设计。

fork/join 专门为细粒度并行设计，它将新任务放入队列、从队列提取任务执行的开销非常低。该框架涉及的主要类和接口：

- `ForkJoinPool`：实现 executor，用于执行任务
- `ForkJoinTask`: 可以在 `ForkJoinPool` 中执行的任务
- `ForkJoinWorkerThread`：用于执行 `ForkJoinPool` 中任务的线程

## 并行 stream

Stream 和 lambda 表达式是 Java 8 最重要的两个新特性。Stream 作为一种方法被添加到 `Collection` 接口和其它集合中。

并行 stream 是一种特殊的 stream，它以并行的方式实现其操作。核心内容：

- `Stream` 接口：该接口定义了在流上执行的所有操作
- `Optional`：这是一个容器对象，可能包含 non-null 值
- `Collectors`：实现归纳操作的类，可作为 stream 操作序列的一部分
- lambda 表达式：大多数 stream 方法都接受 lambda 表达式作为参数，从而实现更紧凑的代码

## 并行数据结构

Java API 的普通数据结构，如 `ArrayList`, `Hashtable` 等，如果不使用外部同步机制，不能在并发应用中使用。如果使用外部同步机制，会增加额外的计算时间；如果不使用，可能出现数据争用。如果从多个线程修改它们，可能会出现各种异常，如 `ConcurrentModificationException` 和 `ArrayIndexOutOfBoundsException`、数据丢失，甚至可能陷入无限循环。

因此，Java 并发 API 提供了许多数据结构，可以在并发应用中安全使用。这些数据结构可以分为两类：

- 阻塞（blocking）数据结构：包含阻塞调用任务的方法，例如，数据结构为空，而你想要 get 一个值；
- 非阻塞（non-blocking）数据结构：立即执行操作，不阻塞调用方法的任务。返回 null 值或抛出异常。

下面是一些并发数据结构：

- `ConcurrentLinkedDeque`: non-blocking list
- `ConcurrentLinkedQueue`: non-blocking queue
- `LinkedBlockingDeque`:  blocking list
- `LinkedBlockingQueue`: blocking queue
- `PriorityBlockingQueue`: 优先队列的  blocking queue 实现
- `ConcurrentSkipListMap`: on-blocking navigable map
- `ConcurrentHashMap`: non-blocking hash map
- `AtomicBoolean`, `AtomicInteger`, `AtomicLong` 和 `AtomicReference`: 基本 java 数据类型的原子实现

