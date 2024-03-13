# 任务返回值

2024-03-11
@author Jiawei Mao

## 简介

`Executor` 框架的优势之一是可以返回并发任务的结果。Java API 通过下面两个接口实现该功能：

- `Callable`： 该接口只有一个 `call()` 方法，在其中实现任务逻辑。`Callable` 为参数化接口，参数类型为 `call()` 方法的返回类型；
- `Future`：该接口在 `Callable` 的基础上提供了获取结果和管理状态的方法。
  - 控制任务的状态：可以取消任务和检查任务是否已经完成。使用 `isDone()` 检查任务是否完成。
  - 通过 `get()` 方法获取返回的结果，如果 `get()` 方法在等待结果时线程中断，抛出 `InterruptedException`。如果 `call()` 抛出异常，`get()` 随之抛出 `ExecutionException`。

提交 `Callable` 任务：

```java
ExecutorService executor = Executors.newFixedThreadPool();
Callable<V> task = ...;
Future<V> result = executor.submit(task);
```

提交任务返回 `Future` 实例，因为任务执行完才有结果，顾名思义，未来才出现。`Future` 接口源码如下：

```java
public interface Future<V> {

    boolean cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();

    boolean isDone();

    V get() throws InterruptedException, ExecutionException;

    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

`Future` 的 `get()` 方法会阻塞直到任务执行完，或者达到设置的时间点。

- 如果顺利执行完，`get()` 方法返回结果;
- 如果出错，抛出 `ExecutionException`;
- 如果超时，则抛出 `TimeoutException`。

`cancel` 方法尝试取消任务的执行，如果任务没在运行，调用无任何效果。如果设置 `mayInterruptIfRunning` 为 `true`，则打断线程执行。

## 示例

首先创建一个实现 `Callable` 接口的 `FactorialCalculator` 类，用于计算指定数的阶乘：

```java
public class FactorialCalculator implements Callable<Integer> {

    private final Integer number;

    public FactorialCalculator(Integer number) {
        this.number = number;
    }

    @Override
    public Integer call() throws Exception {
        int result = 1;
        if (number == 0 || number == 1) {
            return 1;
        } else {
            for (int i = 2; i <= number; i++) {
                result *= i;
                TimeUnit.MILLISECONDS.sleep(20);
            }
        }
        System.out.printf("%s: %d\n", Thread.currentThread().getName(), result);
        return result;
    }
}
```

测试该类：

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class CallableEx {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<Future<Integer>> resultList = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            int number = random.nextInt(10);
            // 创建新的任务
            FactorialCalculator calculator = new FactorialCalculator(number);
            // 提交任务，返回 Future，Future 可用于管理任务
            Future<Integer> result = executor.submit(calculator);
            resultList.add(result);
        }

        do {
            // 查看完成的任务数
            System.out.printf("Main: Number of Completed Tasks: %d\n", executor.getCompletedTaskCount());

            for (int i = 0; i < resultList.size(); i++) {
                Future<Integer> future = resultList.get(i);
                System.out.printf("Main: Task %d: %s\n", i, future.isDone());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (executor.getCompletedTaskCount() < resultList.size());

        // 到这里，所有任务已完成
        System.out.printf("Main: Results\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Integer> result = resultList.get(i);
            try {
                Integer number = result.get(); // get() 阻塞直到 call() 执行完
                System.out.printf("Main: Task %d: %d\n", i, number);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        // 关闭
        executor.shutdown();
    }
}
```

通过 `submit()` 方法将 `Callable` 对象传递给 executor 执行，返回 `Future` 对象，通过 `Future` 对象我们可以：

- 查看任务的状态，可以取消任务，也可以通过 `isDone()` 方法检查任务是否完成；
- 获得执行的结果，通过 `get()` 方法获得结果，该方法会等待 `Callable` 的 `call()` 方法执行结束。

在 `get()` 方法等待结果时如果线程被中断，抛出 `InterruptedException` 异常，如果 `call()` 方法抛出异常，则 `get()` 方法抛出 `ExecutionException` 异常。

`Future` 接口还提供了重载的 `get(long timeout, TimeUnit unit)`方法。该方法如果在指定时间内没有执行完，抛出 `TimeoutException`。
