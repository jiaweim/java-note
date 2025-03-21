# 并行 Flux

2025-03-21 ⭐
@author Jiawei Mao
***
## 简介

`Reactor` 提供 `ParallelFlux`，提供针对并行化的 operators。对任何 `Flux` 使用 `parallel()` 即可获得 `ParallelFlux`。

`parallel()` 本身不会并行化工作，而是将工作负载划分为 rails (rails 数量默认与 CPU 数相等)。

为了告诉 `ParallelFlux` 在哪里运行 rails，必须使用 `runOn(Scheduler)`。`Schedulers.paralle()` 是专门为并行任务设计的 `Scheduler`。

对比一下两个示例：

- 示例一

```java
Flux.range(1, 10)
    .parallel(2) ➊
    .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
```

```
main -> 1
main -> 2
main -> 3
main -> 4
main -> 5
main -> 6
main -> 7
main -> 8
main -> 9
main -> 10
```

➊ 强制使用多个 rails，而不是依赖于 CPU 内核数

- 示例二

```java
Flux.range(1, 10)
    .parallel(2)
    .runOn(Schedulers.parallel())
    .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
```

```
parallel-2 -> 2
parallel-1 -> 1
parallel-2 -> 4
parallel-1 -> 3
parallel-2 -> 6
parallel-1 -> 5
parallel-2 -> 8
parallel-1 -> 7
parallel-2 -> 10
parallel-1 -> 9
```

第二个示例在两个线程上并行。

在并行处理序列后，如果想要恢复到正常 `Flux` 以顺序方式应用 operator-chain 的余下部分，可以使用 `ParallelFlux` 的 `sequential()` 方法：

- 如果使用 `Subscriber` 来 `subscribe` `ParallelFlux`，则隐式应用 `sequential()`

- 如果使用基于 lambda 的 `subscribe`变体，则不应用

`subscribe(Subscriber<T>)` 合并所有 rails，而 `subscribe(Consumer<T>)` 运行所有 rails。如果 `subscribe()` 方法包含一个 lambda，则 lambda 执行次数与 rails 相等。

也可以通过 `groups()` 方法以 `Flux<GroupedFlux<T>>` 的形式访问各个 rail，并通过 `composeGroup()` 方法应用其它 operators。

## Scheduler

线程通常被定义为轻量级进程，但它也可以看作是程序的执行路径。每个 Java 应用至少在 一个线程运行，即 main 线程。

获取 `Flux` 或 `Mono` 并不表示它一定会在专门的 `Thread` 运行。相反，大多数 operator 会继续在前一个 operator 执行的线程中工作。

Reactor 一切从订阅开始，所以最顶层（source）会在调用 `subscribe()` 的 `Thread` 运行。

**示例**：在 main 中运行以下代码

在序列的每一步，打印正在执行的 operator 和方法所在线程。

```java
Flux.just(1, 2, 3, 4, 5)
        .map(i -> {
            System.out.format("map(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return i * 10;
        })
        .flatMap(i -> {
            System.out.format("flatMap(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return Mono.just(i * 10);
        })
        .subscribe(i ->
                System.out.format("subscribe(%d) - %s\n",
                        i, Thread.currentThread().getName())
        );
```

```
map(1) - main
flatMap(10) - main
subscribe(100) - main
map(2) - main
flatMap(20) - main
subscribe(200) - main
map(3) - main
flatMap(30) - main
subscribe(300) - main
map(4) - main
flatMap(40) - main
subscribe(400) - main
map(5) - main
flatMap(50) - main
subscribe(500) - main
```

可以看到，所有步骤都在 main 线程执行（即订阅所在的线程）。

如果在其它线程订阅？

**示例**：在其它线程订阅

```java
Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5)
        .map(i -> {
            System.out.format("map(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return i * 10;
        })
        .flatMap(i -> {
            System.out.format("flatMap(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return Mono.just(i * 10);
        });

Thread myThread = new Thread(() -> integerFlux.subscribe(i ->
        System.out.format("subscribe(%d) - %s\n",
                i, Thread.currentThread().getName())
));

myThread.start();
myThread.join(); // 让程序等待该线程结束
```

```
map(1) - Thread-0
flatMap(10) - Thread-0
subscribe(100) - Thread-0
map(2) - Thread-0
flatMap(20) - Thread-0
subscribe(200) - Thread-0
map(3) - Thread-0
flatMap(30) - Thread-0
subscribe(300) - Thread-0
map(4) - Thread-0
flatMap(40) - Thread-0
subscribe(400) - Thread-0
map(5) - Thread-0
flatMap(50) - Thread-0
subscribe(500) - Thread-0
```

此时，`Flux` 的所有步骤都在 Thread-0 执行，而不是 main 线程。


有些 operator 会修改执行的线程，如 `delayElements` 默认在 `parallel` Scheduler 执行。

**示例**：

```java
Flux.just(1, 2, 3, 4, 5)
        .map(i -> {
            System.out.format("map(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return i * 10;
        })
        .delayElements(Duration.ofMillis(10)) // 每个元素延迟 10 毫秒
        .flatMap(i -> {
            System.out.format("flatMap(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return Mono.just(i * 10);
        })
        .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                i, Thread.currentThread().getName())
        );
Thread.sleep(1000); // 给出时间让前面运行完
```

