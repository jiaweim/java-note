# CompletableFuture

## 简介

Java 5 引入 `Future` 表示异步计算结果，但它没有提供任何合并结果或处理错误的方法。使用 `Future` 获得异步执行结果时，要么调用阻塞方法 `get()`，要么轮询 `isDone`，这两种方法都会使主线程等待。

java 8 引入 `CompletableFuture`，除了实现 `Future` 接口，它还实现了 `CompletionStage` 接口。 该接口定义了与其它异步计算步骤合并的协议。另外，`CompletableFuture` 还是一个完成的框架，它提供了大约 50 个方法，用来组合、合并和执行异步计算步骤以及处理错误。

简而言之，`CompletableFuture` 为异步、非阻塞服务。

### CompletionStage

`CompletionStage` 表示异步计算的一个步骤，在另一个 `CompletionStage` 完成时触发该步骤。一个 stage 在计算终止后完成，但其完成可能触发另一个 stages。该接口定义了几种基本形式的函数，可以扩展为一系列的用法：

- stage 执行的计算可以表示为 `Function`, `Consumer` 或 `Runnable`对应方法名称 `apply`, `accept` 和 `run`，具体取决于需要的参数和返回结果，例如

```java
stage.thenApply(x -> square(x))
    .thenAccept(x -> System.out.print(x))
    .thenRun(() -> System.out.println());
```

- 一个 stage 的执行可能由一个 stage，两个 stage 同时，或两个 stages 任意一个完成触发。
  - 对一个 stage 的依赖关系可以通过以 `then` 开头的方法实现
  - 由两个 stages 同时完成触发的方法以 `combine` 方法实现
- stages 之间的依赖关系控制计算的触发，但不能保证顺序。另外，一个新 stage 的执行由三种方式：
  - 执行
  - 异步执行（使用带 `async` 后缀的方法）
  - 自定义：提供 `Executor`

默认执行和异步执行的属性由 `CompletionStage` 的实现指定，而非该接口。指定 `Executor` 参数的方法可能具有任意执行属性，甚至可能不支持并发，而是以其它异步方式进行处理。

- 不管触发 stage 是正常还是异常完成，`handle` 和 `whenComplete` 无条件执行。`exceptionally` 在触发 stage 异常完成时执行，用于提供备选结果，类似 `catch` 关键字。

### CompletableFuture 特点

- **异步执行**
- **任务组合**：高效的方式链接多个异步操作
  - `thenApply()`：当 `CompletableFuture` 完成，对其结果进行转换。该方法以 `Function` 为参数，处理上一个 stage 的结果，返回一个新的 `CompletableFuture`
  - `thenCompose()`：链接多个异步任务，下一个任务依赖于上一个任务。它需要一个返回 `CompletableFuture` 的函数，创建任务之间的 stream
  - `thenCombine()`：并行执行两个独立的 `CompletableFuture`， 当两个任务都完成，使用特定函数合并它们的结果。用于合并来自不同 source 的结果。
  - `allOf()`：多个 `CompletableFuture` 并行执行，返回一个 `CompletableFuture`，当提供的所有 futures 完成时它才完成。适合并行执行多个任务，并等待完成。
- **异常处理**
  - `exceptionally()`：提供从错误恢复的方法。当出错时，返回备选值或备选操作。
  - `handle()`：使用单个 callback 处理结果或异常。当 `CompletableFuture` 正常完成，则转换结果，当 `CompletableFuture ` 出现异常，则处理异常。
- **取消和完成**
  - `cancel()`：用于取消任务。可以打断执行任务的线程，或者只是标记为已取消
  - `complete()`：允许手动完成`CompletableFuture`
  - `completeExceptionally()`：以指定异常完成 `CompletableFuture`。

### 应用场景

`CompletableFuture` 可应用于许多场景以提高应用性能和响应性。例如：

- UI 响应性：通过异步执行任务，应用可以在执行耗时任务时保持 UI 的响应能力
- 数据处理：`CompletableFuture` 支持并行处理数据
- 批处理：从多个异步任务汇总结果，从而提高效率
- 后台任务：任何不需要立即反馈的任务都可以从 `CompletableFuture` 受益

## Future

`Future` 在 Java 1.5 引入，用于表示异步计算结果。 

长时间运行的方法适合异步处理和 `Future`，因为在等待方法完成的时候可以执行其它任务。

可以利用 `Future` 的异步示例：

- 计算密集型任务
- 操作大型数据结构（大数据）
- 远程调用（下载文件、HTML、Web 服务）

### 创建 Future

