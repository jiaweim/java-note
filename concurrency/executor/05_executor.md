# Executor 的生命周期

- [Executor 的生命周期](#executor-的生命周期)
  - [简介](#简介)
  - [线程池状态](#线程池状态)
- [线程池使用方法](#线程池使用方法)
  - [创建线程池](#创建线程池)
  - [提交任务](#提交任务)
  - [关闭线程池](#关闭线程池)
- [延迟任务与周期任务](#延迟任务与周期任务)
- [线程池配置](#线程池配置)

2024-02-08, 15:47
@author Jiawei Mao
***

## 简介

创建 `Executor` 很容易，但JVM只有在所有（非守护）线程全部终止后才会退出，因此，如果无法正确关闭 `Executor`，JVM 将无法结束。

为了解决执行服务的生命周期问题， `ExecutorService` 接口扩展 `Executor`，添加了一些用于生命周期管理的方法。如下：

```java
public interface ExecutorService extends Executor {
    void shutdown();
    List<Runnable> shutdownNow();
    boolean isShutdown();
    boolean isTerminated();
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;
    <T> Future<T> submit(Runnable task, T result);
    Future<?> submit(Runnable task);
    // 其它用于任务提交的便利方法
```

`ExecutorService` 的生命周期有三种状态：运行、关闭和已终止。

## 线程池状态

`ThreadPoolExecutor` 中定义了几个 final 字段用于定义线程池的状态：
```java
private static final int RUNNING    = -1 << COUNT_BITS;
private static final int SHUTDOWN   =  0 << COUNT_BITS;
private static final int STOP       =  1 << COUNT_BITS;
private static final int TIDYING    =  2 << COUNT_BITS;
private static final int TERMINATED =  3 << COUNT_BITS;

private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
private static final int COUNT_BITS = Integer.SIZE - 3;
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
```
用使用 `AtomicInteger` 类型字段 `ctl` 记录当前状态。`Integer` 有32位，其中高3位表示线程池状态，低29位表示线程池中的任务数目。
- `COUNT_BITS` 表示 `ctl` 变量中有效线程数量的位数，这里是 29；
- `CAPACITY` 表示最大有效线程数，这里是 $2^{29}-1=536870911$，五亿多，完全够用；

所以，这里用一个字段表示了状态和线程数目两个信息，主要是方面这两个信息的同时获取。

线程池有五种状态：
|状态|说明|
|---|---|
|RUNNING|线程池初始化后的默认状态，表示接收新任务，并且处理任务队列中的任务|
|SHUTDOWN|调用 `shutdown()` 后进入该状态，该状态下不接收新任务，但继续处理任务队列中已有的任务，包括那些还未开始执行的任务|
|STOP|调用 `shutdownNow()` 后进入该状态，该状态下线程池不接收新任务，并尝试终止所有正在执行的任务，并不再启动队列中尚未执行的任务|
|TIDYING|所有任务完成，`workerCount` 为 0 时进入该状态|
|TERMINATED|`terminated()`执行后进入该状态|

说明：
- `shutdown()` 执行平缓的关闭过程；
- `shutdownNow()` 执行粗暴的关闭过程；
- 当线程池处于 `SHUTDOWN` 或 `STOP` 状态，并且所有工作线程已经销毁，任务缓存队列已经清空或执行结束后，线程池被设置为 `TERMINATED` 状态。
- 在 `ExecutorService` 关闭后提交的任务将由 "Rejected Execution Handler" 处理，它会抛弃任务，或者使得 `execute` 方法抛出一个 `RejectedExecutionException`。
- 等所有任务都完成了，`ExecutorService` 转入终止状态。可以使用 `awaitTermination` 来等待 `ExecuteService` 到达终止状态，或者通过调用 `isTerminated` 查询 `ExecutorService` 是否已终止。通常在调用 `awaitTermination`后立即调用 `shutdown`，从而产生同步关闭 `ExecutorService` 的效果。


## 线程池使用方法


1. 定义线程类
```java
class Handler implements Runnable{  }
```

### 创建线程池

```java
ExecutorService executorService = Executors.newCachedThreadPool();  
```

或者
```java
int cpuNums = Runtime.getRuntime().availableProcessors();  //获取当前系统的CPU 数目  
ExecutorService executorService =Executors.newFixedThreadPool(cpuNums * POOL_SIZE); //ExecutorService通常根据系统资源情况灵活定义线程池大小  
```

### 提交任务
线程池通过 `execute()` 提交任务，提交任务后的执行流程：
- 如果线程池当前线程数量小于  ``

循环操作：
```java
while(true){  
  executorService.execute(new Handler(socket));   
     // class Handler implements Runnable{  
  或者  
  executorService.execute(createTask(i));  
      //private static Runnable createTask(final int taskID)  
}  
```
`execute(Runnable对象)`方法其实就是对 `Runnable` 对象调用 `start()` 方法（当然还有一些其他后台动作，比如队列，优先级，IDLE timeout，active激活等）

### 关闭线程池
`ThreadPoolExecutor` 提供了两个关闭线程池的方法，分别是 `shutdown()` 和 `shutdownNow()`，其中：
- `shutdown()` 调用后不再接受新的任务，等队列中的所有任务完成后终止；
- `shutdownNow()` 调用后不再接受新任务，并尝试中断正在制定的任务，且清空任务队列。


## 延迟任务与周期任务
`Timer` 类负责管理延迟任务（如在 100ms 后执行任务）和周期任务（如每 10ms 执行一次该任务）。然而，`Timer` 存在一些缺陷，`Timer` 支持基于绝对时间的调度机制，因此任务的执行对系统时钟变化很敏感，而 `ScheduledThreadPoolExecutor` 只支持基于相对时间的调度，因此应该考虑使用 `ScheduledThreadPoolExecutor` 来代替它。

`Timer` 在执行所有定时任务时只创建一个线程。如果某个任务的执行时间过长，那么将破坏其他 `TimerTask` 的定时准确性。

在 Java 5.0 之后已经很少使用 `Timer` 了，如果要构建自己的调度服务，可以使用 `DelayQueue`，它实现了 `BlockingQueue`，并为 `ScheduledThreadPoolExecutor` 提供调度服务。

## 线程池配置

一般需要根据任务的类型来配置线程池大小。

对CPU 密集型任务，就需要尽量压榨CPU，参考值可以设置为 NCPU+1，通常能实现最优的利用率。即使当计算密集型的线程偶尔因为某种原因暂停，额外的线程也能确保CPU的时钟周期不会被浪费。

如果是IO密集型任务，由于线程并不会一直执行，因此线程池的规模应该更大。参考值为 2*NCPU。
