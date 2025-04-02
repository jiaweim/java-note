# CompletableFuture

## 简介

Java 5 引入 `Future` 表示异步计算结果，但它没有提供任何合并结果或处理错误的方法。使用 `Future` 获得异步执行结果时，要么调用阻塞方法 `get()`，要么轮询 `isDone`，这两种方法都会使主线程等待。

java 8 引入 `CompletableFuture`，除了实现 `Future` 接口，它还实现了 `CompletionStage` 接口。 该接口定义了与其它异步计算步骤合并的协议。另外，`CompletableFuture` 还是一个完成的框架，它提供了大约 50 个方法，用来组合、合并和执行异步计算步骤以及处理错误。

简而言之，`CompletableFuture` 为异步、非阻塞服务。

异步与并行：

- 异步对应非阻塞任务
- 并行指利用多核处理器同时执行多个任务

两种方法都提高了性能，但目的不同。

.NET 框架中的 C# 利用 `Task<T>` 类以及 `async`, `await` 关键字实现异步编程。`async` 方法返回 `Task`，表示正在执行的任务。C# 与 Java 一样，采用线程池执行异步任务。

JavaScript 采用 Promises 和 async/await 语法处理异步操作。`Promise` 表示还没完成，但是未来需要的操作。JavaScript 采用单线程事件循环处理异步操作。

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

`Future` 的缺陷：

- 不能手动完成
- 不能合并多个 futures
- 不能串联多个 futures

**示例**：Future `get()` 阻塞

```java
@Test
public void testFuture() throws ExecutionException, InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    Future<String> future = executorService.submit(() -> {
        Thread.sleep(2000);
        return "hello";
    });
    System.out.println(future.get());
    System.out.println("end");
}
```

```
hello
end
```

- Future 无法解决任务相互依赖的场景，使用 `CountDownLatch` 辅助可以解决该问题

```java
ExecutorService executorService = Executors.newFixedThreadPool(5);

CountDownLatch downLatch = new CountDownLatch(2);

long startTime = System.currentTimeMillis();
Future<String> userFuture = executorService.submit(() -> {
    //模拟获取用户信息耗时500毫秒
    Thread.sleep(500);
    downLatch.countDown();
    return "用户A";
});

Future<String> goodsFuture = executorService.submit(() -> {
    //模拟查询商品耗时500毫秒
    Thread.sleep(400);
    downLatch.countDown();
    return "商品A";
});

downLatch.await();
//模拟主程序耗时时间
Thread.sleep(600);
System.out.println("获取用户信息:" + userFuture.get());
System.out.println("获取商品信息:" + goodsFuture.get());
System.out.println("总共用时" + (System.currentTimeMillis() - startTime) + "ms");
```

```
获取用户信息:用户A
获取商品信息:商品A
总共用时1120ms
```

这里不用异步，需要 500+400+600=1500 毫秒，用 异步则只需要 500+600=1100 毫秒。

`CountDownLatch` 虽然能解决问题，但是不够优雅，对更复杂的异步会十分繁琐。

**示例**：使用 `CompletableFuture` 实现相同操作

```java
long startTime = System.currentTimeMillis();

//调用用户服务获取用户基本信息
CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() ->
        //模拟查询商品耗时500毫秒
{
    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "用户A";
});

//调用商品服务获取商品基本信息
CompletableFuture<String> goodsFuture = CompletableFuture.supplyAsync(() ->
        //模拟查询商品耗时500毫秒
{
    try {
        Thread.sleep(400);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "商品A";
});

System.out.println("获取用户信息:" + userFuture.get());
System.out.println("获取商品信息:" + goodsFuture.get());

//模拟主程序耗时时间
Thread.sleep(600);
System.out.println("总共用时" + (System.currentTimeMillis() - startTime) + "ms");
```

```
获取用户信息:用户A
获取商品信息:商品A
总共用时1118ms
```

使用 `CompletableFuture` 实现更为简单。

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

创建 `CompletableFuture` 的方法：

- `supplyAsync`: 执行任务，支持返回值
- `runAsync`：执行任务，无返回值

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

**示例**：创建一个已完成 `CompletableFuture`，提供预定义结果，这通常是计算的第一步

```java
CompletableFuture<String> cf = CompletableFuture.completedFuture("message");
assertTrue(cf.isDone());
assertEquals("message", cf.getNow(null));
```