下面创建一个非常简单的类来计算整数平方，显然这不符合长时间运行的要求，所以在方法中调用 `Thread.sleep()` 使其完成前睡 1 秒。

```java
public class SquareCalculator {

    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    public Future<Integer> calculate(Integer input) {
        return executor.submit(() -> {
            Thread.sleep(1000);
            return input * input;
        });
    }
}
```

`Callable` 接口代表返回结果的 task，它只有一个 `call()` 方法，实际执行的任务代码在 `call()` 方法中，上面以 lambda 表达式的形式提供。

`ExecutorService.submit()` 返回 `Future` 类型，代表异步执行结果。

在主线程某个时刻调用 `Future` 的 `get()` 方法，就可以获得异步执行的结果。在调用 `get()` 时，如果异步任务已经完成，就直接获得结果；如果异步任务没有完成，那么 `get()` 阻塞，直到任务完成返回结果。

`Future` 定义的方法有：

- `get()`：获得结果（阻塞）
- `get(long timeout, TimeUnit unit)`：获得结果，但只等待指定时间
- `cancel(boolean mayInterrupt)`：取消任务
- `isDone()`：任务是否完成

## 创建 CompletableFuture

### CompletableFuture()

无参构造函数：

```java
CompletableFuture<String> future = new CompletableFuture<>();
```

该 `CompletableFuture` 没有关联任何 `Callable<String>`、线程池或异步任务。如果调用 `future.get()`，则会一直阻塞。如果注册 callbacks，注册的 callback 也不会触发。此时可以调用：

```java
future.complete("hello");
```

手动触发 `CompletableFuture` 完成。此时所有注册的 callbacks 被触发。`CompletableFuture.complete()` 只能被调用一次，后续调用将被忽略。

不过 `CompletableFuture.obtrudeValue(value)` 可以强制设置 `get()` 和相关方法返回的值，不管 `CompletableFuture` 是否完成。不过要谨慎使用。

 

**示例**：创建 `CompletableFuture`，在另一个线程执行计算，立即返回 `Future`

```java
public Future<String> calculateAsync() throws InterruptedException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    Executors.newCachedThreadPool().submit(() -> {
        Thread.sleep(500);
        completableFuture.complete("Hello");
        return null;
    });

    return completableFuture;
}
```

这种创建和完成 `CompletableFuture` 的方法可以与任何并发机制或 API 一起使用，包括原始线程。




### completedFuture

```java
public static <U> CompletableFuture<U> completedFuture(U value);
```

创建一个包含指定值的已经完成的 `CompletableFuture`。

**示例**：创建一个已经完成的 `CompletableFuture`，提供预定义结果，这通常是计算的第一步

```java
CompletableFuture<String> cf = CompletableFuture.completedFuture("message");
assertTrue(cf.isDone());
assertEquals("message", cf.getNow(null));
```

- `isDone()`：当 `CompletableFuture` 完成时，返回 true（正常、异常或取消均为完成）
- `T getNow(T valueIfAbsent)`：如果完成，返回接我；否则返回指定值 `valueIfAbsent`

### supplyAsync

```java
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,
                                                   Executor executor);
```

创建一个 `CompletableFuture`，`supplier` 提供的任务在 `executor` 执行，当任务完成，`CompletableFuture` 完成。

`executor` 默认为 `ForkJoinPool.commonPool()`。`ForkJoinPool.commonPool()` 简化异步任务的创建和管理，适合轻量级任务。

**示例**：创建一个后台任务，当任务执行时，main 线程可以执行其它操作

```java
CompletableFuture<String> future = CompletableFuture
        .supplyAsync(() -> "Result from async task!"); // 模拟耗时任务
```

- **自定义 Executor**

如果需要管理线程生命周期，或者 CPU 密集型任务，建议自定义 executor。

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
CompletableFuture<String> future = CompletableFuture
        .supplyAsync(() -> "Result from async task!", executor);
```

### runAsync

```java
public static CompletableFuture<Void> runAsync(Runnable runnable);
public static CompletableFuture<Void> runAsync(Runnable runnable,
                                               Executor executor);
```

 以 `Async` 结尾但不包含 `Executor` 参数的方法使用 `ForkJoinPool.commonPool()` 线程池执行任务。

该方法与 `supplyAsync` 的唯一差别是不返回值。



## 任务组合

### thenApply

```java
public <U> CompletionStage<U> thenApply(Function<T,U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<T,U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<T,U> fn,
                                             Executor executor);
