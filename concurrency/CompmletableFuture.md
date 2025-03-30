# CompletableFuture

## 简介

Java 5 引入 `Future` 表示异步计算结果，但它没有提供任何合并结果或处理错误的方法。使用 `Future` 获得异步执行结果时，要么调用阻塞方法 `get()`，要么轮询 `isDone`，这两种方法都会使主线程等待。

java 8 引入 `CompletableFuture`，除了 `Future` 接口，它还实现了 `CompletionStage` 接口。 该接口定义了与其它异步计算步骤合并的协议。另外，`CompletableFuture` 还是一个完成的框架，它提供了大约 50 个方法，用来组合、合并和执行异步计算步骤以及处理错误。

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