- `isDone()`：当 `CompletableFuture` 完成时，返回 true（正常、异常或取消均为完成）
- `T getNow(T valueIfAbsent)`：如果完成，返回结果；否则返回指定值 `valueIfAbsent`

### supplyAsync

```java
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,
                                                   Executor executor);
```

创建 `CompletableFuture`，`supplier` 提供的任务在 `executor` 执行，当任务完成，`CompletableFuture` 完成。

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

⭐该方法与 `supplyAsync` 的唯一差别是不返回值。

**示例**：运行一个简单的异步任务

```java
CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
    assertTrue(Thread.currentThread().isDaemon());
    randomSleep();
});
assertFalse(cf.isDone());
sleepEnough();
assertTrue(cf.isDone());
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

- 以 `Async` 结尾的方法异步执行
- 异步执行默认（未指定 `Executor`）使用 `ForkJoinPool` 实现，该实现使用守护线程执行 `Runnable` 任务。


## 回调方法

| callback        | 异步版            | 功能                                       |
| --------------- | ----------------- | ------------------------------------------ |
| `thenRun`       | `thenRunAsync`    | 不需要上一个任务的返回值，无参数，无返回值 |
| `thenAccept`    | `thenAcceptAsync` | 需要上一个任务的返回值，有参数，无返回值   |
| `thenApply`     | `thenApplyAsync`  | 需要上一个任务的返回值，有参数，有返回值   |
| `exceptionally` |                   | 任务异常时执行的 callback                  |
| `whenComplete`  |                   | 任务指望完成后执行的 callback，无返回值    |
| `handle`        |                   | 任务执行完成后执行的 callback，有返回值    |

- `thenRun`：如果 task-1 已经完成，则 task-2 直接在调用线程执行；如果 task-1 没有完成，则 task-2 与 task-1 在相同线程执行
- `thenRunAsync(Runnable action)` 方法的 task-2 在 `ForkJoin.commonPool` 运行
- `thenRunAsync(Runnable action,Executor executor)` 的 task-2 在自定义线程池运行

`thenAccept`, `thenApply` 不同重载方法的差异与此相同。

**示例**：task-1 已经完成，`thenRun()` 方法中 task-2 直接在 main 线程执行

```jade
ExecutorService e = Executors.newSingleThreadExecutor(r ->
        new Thread(r, "sole thread"));
CompletableFuture<?> f = CompletableFuture.runAsync(() -> {}, e);
f.join(); // f 完成
f.thenRun(() -> System.out.println("thenRun:\t"
        + Thread.currentThread().getName())); // 直接在 main 线程执行
f.thenRunAsync(() -> System.out.println("thenRunAsync:\t"
        + Thread.currentThread().getName()));
f.thenRunAsync(() -> System.out.println("thenRunAsync+e:\t"
        + Thread.currentThread().getName()), e);
e.shutdown();
```

```
thenRun:	main
thenRunAsync+e:	sole thread
thenRunAsync:	ForkJoinPool.commonPool-worker-1
```

**示例**：task-1 没有完成，则 task-2 与 task-1 在相同线程执行

```java
ExecutorService e = Executors.newSingleThreadExecutor(r ->
        new Thread(r, "sole thread"));
CompletableFuture<?> f = CompletableFuture
        .runAsync(() -> LockSupport.parkNanos((int) 1e9), e);
f.thenRun(() -> System.out.println("thenRun:\t" 
		+ Thread.currentThread().getName()));
f.thenRunAsync(() -> System.out.println("thenRunAsync:\t" 
		+ Thread.currentThread().getName()));
f.thenRunAsync(() -> System.out.println("thenRunAsync+e:\t" 
		+ Thread.currentThread().getName()), e);
LockSupport.parkNanos((int) 2e9);
e.shutdown();
```

```
thenRun:	sole thread
thenRunAsync+e:	sole thread
thenRunAsync:	ForkJoinPool.commonPool-worker-1
```

### thenRun

```java
public CompletionStage<Void> thenRun(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action,
                                          Executor executor);
```

⭐**任务串联**。完成 task-1 后，执行 task-2。task-2 不需要 task-1 的执行结果，task-2 也没有返回值。

适合执行不依赖于上一步计算结果的动作。

**示例**：两个简单任务，task-2 单纯打印到控制台

```java
CompletableFuture
    	.supplyAsync(() -> "Task Complete")
        .thenRun(() -> System.out.println("Next task starts now."));
