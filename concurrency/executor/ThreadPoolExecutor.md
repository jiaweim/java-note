# ThreadPoolExecutor

- [ThreadPoolExecutor](#threadpoolexecutor)
  - [简介](#简介)
  - [workQueue](#workqueue)
    - [Executors 工厂方法说明](#executors-工厂方法说明)
  - [threadFactory](#threadfactory)
  - [handler](#handler)
  - [核心方法](#核心方法)
  - [执行流程](#执行流程)
  - [回收核心线程？](#回收核心线程)

2024-03-17
@author Jiawei Mao
***

## 简介

`ThreadPoolExecutor` 类是线程池的具体实现类，该类提供了4个构造函数，其它三个构造函数在下面的构造的基础上提供了部分默认值：

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
   // ...
}
```

`Executors` 的几个工厂方法，都直接调用了该构造函数：

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService
        (new ThreadPoolExecutor(1, 1,
                                0L, TimeUnit.MILLISECONDS,
                                new LinkedBlockingQueue<Runnable>()));
}
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}
```

| 参数 | 说明  |
| -- | --- |
| `corePoolSize` | 线程池大小。在创建线程池后，线程池中默认没有线程，等任务来才创建线程执行任务，当线程池中的线程数达到 `corePoolSize`，后续任务放在缓存队列中等待执行；如果调用了 `prestartAllCoreThreads()` 或 `prestartCoreThread()`方法，则在任务到来之前就预创建线程 |
| `maximumPoolSize` | 线程池允许的最大线程数 |
| `keepAliveTime`   | 线程没有任务执行时，过多久终止空闲线程。默认情况下只有当线程池中的线程数目大于 `corePoolSize` 时 `keepAliveTime` 才起作用，即当线程池中的线程数大于 `corePoolSize`，线程空闲的时间达到 `keepAliveTime` 就被终止，直到线程池中的线程数不超过 `corePoolSize`。如果调用了 `allowCoreThreadTimeOut(boolean value)` 方法，在线程池中的线程数不大于 `corePoolSize` 时 `keepAliveTime` 也起作用 |
| `unit`  | `keepAliveTime` 的时间单位 |
| `workQueue`  | 当前线程数超过 `corePoolSize` 时，新的任务会处于等待状态，并保存在该阻塞队列中  |
| `threadFactory`   | 线程工厂，用于创建线程 |
| `handler`         | 拒绝策略，拒绝任务采取的策略 |

> `largestPoolSize` 字段用于记录线程池中曾出现的最大线程数目。

## workQueue

提交都未被执行的任务放在 `workQueue` 中，该阻塞队列一般选择以下几种类型：

- `ArrayBlockingQueue`
- `DelayQueue`
- `LinkedBlockingQueue`
- `PriorityBlockingQueue`
- `SynchronousQueue`

**ArrayBlockingQueue**
内部实现为数组，有固定大小，初始化后大小不再变化。称为**有界队列**。

若有新的任务需要执行：

- 如果线程池的实际线程数小于 `corePoolSize`，则优先创建新的线程；
- 若大于 `corePoolSize`，则将新任务加入等待队列；
- 若等待队列已满，无法加入：
  - 若当前总线程数不大于 `maximumPoolSize` ，则创建新的线程执行任务；
  - 若大于 `maximumPoolSize`，则执行拒绝策略。

**DelayQueue**
阻塞的是其内部元素，`DelayQueue` 中的元素必须实现 `java.util.concurrent.Delayed` 接口，该接口只有一个方法 `long getDelay(TimeUnit unit)`，返回值是队列元素被释放前的保留时间，返回 0 或者负值表示该元素已经到期需要被释放，此时 `DelayedQueue` 通过其 `take()` 方法释放此对象，`DelayQueue` 可应用于定时关闭连接、缓存对象，超时处理等各种场景；

**LinkedBlockingQueue**
阻塞队列的大小是可选的，如果初始化时指定一个大小，它就是有边界的，如果不指定，它就是**无边界**的。无边界其实是采用了默认大小 `Integer.MAX_VALUE`。其内部实现是一个链表。

与有界队列 `ArrayBlockingQueue` 相比，除非系统资源耗尽，否则无界队列不存在任务入队失败的情况。当有新的任务到来：

- 系统的线程数小于 `corePoolSize` 时，线程池会生成新的线程执行任务；
- 当线程数达到 `corePoolSize`，就不会继续增加线程，后续任务直接进入队列；
- 若任务创建速度远大于处理速度，无界队列会保持快速增长，直到耗尽系统内存。

**PriorityBlockingQueue**
`PriorityBlockingQueue` 是带有执行优先级的队列，是一个无边界队列。它的排序规则和 `java.util.PriorityQueue` 一样。需要注意，`PriorityBlockingQueue` 中允许插入 `null` 对象。所有插入 `PriorityBlockingQueue` 的对象必须实现 `java.lang.Comparable` 接口，队列优先级的排序规则按照对这个接口的实现来定义。

**SynchronousQueue**
`SynchronousQueue` 是一个特殊的阻塞队列，它没有容量，每个插入操作必须等待另一个线程的删除操作，每一个删除操作需要等待另一个线程的插入操作。所以，它直接将新任务提交给线程执行，如果没有空闲线程，则尝试创建新的线程；如果线程数量达到最大值，则执行拒绝策略。

如果使用 `SynchronousQueue`，通常要设置很大的 `maximumPoolSize`，否则容易执行拒绝策略。

### Executors 工厂方法说明

下面看 `Executors` 中的 `newFixedThreadPool` 定义：

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}
```

其中 `corePoolSize` 和 `maximumPoolSize` 大小一样，并且使用 `LinkedBlockingQueue` 作为任务队列。

因为对固定大小的线程池，不存在线程数量的动态变化，因此 corePoolSize 和 maximumPoolSize 相等。同时，它使用无界队列存放无法立即执行的任务，当任务提交非常频繁时，该队列可能迅速膨胀，从而耗尽系统资源。

`newSingleThreadExecutor` 返回的单线程线程池，是 `newFixedThreadPool` 的一种特殊情况，即将线程数量设为 1。

`newCachedThreadPool` 的定义如下：

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}
```

其 `corePoolSize` 为 0，而 `maximumPoolSize` 为 `Integer.MAX_VALUE`，意味没有任务时，线程池内无线程，而当任务提交时，该线程池会使用空闲的线程执行任务，若无空闲线程，则将任务加入 `SynchronousQueue`，而 `SynchronousQueue` 是一种直接提交任务的队列，它会迫使线程池增加新的线程执行任务。当任务执行完毕，由于 `corePoolSize` 为 0，因此空闲线程会在指定时间内被回收。

对 `newCachedThreadPool`，如果同时有大量任务提交，而任务的执行又不快，那么系统会开启等量的线程，这样做可能会很快耗尽系统资源。

一般使用 `LinkedBlockingQueue`，大小设置为有边界，避免内存溢出。即使用 `newFixedThreadPool`。

## threadFactory
`ThreadFactory` 是一个简单的接口：

```java
public interface ThreadFactory {
    Thread newThread(Runnable r);
}
```

用于创建线程的工厂类，`Executors`里默认的 `threadFactory` 里线程的默认命名规则为 "pool-num-thread-num".

## handler

当运行的线程数目达到 `maximumPoolSize`，并且 `workQueue` 有界且已满，这时提交新的任务通过 handler 策略处理。

handle 有以下几种取值：
| handle 策略                              | 说明                                                     |
| ---------------------------------------- | -------------------------------------------------------- |
| `ThreadPoolExecutor.AbortPolicy`         | 默认值，丢弃任务并抛出`RejectedExecutionException`异常   |
| `ThreadPoolExecutor.DiscardPolicy`       | 丢弃任务，但是不抛出异常                                 |
| `ThreadPoolExecutor.DiscardOldestPolicy` | 丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程） |
| `ThreadPoolExecutor.CallerRunsPolicy`    | 由调用线程处理该任务                                     |

还可以自定义拒绝策略，只需要实现 `RejectedExecutionHandler` 接口。

## 核心方法

| 方法                                            | 说明                                                         |
| ----------------------------------------------- | ------------------------------------------------------------ |
| `execute()`                                     | 向线程池提交任务                                             |
| `submit()`                                      | 先线程池提交任务，和 `execute()` 不同的时，它可以返回执行结果 |
| `getPoolSize()`                                 | 获得线程池大小                                               |
| `getActiveCount()`                              | 线程池中正在执行任务线程的数量                               |
| `getCompletedTaskCount()`                       | 获得执行器完成的任务数量                                     |
| `getLargestPoolSize()`                          | 返回曾经同时位于线程池中的最大线程数                         |
| `shutdownNow()`                                 | 立即关闭 executor。不再执行挂起的任务，返回挂起任务的列表。调用时，正在执行的任务继续执行，但该方法并不等待这些任务完成，直接返回。 |
| `isTerminated()`                                | 调用 `shutdown()` 或 `shutdownNow()`，并且 executor 完成了关闭，该方法返回 true |
| `isShutdown()`                                  | 如果调用了 `shutdown()`，返回 true                           |
| `awaitTermination(long timeout, TimeUnit unit)` | 该方法阻塞所调用的线程，直到 executor 完成任务或者时间达到指定的 timeout 值 |
| `getTaskCount()`                                | 返回发送给执行器任务的数目                                   |
| `prestartAllCoreThreads()`                      | 创建线程池后调用该方法，可以预先创建 `corePoolSize` 数目的线程 |
| `prestartCoreThread`                            | 预先创建线程，不过只创建一条线程                             |

## 执行流程
通过 `execute()` 方法提交任务，线程池通过如下流程安排新的任务：

<img src="images/2019-09-25-13-44-15.png" style="zoom:80%;" />

1. 刚创建的线程池，里面没有线程，不过可以通过 `prestartCoreThread()` 或 `prestartAllCoreThreads()` 预先创建线程
2. 当调用 `execute()` 方法添加一个新任务时，线程池会作如下判断：
   - 如果正在运行的线程数小于 `corePoolSize`，则创建线程执行该任务；
   - 如果正在运行的线程数大于或等于 `corePoolSize`，但队列 `workQueue`未满，那么将该任务放入队列，按照FIFO的顺序执行；
   - 如果队列已满，而正在运行的线程数目小于 `maximumPoolSize`，则创建线程执行该任务；
   - 如果队列已满，且正在运行的线程数目大于或等于 `maximumPoolSize`，那么线程池会给 `RejectedExecutionHandler` 作拒绝处理。
3. 当一个线程执行结束，线程池从队列中取下一个任务执行。
4. 当一个线程无所事事超过一段时间（`keepAliveTime`），如果当前运行的线程数目大于 `corePoolSize`，线程池会关掉这个线程。

`submit()` 也是提交任务的方法，和 `execute()` 不同的是，它可以返回任务执行的结果：其内部调用 `execute()` 方法执行任务，通过 `Future()` 获得结果。

小结一下：在线程池中执行任务比为每个任务分配一个线程优势更多，通过重用现有的线程而不是创建新线程，可以在处理多个请求时分摊线程创建和销毁产生的巨大的开销。当请求到达时，通常工作线程已经存在，提高了响应性；通过配置线程池的大小，可以创建足够多的线程使CPU达到忙碌状态，还可以防止线程太多耗尽计算机的资源。

## 回收核心线程？

你可能会想到将 `corePoolSize` 的数量设置为 0，从而线程池的所有线程都是“临时”的，只有 `keepAliveTime` 存活时间，你的思路也许是正确的，但你有没有想过一个很严重的后果，`corePoolSize=0` 时，任务需要填满阻塞队列才会创建线程来执行任务，阻塞队列有设置长度还好，如果队列长度无限大呢，你就等着 OOM 吧，所以用这种设置行为并不是我们所需要的。