```

⭐**串联任务**。指定 stage 完成后执行的动作。

`thenApply` 与 `thenCompose` 的主要差别在于：`thenApply` 的 function 可以返回非 future 类型，而 `thenCompose` 的 function 直接返回 `CompletionStage` 类型。

`Async` 与非 `Async` 版本的差异在于：`Async` 串联的 stage 在其它线程执行，而非 `Async` 串联的 stage 与当前 stage 在同一个线程执行。

**示例**：`thenApply` 和 `thenCompose`

```java
CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 2);

CompletableFuture<Integer> future2 = future1.thenApply(result -> result * 2);
CompletableFuture<Integer> future3 = future1.thenCompose(
        result -> CompletableFuture.supplyAsync(() -> result * 2));
```

- `thenApply`：当需要转换 `CompletableFuture` 的结果，但不需要启动另一个异步操作，适合**简单转换操作**。
- `thenCompose`：当使用一个 `CompletableFuture` 的结果启动另一个返回 `CompletableFuture` 的异步操作，适合串联多个异步操作。

### thenAccept

```java
public CompletionStage<Void> thenAccept(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,
                                             Executor executor);
```

⭐**任务串联**。指定 stage 完成后执行的动作。

该方法以 `Consumer` 为参数，而 `Consumer` 以当前 stage 结果为参数，但不返回值。

该方法对执行 side-effect 非常有用，如日志记录、**更新用户界面**。

在异步版本中，新的 stage 在指定 executor 执行。

**示例**：

```java
CompletableFuture.supplyAsync(() -> "Hello, World!")
        .thenAccept(s -> System.out.println("Result:" + s));
```

字符串 "Hello, World!" 异步生成，计算完成后，结果被打印到控制台。

> [!TIP]
>
> `theAccept` 和 `thenRun` 通常为 future 管线的最后一步。`thenAccept` 使用最后生成的值。`thenRun` 则不需要最后生成的值。

### thenRun

```java
public CompletionStage<Void> thenRun(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action,
                                          Executor executor);
```

⭐**任务串联**。与 `thenAcccept` 一样，指定 stage 完成后执行的动作。

与 `thenAccept` 不同的是，`thenRun` 的参数为 `Runnable` 类型，即不接收上一个 stage 生成的结果，适合执行不依赖于上一步计算结果的动作。

**示例**：

```java
CompletableFuture.supplyAsync(() -> "Task Complete")
        .thenRun(() -> System.out.println("Next task starts now."));
```

### thenCombine

```java
public <U,V> CompletionStage<V> thenCombine(CompletionStage<U> other,
     										BiFunction<T,U,V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<U> other,
                                                 BiFunction<T,U,V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<U> other,
                                                 BiFunction<T,U,V> fn,
                                                 Executor executor);
```

⭐**任务合并**。指定当该 stage 和另一个 stage 均完成时执行的动作。

适合用来合并两个独立异步计算的结果。

**示例**：合并两个独立任务的结果，输出合并后的值

```java
CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);
future1.thenCombine(future2, Integer::sum)
        .thenAccept(i -> System.out.println("Combined Result:" + i));
```

```
Combined Result:30
```

### thenCompose

```java
public <U> CompletionStage<U> thenCompose(Function<T, CompletionStage<U>> fn);
public <U> CompletionStage<U> thenComposeAsync(Function<T, CompletionStage<U>> fn);
public <U> CompletionStage<U> thenComposeAsync(Function<T, CompletionStage<U>> fn,
                                               Executor executor);
```

⭐**任务串联**。当前 stage 完成时，指定的 `Function` 以其结果为参数，返回另一个 stage。

内部返回的是 stage 类型，适合 fluent 调用。

**示例**：

```java
CompletableFuture<Integer> future =
        CompletableFuture.supplyAsync(() -> 5)
                .thenCompose(i -> CompletableFuture.supplyAsync(() -> i * 2));
future.thenAccept(i -> System.out.println("Final Result:" + i));
```

```
Final Result:10
```

### allOf：并行任务

```java
public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs);
```

并行执行任务对提供程序的性能和响应能力至关重要。

`allOf()` 返回一个 `CompletableFuture`，当所有指定 `CompletableFuture` 完成，它才完成：

- 任意一个 `CompletableFuture` 出现异常，返回 `CompletableFuture` 也返回异常，异常信息通过 `CompletionException ` 查询
- 如果不提供 `CompletableFuture`，返回的 `CompletableFuture` 的值为 null。

该方法可用于等待一组独立的 `CompletableFuture` 完成，然后继续程序，例如：

```java
CompletableFuture.allOf(c1, c2, c3).join();
```

当有多个**独立的并行任务**，`allOf()` 可以整个结果：

- 以 `CompletableFuture` 数组为参数
- 返回一个 `CompletableFuture`，当提供的所有 futures 完成，它才完成
- 同步多个任务的便捷方法


## 任务取消

## 完成或异常

### complete

```java
public boolean complete(T value);
public CompletableFuture<T> completeAsync(Supplier<? extends T> supplier);
public CompletableFuture<T> completeAsync(Supplier<? extends T> supplier,
                                          Executor executor);