```

**示例**：使用 `Thread.sleep` 模拟耗时任务

```java
long startTime = System.currentTimeMillis();

CompletableFuture<Void> cp1 = CompletableFuture.runAsync(() -> {
    try {
        //执行任务A
        Thread.sleep(600);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

CompletableFuture<Void> cp2 = cp1.thenRun(() -> { // 任务A结束后执行任务B
    try {
        //执行任务B
        Thread.sleep(400);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});
// get方法测试
System.out.println(cp2.get());
//模拟主程序耗时时间
Thread.sleep(600);
System.out.println("总共用时" + (System.currentTimeMillis() - startTime) + "ms");
```

```
null
总共用时1647ms
```

### thenAccept

```java
public CompletionStage<Void> thenAccept(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,
                                             Executor executor);
```

⭐**任务串联**。task-1 执行完成后，执行 task-2。task-2 需要 task-1 的执行结果，task-2 没有返回值。

该方法对执行 side-effect 非常有用，如日志记录、**更新用户界面**。

**示例**：

```java
CompletableFuture<Void> future = CompletableFuture
    	.supplyAsync(() -> "Hello, World!")
        .thenAccept(s -> System.out.println("Result:" + s));
future.join();
```

```java
Result:Hello, World!
```

> [!TIP]
>
> `theAccept` 和 `thenRun` 通常为 future 管线的最后一步。`thenAccept` 使用最后生成的值。`thenRun` 则不需要最后生成的值。

### thenApply

```java
public <U> CompletionStage<U> thenApply(Function<T,U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<T,U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<T,U> fn,
                                             Executor executor);
```

⭐**串联任务**。task-1 完成后执行 task-2。

task-2 需要 task-1 的返回值作为参数，task-2 执行结束后有返回值。

**示例**：

```java
CompletableFuture<String> cp1 = CompletableFuture
        .supplyAsync(() -> "dev")
        .thenApply((a) -> {
            if (Objects.equals(a, "dev")) {
                return "dev";
            }
            return "prod";
        });

System.out.println("当前环境为:" + cp1.get());
```

```
当前环境为:dev
```

**示例**：转换为大写

```java
CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
        .thenApply(String::toUpperCase);
assertEquals("MESSAGE", cf.getNow(null));
```

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

**示例**：异步方法可以使用自定义 `Executor` 执行 `CompletableFuture`。

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


**示例**：`thenApply` 串联

```java
CompletableFuture<String> welcomeText = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Jack";
        }).thenApply(name -> "Hello " + name)
          .thenApply(greeting -> greeting + ", Welcome to the new world");

System.out.println(welcomeText.join());
```

```
Hello Jack, Welcome to the new world
```

`thenApply` 与 `thenCompose` 的主要差别在于：`thenApply` 的 function 可以返回非 future 类型，而 `thenCompose` 的 function 直接返回 `CompletionStage` 类型。

**示例**：`thenApply` 和 `thenCompose`

```java
CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 2);

CompletableFuture<Integer> future2 = future1.thenApply(result -> result * 2);
CompletableFuture<Integer> future3 = future1.thenCompose(
        result -> CompletableFuture.supplyAsync(() -> result * 2));
```

- `thenApply`：当需要转换 `CompletableFuture` 的结果，但不需要启动另一个异步操作，适合**简单转换操作**。
- `thenCompose`：当使用一个 `CompletableFuture` 的结果启动另一个返回 `CompletableFuture` 的异步操作，适合串联多个异步操作。

### thenCompose

```java
public <U> CompletionStage<U> thenCompose(Function<T, CompletionStage<U>> fn);
public <U> CompletionStage<U> thenComposeAsync(Function<T, CompletionStage<U>> fn);
public <U> CompletionStage<U> thenComposeAsync(Function<T, CompletionStage<U>> fn,
                                               Executor executor);
```

⭐**任务串联**。当 task-1 完成，task-2 以 task-1 的返回值为参数。

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

## 任务组合

### AND

`runAfterBoth`, `thenAcceptBoth` 和 `thenCombine` 都表示当 task-1 和 task-2 都完成，再执行 task-3。差别在于：

- `runAfterBoth` 不把 task-1 和 task-2 的返回值作为参数，task-3 没有返回值
- `thenAcceptBoth` 将 task-1 和 task-2 的返回值作为参数，task-3 没有返回值
- `thenCombine` 将 task-1 和 task-2 的返回值作为参数，task-3 有返回值

#### runAfterBoth

```java
public CompletionStage<Void> runAfterBoth(CompletionStage<?> other,
                                          Runnable action);
public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other,
                                               Runnable action);
