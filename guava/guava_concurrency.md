# Guava Concurrency

- [Guava Concurrency](#guava-concurrency)
  - [ListenableFuture](#listenablefuture)
    - [接口](#接口)
    - [添加 callbacks](#添加-callbacks)
    - [创建](#创建)
    - [应用](#应用)

2024-11-08
@author Jiawei Mao

***

## MoreExecutors

Guava 提供了几个 `ExecutorService` 的几个实现，实现类无法直接实例化，而是使用 `MoreExecutors` 类创建。

### directExecutor

有时我们希望根据某些条件，在当前线程或线程池中运行任务。此时可以使用单个 `Executor` 接口，只需切换实现即可。虽然实现一个在当前线程运行任务的 `Executor` 或 `ExecutorService` 实现并不难，但仍然需要一些代码。Guava 提供了该实现。

**示例：** 在同一个线程中执行任务

任务会 sleep 500 毫秒，导致阻塞当前线程，在执行调用完成后立即获得结果：

```java
Executor executor = MoreExecutors.directExecutor();

AtomicBoolean executed = new AtomicBoolean();

executor.execute(() -> {
    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    executed.set(true);
});

assertTrue(executed.get());
```

`directExecutor()` 返回的是一个 static singleton，所以该方法没有创建对象的开销。

`MoreExecutors.newDirectExecutorService()` 则在每次调用时创建一个 `ExecutorService`，开销相对较大，因此推荐在必要在使用该方法。

### 退出 ExecutorService

当线程池正在运行任务时关闭虚拟机，即使有取消机制，也不能保证 `ExecutorService` 关闭后任务会停止执行。这可能导致 JVM 无限期挂起，任务继续执行。

为了解决该问题，Guava 基于与 JVM 一起终止的守护线程引入了一系列退出 `ExecutorService` 的功能。

### Listening



## ListenableFuture

Guava 提供的 `ListenableFuture` 扩展了 JDK `Future` 接口，支持 callbacks。

Guava 建议在所有代码中始终使用 `ListenableFuture` 代替 `Future`。

`ListenableFuture` 可以注册 callbacks，在计算完成后立即执行。这个简单的功能可以支持许多 `Future` 接口无法完成的操作。

`ListenableFuture` 添加了一个 `addListener(Runnable, Executor)` 方法，指定当这个 `Future` 完成，在指定 `Executor` 上运行指定 `Runnable`。

### 添加 callbacks

推荐  `Futures.addCallback(ListenableFuture<V>, FutureCallback<V>, Executor)` 方法。`FutureCallback<V>` 实现两个方法：

- `onSuccess(V)`, 如果 `Future` 执行成功，根据其执行结果执行的操作
- `onFailure(Throwable)`, 如果 `Future` 执行失败，根据失败结果执行的操作

### 创建

与 JDK 的 `ExecutorService.submit(Callable)` 方法对应，Guava 提供了 `ListeningExecutorService` 接口，该接口在 `ExecutorService` 返回常规 `Future` 的地方返回 `ListenableFuture`。

1. 使用 `MoreExecutors.listeningDecorator(ExecutorService)` 将 `ExecutorService` 转换为 `ListeningExecutorService`

```java
ListeningExecutorService service = 
    	MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
```

2. 向 `ListeningExecutorService` 提交任务，返回 `ListenableFuture`

```java
ListenableFuture<Integer> asyncTask = lExecService.submit(() -> {
    TimeUnit.MILLISECONDS.sleep(500); // 耗时任务
    return 5;
});
```

3. 添加 callbacks

```java
Executor listeningExecutor = Executors.newSingleThreadExecutor();

ListenableFuture<Integer> asyncTask = new ListenableFutureService().succeedingTask()
Futures.addCallback(asyncTask, new FutureCallback<Integer>() {
    @Override
    public void onSuccess(Integer result) {
        // do on success
    }

    @Override
    public void onFailure(Throwable t) {
        // do on failure
    }
}, listeningExecutor);
```

对基于 `FutureTask` 的 API，Guava 提供了 `ListenableFutureTask.create(Callable<V>)` 和 `ListenableFutureTask.create(Runnable, V)`。与 JDK  不同的是，`ListenableFutureTask` 不是用来直接扩展。

```java
// old api
public FutureTask<String> fetchConfigTask(String configKey) {
    return new FutureTask<>(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return String.format("%s.%d", configKey, new Random().nextInt(Integer.MAX_VALUE));
    });
}

// new api
public ListenableFutureTask<String> fetchConfigListenableTask(String configKey) {
    return ListenableFutureTask.create(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return String.format("%s.%d", configKey, new Random().nextInt(Integer.MAX_VALUE));
    });
}
```

如果更喜欢继承，可以扩展 `AbstractFuture<V>` 或直接使用 `SettableFuture`。

如果必须将其它 API 提供的 `Future` 转换为 `ListenableFuture`，此时别无选择，只能使用重量级的 `JdkFutureAdapters.listenInPoolThread(Future)` 将 `Future` 转换为 `ListenableFuture`。如果可能，最好修改原始代码以返回 `ListenableFuture`。

### 应用

使用 `ListenableFuture` 的最重要的原因是它可以实现复杂的异步操作链，

```java
ListenableFuture<RowKey> rowKeyFuture = indexService.lookUp(query);
AsyncFunction<RowKey, QueryResult> queryFunction =
    new AsyncFunction<RowKey, QueryResult>() {
        public ListenableFuture<QueryResult> apply(RowKey rowKey) {
        	return dataService.read(rowKey);
        }
    };
ListenableFuture<QueryResult> queryFuture =
    Futures.transformAsync(rowKeyFuture, queryFunction, queryExecutor);
```

使用 `ListenableFuture` 可以实现许多 `Future` 不支持的操作。不同的操作可以由不同 executor 执行，并且一个 `ListenableFuture` 可以添加多个 callbacks。

| 方法                                                         | 说明                                                         | See also                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`transformAsync(ListenableFuture, AsyncFunction, Executor)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#transformAsync-com.google.common.util.concurrent.ListenableFuture-com.google.common.util.concurrent.AsyncFunction-java.util.concurrent.Executor-)`*` | 返回 `ListenableFuture`，包含将指定`AsyncFunction` 应用于指定 `ListenableFuture` 结果的结果 | [`transformAsync(ListenableFuture, AsyncFunction)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#transformAsync-com.google.common.util.concurrent.ListenableFuture-com.google.common.util.concurrent.AsyncFunction-) |
| [`transform(ListenableFuture, Function, Executor)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#transform-com.google.common.util.concurrent.ListenableFuture-com.google.common.base.Function-java.util.concurrent.Executor-) | 返回的 `ListenableFuture`，包含将指定`Function` 应用于指定 `ListenableFuture` 结果的结果 | [`transform(ListenableFuture, Function)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#transform-com.google.common.util.concurrent.ListenableFuture-com.google.common.base.Function-) |
| `allAsList(Iterable<ListenableFuture<V>>)`                   | 返回的 `ListenableFuture` 包含一个 list，该 list 按序包含每个输入 future 的及诶过，如果输入 futures 任意一个取消或失败，该 future 取消或失败 | [`allAsList(ListenableFuture...)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#allAsList-com.google.common.util.concurrent.ListenableFuture...-) |
| [`successfulAsList(Iterable>)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#successfulAsList-java.lang.Iterable-) | 返回的 `ListenableFuture` 包含一个 list，该 list 按序包含每个输入 future 的值，失败或取消的 future 的值为 null | [`successfulAsList(ListenableFuture...)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#successfulAsList-com.google.common.util.concurrent.ListenableFuture...-) |

> `*` [`AsyncFunction`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/AsyncFunction.html) 提供的 `ListenableFuture<B> apply(A input)` 方法可用于异步转换值。

```java
List<ListenableFuture<QueryResult>> queries;
// The queries go to all different data centers, but we want to wait until they're all done or failed.

ListenableFuture<List<QueryResult>> successfulQueries = Futures.successfulAsList(queries);

Futures.addCallback(successfulQueries, callbackOnSuccessfulQueries);
```

#### 合并多个异步任务的结果

调用多个异步任务并收集它们的结果，通常称为 fan-in 操作。

Guava 为我们提供了两种方法来实现这一点。根据自己的需求进行选择。假设需要协调以下异步任务：

```java
ListenableFuture<String> task1 = service.fetchConfig("config.0");
ListenableFuture<String> task2 = service.fetchConfig("config.1");
ListenableFuture<String> task3 = service.fetchConfig("config.2");
```

1. 使用 `Futures.allAsList()`

创建一个  `ListenableFuture`：如果所有 `Future` 都执行成功，则按照提供 `Future` 的顺序收集所有 `Future` 的结果；如果取消该 `Future`，则取消包含的所有 `Future`；如果所含的任何 `Future` 失败或取消，它也随之失败或取消。

```java
ListenableFuture<List<String>> configsTask = Futures.allAsList(task1, task2, task3);
Futures.addCallback(configsTask, new FutureCallback<List<String>>() {
    @Override
    public void onSuccess(@Nullable List<String> configResults) {
        // do on all futures success
    }

    @Override
    public void onFailure(Throwable t) {
        // handle on at least one failure
    }
}, someExecutor);
```

2. 使用 `Futures.successfulAsList()`

如果不管异步任务是否成功，收集所有任务结果，则使用 `Futures.successfulAsList`。它将返回一个 list，其结果与传入参数的熟悉怒一致，失败任务的结果为 `null`：

```java
ListenableFuture<List<String>> configsTask = Futures.successfulAsList(task1, task2, task3);
Futures.addCallback(configsTask, new FutureCallback<List<String>>() {
    @Override
    public void onSuccess(@Nullable List<String> configResults) {
        // handle results. If task2 failed, then configResults.get(1) == null
    }

    @Override
    public void onFailure(Throwable t) {
        // handle failure
    }
}, listeningExecutor);
```

如果 `Future` 在成功时返回 `null`，则该方法无法区分任务是成功还是失败，需要谨慎。

#### fan-in Combiners

如果不同 `Future` 返回结果类型不同，`Futures.allAsList` 和 `Futures.successfulAsList` 可能不够用。此时可以与 combiners 合并使用。Guava 也提供了两个版本：

- `Futures.whenAllSucceed()`
- `Futures.whenAllComplete()`

```java
ListenableFuture<Integer> cartIdTask = service.getCartId();
ListenableFuture<String> customerNameTask = service.getCustomerName();
ListenableFuture<List<String>> cartItemsTask = service.getCartItems();

ListenableFuture<CartInfo> cartInfoTask = Futures.whenAllSucceed(cartIdTask, customerNameTask, cartItemsTask)
    .call(() -> {
        // 合并策略
        int cartId = Futures.getDone(cartIdTask);
        String customerName = Futures.getDone(customerNameTask);
        List<String> cartItems = Futures.getDone(cartItemsTask);
        return new CartInfo(cartId, customerName, cartItems);
    }, someExecutor);

Futures.addCallback(cartInfoTask, new FutureCallback<CartInfo>() {
    @Override
    public void onSuccess(@Nullable CartInfo result) {
        //handle on all success and combination success
    }

    @Override
    public void onFailure(Throwable t) {
        //handle on either task fail or combination failed
    }
}, listeningExecService);
```

#### Transformations

任务执行成功后，将 `Future` 的结果转换为其它类型。Guava 提供了两个方法：`Futures.transform()` 和 `Futures.lazyTransform()`.

下面演示如何使用 `Futures.transform()` 转换 `Future` 的结果。只要转换的计算量不大，都可以使用：

```java
ListenableFuture<List<String>> cartItemsTask = service.getCartItems();

Function<List<String>, Integer> itemCountFunc = cartItems -> {
    assertNotNull(cartItems);
    return cartItems.size();
};

ListenableFuture<Integer> itemCountTask = Futures.transform(cartItemsTask, itemCountFunc, listenExecService);
```

也可以使用 `Futures.lazyTransform()` 将转换函数应用于 `Future`，返回 `Future` 而非 `ListenableFuture`。

#### Chaining Futures

在 `Future` 中调用其它 `Future`，Guava 提供了 `async()`，可以安全地串联 `Future`。

`Futures.submitAsync()` 在 `Callable` 中调用 `Future`：

```java
AsyncCallable<String> asyncConfigTask = () -> {
    // 调用另一个 Future
    ListenableFuture<String> configTask = service.fetchConfig("config.a");
    TimeUnit.MILLISECONDS.sleep(500); / /some long running task
    return configTask;
};

ListenableFuture<String> configTask = Futures.submitAsync(asyncConfigTask, executor);
```

如果需要将一个 `Future` 的结果作为另一个 `Future` 的输入，可以使用 `Futures.transformAsync`：

```java
ListenableFuture<String> usernameTask = service.generateUsername("john");
AsyncFunction<String, String> passwordFunc = username -> {
    ListenableFuture<String> generatePasswordTask = 
        	service.generatePassword(username);
    TimeUnit.MILLISECONDS.sleep(500); // some long running task
    return generatePasswordTask;
};

ListenableFuture<String> passwordTask = Futures.transformAsync(usernameTask,
                                                   passwordFunc, executor);
```

### withTimeout

```java
static <V> ListenableFuture<V> withTimeout(
      ListenableFuture<V> delegate, Duration time, 
    	ScheduledExecutorService scheduledExecutor);
static <V> ListenableFuture<V> withTimeout(
      ListenableFuture<V> delegate,
      long time,
      TimeUnit unit,
      ScheduledExecutorService scheduledExecutor)
```



### 注意事项

#### working vs. listening executors

在使用 Guava futures 时，要注意区分 working executor 和 listening executor。假设有一个获取配置的异步 task：

```java
public ListenableFuture<String> fetchConfig(String configKey) {
    return lExecService.submit(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return String.format("%s.%d", configKey, new Random().nextInt(Integer.MAX_VALUE));
    });
}
```

为上面的 future 添加一个 listener:

```java
ListenableFuture<String> configsTask = service.fetchConfig("config.0");
Futures.addCallback(configsTask, someListener, listeningExecutor);
```

这里，`lExecService` 负责执行异步 task，`listeningExecutor` 负责执行 listener。

应该将这两种 executor 分开，以避免 listener 和 workers 竞争同一线程资源的情况。共享同一个 executor 可能导致 cpu 密集型的 task 占据线程使得 listener 无资源可用，或者编码糟糕的 listener 任务阻塞重要的 task。

#### directExecutor 使用要点

虽然可以在单元测试中使用 `MoreExecutors.directExecutor()` 和 `MoreExecutors.newDirectExecutorService()` 更轻松地处理异步执行，但在生产代码中使用要谨慎。

使用这两个方法获得的 executors，提交给它的任务都在当前线程执行。如果当前执行需要高吞吐量，就会导致严重问题。例如，使用 `directExecutor` 并在 UI 线程提交 heavy-task，从而阻塞 UI 线程。

另外，当前线程的 listeners 还可能拖慢所有其它 listeners。因为 Guava 在各自的 `Executor` 中以 while 循环执行所有 listeners，但 `directExecutor` 导致 listener 与 while 循环在相同线程运行。

#### 避免 Future 嵌套

使用 chaining-Future 时，注意不要从一个 `Future` 中调用另一个 `Future`，这样会创建嵌套的 `Future`：

```java
public ListenableFuture<String> generatePassword(String username) {
    return lExecService.submit(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return username + "123";
    });
}

String firstName = "john";
ListenableFuture<ListenableFuture<String>> badTask = lExecService.submit(() -> {
    final String username = firstName.replaceAll("[^a-zA-Z]+", "")
        .concat("@service.com");
    return generatePassword(username);// 调用另一个 Future
});
```

出现 `ListenableFuture<ListenableFuture<V>>`，就说明出现了嵌套。嵌套 `Future` 会导致外部 `Future` 的取消操作无法传递到内部 `Future` 等情况。

为了避免该情况，Guava 的所有 future 处理方法都有一个 `*Async` 版本，可以安全地将这种嵌套解开：

- [`transform(ListenableFuture, Function, Executor)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#transform-com.google.common.util.concurrent.ListenableFuture-com.google.common.base.Function-java.util.concurrent.Executor-)
- [`transformAsync(ListenableFuture, AsyncFunction, Executor)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#transformAsync-com.google.common.util.concurrent.ListenableFuture-com.google.common.util.concurrent.AsyncFunction-java.util.concurrent.Executor-)
- [`ExecutorService.submit(Callable)`](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html#submit-java.util.concurrent.Callable-)
- [`submitAsync(AsyncCallable, Executor)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#submitAsync-com.google.common.util.concurrent.AsyncCallable-java.util.concurrent.Executor-)

### SettableFuture

`ListenableFuture` 的子类，其结果可以通过 `set(Object)`, `setException(Throwable)` 或 `setFuture(ListenableFuture)` 设置。也可以被取消。

对无法通过 `ListeningExecutorService`, `Futures` 方法以及 `ListenableFutureTask` 实现的任务，推荐使用 `SettableFuture` 实现 `ListenableFuture`。如果你的需求比 `SettableFuture` 更复杂，则可以扩展 `AbstractFuture`。

### FluentFuture

支持 fluent 操作链的 `ListenableFuture`。例如：

```java
ListenableFuture<Boolean> adminIsLoggedIn = 
    FluentFuture.from(usersDatabase.getAdminUser())
    	.transform(User::getId, directExecutor())
    	.transform(ActivityService::isLoggedIn, threadPool)
    	.catching(RpcException.class, e -> false, directExecutor());
```

如果想要一个像 `FluentFuture` 的类，但需要额外的方法，建议自定义 `ListenableFuture` 子类，并使用类似 `from` 的方法来适配现有 `ListenableFuture`，

## 参考

- https://www.baeldung.com/guava-futures-listenablefuture
- https://github.com/google/guava/wiki/ListenableFutureExplained