```

⭐将该 future 标记为完成，返回指定值。

`complete` 用于手动完成 `CompletableFuture`。当异步操作的结果取决于外部事件或条件，该方法特别有用。

通过调用 `complete()`，不用等待异步任务完成即可设置 `CompletableFuture` 的结果：

- 调用 `complete(value)`：传入的参数作为 future 的返回值，通知等待 future 完成的所有 consumers；
- 执行依赖动作：所有通过 `thenApply()` 或 `thenAccept()` 链接的 `CompletableFuture` 使用提供的值继续执行

### completeExceptionally

```java
public boolean completeExceptionally(Throwable ex);
```

- 调用 `completeExceptionally(e)`：传入一个异常，`CompletableFuture` 

### exceptionally

```java
public CompletionStage<T> exceptionally(Function<Throwable, T> fn);
public default CompletionStage<T> exceptionallyAsync(Function<Throwable, T> fn);
public default CompletionStage<T> exceptionallyAsync(Function<Throwable, T> fn, 
                                                     Executor executor);
```

⭐创建 一个新的 stage：

- 若当前 stage 出现异常，新 stage 执行的`Function` 以异常为参数
- 若当前 stage 正常完成，新 stage 的返回值与其相同

**示例**：使用 `exceptionally` 捕获 `ArithmeticException`，出现异常时提供默认值

```java
CompletableFuture.supplyAsync(() -> {
    if(true){
        throw new ArithmeticException();
    }
    return 1;
}).exceptionally(error -> {
    System.out.println("Exception occurred:"+ error.getMessage());
    return 0; // 默认值
});
```

### handle

```java
public <U> CompletionStage<U> handle(BiFunction<T,Throwable,U> fn);
```

`handle` 处理异常更灵活，提供结果和异常：

- 当 stage 正常完成，提供结果，异常为 null
- 当 stage 出现异常，提供异常，结果为 null

示例：

```java
CompletableFuture<Integer> safe = future.handle((ok, ex) -> {
    if (ok != null) {
        return Integer.parseInt(ok);
    } else {
        log.warn("Problem", ex);
        return -1;
    }
});
```

## 并行

### defaultExecutor

```java
 public Executor defaultExecutor();
```

对未指定 `Executor` 的异步方法的默认 `Executor`。如果支持多个线程，则默认使用 `ForkJoinPool.commonPool()`，否则对每个异步任何使用一个线程的`Executor`。

### join

```java
public T join();
```

类似 `get()`，阻塞并返回结果。



## 示例

演示如何使用 `CompletableFuture` 执行异步任务，使用 `Thread.sleep()` 模拟耗时任务。

- **模拟耗时任务**

在 Java 中，可以通过 `Thread.sleep()` 模拟延迟，该方法暂停当前线程一段时间，可用来模拟网络延迟或耗时计算。

**示例**：使用 `Thread.sleep` 5 秒，掩饰 `CompletableFuture` 的异步特性

```java
CompletableFuture.supplyAsync(() -> {
    try {
        Thread.sleep(5000); // 模拟耗时任务
    } catch (InterruptedException e) {
        throw new IllegalStateException(e);
    }
    return "Hello, world!";
});
```

- **处理返回值**

在异步任务完成后，如何处理结果？`CompletableFuture` 提供了许多结果，这里返回 `String` 类型，可以调用 `thenAccept` 来获取并处理结果：

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    try {
        Thread.sleep(5000); // 模拟耗时任务
    } catch (InterruptedException e) {
        throw new IllegalStateException(e);
    }
    return "Hello, world!";
});
future.thenAccept(System.out::println); // 直接打印结果
```

- **串联异步任务**

如何创建一系列按顺序执行的异步操作？



## CompletableFuture 的使用

### 运行异步 Stage

**示例**：异步执行一个 `Runnable` 

```java
@Test
void runAsync() {
    CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
        assertTrue(Thread.currentThread().isDaemon());
        randomSleep();
    });
    assertFalse(cf.isDone());
    sleepEnough();
    assertTrue(cf.isDone());
}
```

辅助方法：

```java
static Random random = new Random();

private static void randomSleep() {
    try {
        Thread.sleep(random.nextInt(1000));
    } catch (InterruptedException e) {
        // ...
    }
}

private static void sleepEnough() {
    try {
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        // ...
    }
}
```