public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other,
                                               Runnable action,
                                               Executor executor);
```

⭐功能类似 `thenRun`，但是等待两个 futures 完成。

#### thenAcceptBoth

```java
public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<U> other,
                                                BiConsumer<T,U> action);
public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<U> other,
                                                     BiConsumer<T, U> action);
public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<U> other,
         BiConsumer<T,U> action,
         Executor executor);
```

⭐功能类似 `thenAccept`，但是等待两个 futures 完成。

#### thenCombine

```java
public <U,V> CompletionStage<V> thenCombine(CompletionStage<U> other,
     										BiFunction<T,U,V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<U> other,
                                                 BiFunction<T,U,V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<U> other,
                                                 BiFunction<T,U,V> fn,
                                                 Executor executor);
```

⭐**任务合并**。当 task-1 和 task-2 执行结束，执行 task-3。task-3 需要 task-1 和 task-2 的返回值，task-3 执行也有返回值。

适合用来合并两个独立异步计算的结果。

**示例**：

```java
//创建线程池
ExecutorService executorService = Executors.newFixedThreadPool(10);
//开启异步任务1
CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
    System.out.println("异步任务1，当前线程是：" + Thread.currentThread().threadId());
    int result = 1 + 1;
    System.out.println("异步任务1结束");
    return result;
}, executorService);

//开启异步任务2
CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
    System.out.println("异步任务2，当前线程是：" + Thread.currentThread().threadId());
    int result = 1 + 1;
    System.out.println("异步任务2结束");
    return result;
}, executorService);

//任务组合
CompletableFuture<Integer> task3 = task.thenCombineAsync(task2, (f1, f2) -> {
    System.out.println("执行任务3，当前线程是：" + Thread.currentThread().threadId());
    System.out.println("任务1返回值：" + f1);
    System.out.println("任务2返回值：" + f2);
    return f1 + f2;
}, executorService);

Integer res = task3.get();
System.out.println("最终结果：" + res);
```

```
异步任务2，当前线程是：45
异步任务2结束
异步任务1，当前线程是：44
异步任务1结束
执行任务3，当前线程是：46
任务1返回值：2
任务2返回值：2
最终结果：4
```

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

### OR

`runAfterEither`, `acceptEither` 和 `applyToEither` 都表示当 task-1 和 task-2 其中一个完成，就执行 task-3。区别在于：

- `runAfterEither` 不把 task1 或 task-2 的返回值作为参数，task-3 没有返回值
- `acceptEither` 将 task-1 或 task-2 的返回值作为参数，task-3 没有返回值
- `applyToEither` 将 task-1 或 task-2 的返回值作为参数，task-3 有返回值

#### runAfterEither

```java
public CompletionStage<Void> runAfterEither(CompletionStage<?> other,
                                            Runnable action);
public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other,
         				                    Runnable action);
public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other,
                                            Runnable action,
                                            Executor executor);
```

⭐当前 stage 或另一个 stage，任意一个完成触发指定 `Runnable`。

#### acceptEither

```java
public CompletionStage<Void> acceptEither(CompletionStage<T> other,
         Consumer<T> action);
public CompletionStage<Void> acceptEitherAsync(CompletionStage<T> other,
         Consumer<T> action);
public CompletionStage<Void> acceptEitherAsync(CompletionStage<T> other,
         Consumer<T> action,
         Executor executor);
```

⭐当前 stage 或另一个 stage，任意一个完成触发指定 action。

示例：

```java
//创建线程池
ExecutorService executorService = Executors.newFixedThreadPool(10);
//开启异步任务1
CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
    System.out.println("异步任务1，当前线程是：" + Thread.currentThread().threadId());

    int result = 1 + 1;
    System.out.println("异步任务1结束");
    return result;
}, executorService);

//开启异步任务2
CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
    System.out.println("异步任务2，当前线程是：" + Thread.currentThread().threadId());
    int result = 1 + 2;
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("异步任务2结束");
    return result;
}, executorService);