```
map(1) - main
map(2) - main
map(3) - main
map(4) - main
map(5) - main
flatMap(10) - parallel-1
subscribe(100) - parallel-1
flatMap(20) - parallel-2
subscribe(200) - parallel-2
flatMap(30) - parallel-3
subscribe(300) - parallel-3
flatMap(40) - parallel-4
subscribe(400) - parallel-4
flatMap(50) - parallel-5
subscribe(500) - parallel-5
```

可以看到，`map` 在 main 线程之后，`delayElements` 之后，在 `parallel()` `Scheduler` 执行，每个元素在单独线程执行。

> [!TIP]
>
> `delayElements()` 还有一个 `delayElements(Duration delay, Scheduler timer)` 版本，可以指定执行的 scheduler，而不用在默认的 `parallel()` scheduler 执行。

另外，`delaySequence`, `delaySubscription`, `interval` 也可以在不同线程执行。

## 创建 Scheduler


在 Reactor 中，执行发生的位置由使用的 `Scheduler` 决定。`Scheduler` 与 `ExecutorService` 调度功能类似，但是功能更丰富。

`Schedulers` 类提供访问以下 `Scheduler` 的 static 方法：

- 没有执行 context (**Schedulers.immediate()**)：在处理时，提交的 `Runnable`  直接被执行，即在当前 `Thread` 执行；
- 单个可重复使用的线程（**Schedulers.single()**）：该方法为所有调用者重复使用同一个线程，直到 `Scheduler` 被释放。如果想要每个调用都有一个专用线程，可以使用 `Schedulers.newSingle()`；
- 无界弹性线程池（**Schedulers.elastic()**）：随着 `Schedulers.boundedElestic()` 的引入，这个线程池不再是首选，因为它容易隐藏背压问题，同时导致线程过多；
- 有界弹性线程池（**Schedulers.boundedElastic()**）：首选线程池。可以让阻塞进程在单独线程运行。从 3.6.0 开始，可以设置两种不同的实现：
  - 基于 `ExecutorService`，重用平台线程。该实现与 `elastic()` 一样，根据需要创建新的 worker-pool 并重用空闲的。空闲时间过长（默认 60s）的 worker-pool 被处理。与 `elastic()` 不同的是，它可以控制后备线程的上限（默认 CPU cores x10）。达到上限后最多支持 100,000 个 tasks 入队，在有线程可用时重新安排（如果设置有延迟，延迟从线程可用时开始计算）
  - 每个 task 一个线程，用于在 `VirtualThread` 上运行。使用该功能需要 JDK 21+，并将系统属性 `reactor.schedulers.defaultBoundedElasticOnVirtualThreads` 设置为 `true`。满足以上条件，共享的 `Schedulers.boundedElastic()` 将返回特定的 `BoundedElasticScheduler`  实现，该实现在 `VirtualThread` 的新实例上运行每个 task。该实现的行为与基于 `ExecutorService` 的实现类似，但没有空闲池，并且为每个 task 创建一个新的 `VirtualThread`。
- 为并行任务 **Schedulers.parallel()** 而调优的固定 worker-pool。它会创建与 CPU cores 数量相同的工作线程。
- `Schedulers.fromExecutorService(ExecutorService)` 从任何现有 `ExecutorService` 创建 `Scheduler`。

也可以使用 `newXXX` 方法创建各种 scheduler 类型。例如，`Schedulers.newParallel(yourScheduleName)` 创建一个名为 `yourScheduleName` 的 parallel-scheduler。

一般来说，`newXXX` 创建特定 `Scheduler` 的新实例，而其它方法返回第一次调用时创建并缓存提供后续调用的实例。

**示例**：`delaySubscription` 指定 boundedElastic `Scheduler`

```java
Flux.just(1, 2, 3, 4, 5)
        .map(i -> {
            System.out.format("map(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return i * 10;
        })
        .flatMap(i -> {
            System.out.format("flatMap(%d) - %s\n",
                    i, Thread.currentThread().getName());
            return Mono.just(i * 10);
        })
        .delaySubscription(Duration.ofMillis(10), Schedulers.boundedElastic())
        .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                i, Thread.currentThread().getName()
        ));
Thread.sleep(1000);
```

```
map(1) - boundedElastic-1
flatMap(10) - boundedElastic-1
subscribe(100) - boundedElastic-1
map(2) - boundedElastic-1
flatMap(20) - boundedElastic-1
subscribe(200) - boundedElastic-1
map(3) - boundedElastic-1
flatMap(30) - boundedElastic-1
subscribe(300) - boundedElastic-1
map(4) - boundedElastic-1
flatMap(40) - boundedElastic-1
subscribe(400) - boundedElastic-1
map(5) - boundedElastic-1
flatMap(50) - boundedElastic-1
subscribe(500) - boundedElastic-1
```

`delaySubscription` 默认在 parallel `Scheduler` 执行，这里将其修改为了 boundedElastic。

## 修改执行 Scheduler

对参数没有 `Scheduler` 的 operator，Reactor 提供了两种方法来修改执行序列的 `Scheduler`：

- `publishOn`
- `subscribeOn`



## 参考

- https://docs.spring.io/projectreactor/reactor-core/docs/current-SNAPSHOT/reference/html/advancedFeatures/advanced-parallelizing-parralelflux.html
- https://eherrera.net/project-reactor-course/06-schedulers-and-threads/