- 通常以 `Async` 结尾的方法异步执行
- 异步执行默认（未指定 `Executor`）使用 `ForkJoinPool` 实现，该实现使用守护线程执行 `Runnable` 任务。

### 应用 Function

**示例**：转换为大写

```java
CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
        .thenApply(String::toUpperCase);
assertEquals("MESSAGE", cf.getNow(null));
```

- `thenApply`：类似 `Optional.map` 和 `Stream.map`，当该 stage 正常完成，应用指定 `Function`

`Function` 的执行为阻塞操作。

### 异步应用 Function

**示例**：`thenApply` 异步应用  `Function`

```java
CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
        .thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().isDaemon());
            randomSleep();
            return s.toUpperCase();
        });
assertNull(cf.getNow(null));
assertEquals("MESSAGE", cf.join());
```

- `thenApplyAsync` 在 `ForkJoinPool.commonPool()` 执行

### 自定义 Executor

异步方法可以使用自定义 `Executor` 执行 `CompletableFuture`。

**示例**：自定义线程池执行 `Function`

```java
static ExecutorService executor = Executors.newFixedThreadPool(3,
        new ThreadFactory() {
            int count = 1;

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "custom-executor-" + count++);
            }
        });

@Test
void thenApplyAsyncExecutor() {
    CompletableFuture<String> cf = CompletableFuture
            .completedFuture("message")
            .thenApplyAsync(s -> {
                assertTrue(Thread.currentThread().getName()
                        .startsWith("custom-executor-"));
                assertFalse(Thread.currentThread().isDaemon());
                return s.toUpperCase();
            }, executor);
    assertNull(cf.getNow(null));
    assertEquals("MESSAGE", cf.join());
}
```

### consume stage

如果下一个 stage 需要当前 stage 结果，但不返回值，则可以使用以 `Consumer` 为参数的 `thenAccept` 方法。

**示例**：同步执行 `thenAccept`

```java
@Test
void thenAccept() {
    StringBuilder sb = new StringBuilder();
    CompletableFuture.completedFuture("thenAccept message")
            .thenAccept(sb::append);
    assertFalse(sb.isEmpty());
}
```

- `Consumer` 会同步执行，所以不需要对返回的 `CompletableFuture` 进行 join

### 异步 consume stage

```java
@Test
void thenAcceptAsync() {
    StringBuilder sb = new StringBuilder();
    CompletableFuture<Void> cf = CompletableFuture
            .completedFuture("thenAcceptAsync message")
            .thenAcceptAsync(sb::append);
    cf.join();
    assertFalse(sb.isEmpty());
}
```

### 异常

**示例**：将字符串转换为大写，其中 sleep 一秒模拟延迟。使用 `thenApplyAsync(Function, Executor)` 方法，通过 `Executor` 引入延迟

```java
@Test
void completeExceptionally() {
    CompletableFuture<String> cf = CompletableFuture
            .completedFuture("message")
            .thenApplyAsync(String::toUpperCase,
                    CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
    CompletableFuture<String> exceptionHandler = cf.handle(
            (s, error) -> error != null ? "message upon cancel" : "");
    cf.completeExceptionally(new RuntimeException("completed exceptionally"));
    assertTrue(cf.isCompletedExceptionally());
    try {
        cf.join();
        fail("should have thrown an exception");
    } catch (CompletionException e) {
        assertEquals("completed exceptionally", e.getCause().getMessage());
    }
    assertEquals("message upon cancel", exceptionHandler.join());
}
```

- 首先创建一个已经完成的 `CompletableFuture`，其值为 "message"
- 调用 `thenApplyAsync` 返回一个新的 `CompletableFuture`，该 stage 在第一个 stage 完成后以异步方式应用大写转换（第一个 stage 已完成，所以会立即执行该操作）
- `delayedExecutor(timeout, TimeUnit)` 用于延迟异步任务
- `exceptionHandler` 用于处理异常
- 使用 `completeExceptionally` 异常结束，使得 `join()` 方法抛出 `CompletionException`

## 性能问题

`CompletableFuture` 默认采用 `ForkJoinPool.commonPool()` 线程池，该线程池旨在有效管理线程，，适合各种任务。但是，使用 commonPool 可能导致各种性能问题。例如，如果同时提交许多任务，可能导致线程争用、增加延迟：

- 线程管理：common-pool 根据 workload 动态调整线程数。但是，在高通量应用中，这可能不是最好结果；
- 阻塞任务：阻塞任务会减少整体通量，导致任务延迟
- 延迟和通量：common-pool 的大小会影响延迟和通量

为了获得更好的性能，通常建议使用自定义 executor。