//任务组合
task.acceptEitherAsync(task2, (res) -> {
    System.out.println("执行任务3，当前线程是：" + Thread.currentThread().threadId());
    System.out.println("上一个任务的结果为：" + res);
}, executorService);
```

```
异步任务2，当前线程是：45
异步任务1，当前线程是：44
异步任务1结束
执行任务3，当前线程是：46
上一个任务的结果为：2
```

**WARNING** 如果将上面线程池大小该为 1

```java
ExecutorService executorService = Executors.newFixedThreadPool(1);
```

执行结果如下：

```
异步任务1，当前线程是：44
异步任务1结束
异步任务2，当前线程是：44
```

任务3 直接被丢弃了。

#### applyToEither

```java
public <U> CompletionStage<U> applyToEither(CompletionStage<T> other,
         Function<T, U> fn);
public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<T> other,
         Function<T, U> fn);
public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<T> other,
         Function<T, U> fn,
         Executor executor);
```

⭐当前 stage 和另一个 stage，对先完成的 stage 应用指定操作。

### 多任务组合

- `allOf`，等待所有任务完成
- `anyOf`，等待任意一个任务完成

#### allOf：并行任务

```java
public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs);
```

并行执行任务对提供程序的性能和响应能力至关重要。

`allOf()` 返回一个 `CompletableFuture`，当指定的所有 `CompletableFuture` 完成，它才完成：

- 任意一个 `CompletableFuture` 出现异常，返回 `CompletableFuture` 也返回异常，异常信息通过 `CompletionException` 查询
- 如果不提供 `CompletableFuture`，返回的 `CompletableFuture` 的值为 null。

该方法可用于等待一组独立的 `CompletableFuture` 完成，然后继续程序，例如：

```java
CompletableFuture.allOf(c1, c2, c3).join();
```

当有多个**独立的并行任务**，`allOf()` 可以整合结果：

- 以 `CompletableFuture` 数组为参数
- 返回一个 `CompletableFuture`，当提供的所有 futures 完成，它才完成
- 同步多个任务的便捷方法

`allOf` 的缺点是不会合并所有 futures 的结果，而是需要手动从各个 features 查询结果。

采用 `Stream` API可以简化该过程：

```java
CompletableFuture<String> future1 = CompletableFuture
        .supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture
        .supplyAsync(() -> "Beautiful");
CompletableFuture<String> future3 = CompletableFuture
        .supplyAsync(() -> "World");
String collect = Stream.of(future1, future2, future3)
        .map(CompletableFuture::join)
        .collect(Collectors.joining(" "));
assertEquals("Hello Beautiful World", collect);
```
**示例**：

```java
//创建线程池
ExecutorService executorService = Executors.newFixedThreadPool(10);
//开启异步任务1
CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
    System.out.println("异步任务1，当前线程是：" + Thread.currentThread().threadId());
    int result = 1 + 1;
    System.out.println("异步任务1结束");
    return result;
}, executorService);

//开启异步任务2
CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
    System.out.println("异步任务2，当前线程是：" + Thread.currentThread().threadId());
    int result = 1 + 2;
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("异步任务2结束");
    return result;
}, executorService);

//开启异步任务3
CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> {
    System.out.println("异步任务3，当前线程是：" + Thread.currentThread().threadId());
    int result = 1 + 3;
    try {
        Thread.sleep(4000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("异步任务3结束");
    return result;
}, executorService);

//任务组合
CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2, task3);

//等待所有任务完成
allOf.get();
//获取任务的返回结果
System.out.println("task1结果为：" + task1.get());
System.out.println("task2结果为：" + task2.get());
System.out.println("task3结果为：" + task3.get());
```

```
异步任务2，当前线程是：45
异步任务3，当前线程是：46
异步任务1，当前线程是：44
异步任务1结束
异步任务2结束
异步任务3结束
task1结果为：2
task2结果为：3
task3结果为：4
```

#### anyOf

```java
public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs);
```
`anyOf` 返回最先完成任务的结果。

**示例**：

```java
//创建线程池
ExecutorService executorService = Executors.newFixedThreadPool(10);
//开启异步任务1
CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
    int result = 1 + 1;
    return result;
}, executorService);

//开启异步任务2
CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
    int result = 1 + 2;
    return result;
}, executorService);

//开启异步任务3
CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> {
    int result = 1 + 3;
    return result;
}, executorService);

//任务组合
CompletableFuture<Object> anyOf = CompletableFuture.anyOf(task, task2, task3);
//只要有一个有任务完成
Object o = anyOf.get();
System.out.println("完成的任务的结果：" + o);
```

```
完成的任务的结果：2
```

## 取消任务

```java
public boolean cancel(boolean mayInterruptIfRunning);
```

如果 task 还没完成，则使用 `CancellationException` 完成。依赖该 task 的其它 `CompletableFuture` 也以  `CompletionException` 完成。

在 `CompletableFuture` 中 `mayInterruptIfRunning` 参数无效。`cancel()` 操作等价于 `completeExceptionally(new CancellationException())`。

**示例**：

```java
CompletableFuture<String> cf = CompletableFuture
        .completedFuture("message")
        .thenApplyAsync(String::toUpperCase,
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
CompletableFuture<String> cf2 = cf.exceptionally(throwable -> "canceled message");
assertTrue(cf.cancel(true));
assertTrue(cf.isCompletedExceptionally());
assertEquals("canceled message", cf2.join());
```

## 完成或异常

### 获取结果

`CompletableFuture` 提供了 4 种获取结果的方式：

```java
public T get() 
    throws InterruptedException, ExecutionException;
public T get(long timeout, TimeUnit unit)
    throws InterruptedException, ExecutionException, TimeoutException;
public T getNow(T valueIfAbsent);
public T join();
```

- `get()` 在 `Future` 接口中定义，阻塞并获取结果；
- `get(timeout, unit)` 提供超时功能，如果在指定时间内未完成抛出 `TimeoutException`；
- `getNow()` 立即获取结果，不阻塞。如果计算完成，则返回结果；如果计算出错，则返回异常；如果未完成，则返回 `valueIfAbsent` 默认值；
- `join()` 阻塞并获取结果，但不抛出异常。

**示例**：`getNow` 不会抛出异常

```java
CompletableFuture<String> cp1 = CompletableFuture.supplyAsync(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "商品A";
});

// getNow方法测试
System.out.println(cp1.getNow("商品B"));
```

```
商品B
```

**示例**：`join()` 不抛出异常，但是执行出错会抛出 `CompletionException`

```java
CompletableFuture<Integer> cp2 = CompletableFuture.supplyAsync((() -> 1 / 0));
System.out.println(cp2.join());
```

```
java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
```

**示例**：`get()` 方法签名就抛出了异常，下面抛出 `ExecutionException`

```java
CompletableFuture<Integer> cp3 = CompletableFuture.supplyAsync((() -> 1 / 0));
System.out.println(cp3.get());
```

```
java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
```


### whenComplete

```java
public CompletionStage<T> whenComplete(BiConsumer<T, Throwable> action);
public CompletionStage<T> whenCompleteAsync(BiConsumer<T, Throwable> action);
public CompletionStage<T> whenCompleteAsync(BiConsumer<T, Throwable> action,
         Executor executor);
```

当 task-1 完成，执行 task-2。task-2 以 task-1 的返回值与异常作为参数：

- 当 task-1 正常终止，task-2 返回结果和 task-1 一样，异常为 `null`
- 当 task-1 异常终止，task-2 返回结果为 `null`，异常为 task-1 抛出的异常

与 `handle` 不同的是，该方法不是为转换结果设计，因此所提供的操作不应抛出异常。但是如果抛出了异常：

- 如果 task-1 正常终止，但是提供的操作抛出异常，则返回的 task-2 以操作的 exception 异常终止
- 如果 task-1 异常终止，并且提供的操作也抛出异常，则返回的 task-2 以 task-1 的 exception 异常终止

**示例**：`whenComplete`

```java
CompletableFuture<Double> future = CompletableFuture.supplyAsync(() -> {
    if (Math.random() < 0.5) {
        throw new RuntimeException("出错了");
    }
    System.out.println("正常结束");
    return 0.11;

}).whenComplete((aDouble, throwable) -> {
    if (aDouble == null) {
        System.out.println("whenComplete aDouble is null");
    } else {
        System.out.println("whenComplete aDouble is " + aDouble);
    }
    if (throwable == null) {
        System.out.println("whenComplete throwable is null");
    } else {
        System.out.println("whenComplete throwable is " + throwable.getMessage());
    }
});
System.out.println("最终返回的结果 = " + future.get());
```

根据 `Math.random()` 的输出有两种可能结果，出现异常：

```
whenComplete aDouble is null
whenComplete throwable is java.lang.RuntimeException: 出错了

java.util.concurrent.ExecutionException: java.lang.RuntimeException: 出错了
```

没有异常：

```
正常结束
whenComplete aDouble is 0.11
whenComplete throwable is null
最终返回的结果 = 0.11
```

### exceptionally

```java
public CompletionStage<T> exceptionally(Function<Throwable, T> fn);
public default CompletionStage<T> exceptionallyAsync(Function<Throwable, T> fn);
public default CompletionStage<T> exceptionallyAsync(Function<Throwable, T> fn, 
                                                     Executor executor);
```

⭐**处理异常**：

- 若 task-1 出现异常，task-2 执行的`Function` 以异常为参数，用于处理异常
- 若 task-1 正常完成，task-2 返回与 task-1 相同的值

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

**示例**：处理多个 futures 的异常

```java
```



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

手动以异常完成。


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

对未指定 `Executor` 异步方法的默认 `Executor`。如果支持多个线程，则默认使用 `ForkJoinPool.commonPool()`，否则对每个异步任何使用一个线程的`Executor`。

可以扩展 `CompletableFuture` 并覆盖该方法：

```java
public class Flow<T> extends CompletableFuture<T> {

    private static final Executor executor =
            Executors.newFixedThreadPool(10);

    @Override
    public Executor defaultExecutor() {
        return executor;
    }
}
```

这样，所有操作默认就在自定义线程池中执行。

### newIncompleteFuture

```java
public <U> CompletableFuture<U> newIncompleteFuture() {
    return new CompletableFuture<U>();
}
```

又称为虚拟构造函数，用于创建相同类型的 `CompletableFuture`。

在继承 `CompletableFuture` 时非常有用，因为在所有返回新 `CompletionStage` 的方法都需要。

### copy

```java
public CompletableFuture<T> copy();
```

返回一个新的 `CompletableFuture`，当它正常完成时，返回值与当前 future 相同；如果原 future 抛出异常，返回的 future 抛出 `CompletionException `，包含原始异常的信息。

该方法可以复制防御。

### join

```java
public T join();
```

类似 `get()`，阻塞并返回结果。不过在出错时抛出的是 unchecked 异常，便于在 `Stream` 中使用。

### completedStage & failedStage

```java
public static <U> CompletionStage<U> completedStage(U value);
public static <U> CompletionStage<U> failedStage(Throwable ex);
```

`completedStage` 返回正常终止的 stage；`failedStage` 返回失败终止的 stage。

### delayedExecutor

```java
public static Executor delayedExecutor(long delay, TimeUnit unit);
public static Executor delayedExecutor(long delay, TimeUnit unit,
                                       Executor executor);
```

引入延迟功能。每个延迟从调用 executor 的 `execute` 方法开始计算。

### orTimeout

```java
public CompletableFuture<T> orTimeout(long timeout, TimeUnit unit);
```

超时限制。如果在指定 timeout 前没有正常结束，则抛出 `TimeoutException`，如果执行结束，则返回计算值。

### completeOnTimeout

```java
public CompletableFuture<T> completeOnTimeout(T value, long timeout,
                                              TimeUnit unit);
```

超时限制。如果在指定 timeout 前没有正常结束，则以指定 `value` 返回。

### failedFuture

```java
public static <U> CompletableFuture<U> failedFuture(Throwable ex);
```

创建一个以指定异常完成的 future。

可用于测试或模拟异步流程中的失败条件。

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

## 注意事项

### 异常

`CompletableFuture` 在获取返回值时才开始执行，才会抛出异常：

```java
CompletableFuture<Double> future = CompletableFuture.supplyAsync(() -> {
    if (1 == 1) {
        throw new RuntimeException("出错了");
    }
    System.out.println("a");
    return 0.11;
});
Thread.sleep(1000);
//如果不加 get()方法这一行，看不到异常信息
//future.get();
```

### 线程池

`CompletableFuture` 默认采用 `ForkJoinPool.commonPool()` 线程池，该线程池旨在有效管理线程，适合各种任务。但是，使用 commonPool 可能导致性能问题。例如，如果同时提交许多任务，可能导致线程争用、增加延迟：

- 线程管理：common-pool 根据 workload 动态调整线程数。但是，在高通量应用中，这可能不是最好选择；
- 阻塞任务：阻塞任务会减少整体通量，导致任务延迟
- 延迟和通量：common-pool 的大小会影响延迟和通量

为了获得更好的性能，通常建议使用自定义 executor。

## 参考

- https://pwrteams.com/content-hub/blog/async-programming-and-completablefuture-in-java
- https://javabetter.cn/thread/completable-future.html
