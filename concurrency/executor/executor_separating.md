# 任务启动和处理结果的分离

## 简介

使用 executor 执行并发任务，将 `Runnable` 或 `Callable` 任务发送给 executor，获得 `Future` 对象。

`Future` 的主要问题是，如果任务没有完成，其 `get()` 方法会阻塞，因此无法迅速拿到第一个完成的 Callable 的结果。`CompletionService`  为该问题提供了解决方案：`CompletionService`  根据线程池中任务的执行结果按执行完成的先后顺序排序，先完成的可优先取到。

`CompletionService`  的定义如下：

```java
public interface CompletionService<V> {
    // 提交任务
    Future<V> submit(Callable<V> task);
    Future<V> submit(Runnable task, V result);

    // 阻塞方法，从队列中获取并移除一个已经完成的任务的结果，如果没有已完成任务，就阻塞
    Future<V> take() throws InterruptedException;
	// 非阻塞：从队列中获取并移除一个已经完成的任务的结果，如果没有已完成任务，返回 null，不阻塞
    Future<V> poll();
    Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
}
```

## 示例

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class CompletionServiceExample {

    public static void main(String[] args) throws ExecutionException,
            InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Callable<Integer>> callables = Arrays.asList(
                () -> {
                    mySleep(20);
                    System.out.println("=============20 end==============");
                    return 20;
                },
                () -> {
                    mySleep(10);
                    System.out.println("=============10 end==============");
                    return 10;
                }
        );

        List<Future<Integer>> futures = new ArrayList<>();
        //提交任务,并将future添加到list集合中
        futures.add(executorService.submit(callables.get(0)));
        futures.add(executorService.submit(callables.get(1)));
        // 遍历Future,因为不知道哪个任务先完成,所以这边模拟第一个拿到的就是执行时间最长的任务,
        // 那么执行时间较短的任务就必须等待执行时间长的任务执行完
        for (Future future : futures) {
            System.out.println("结果: " + future.get());
        }
        System.out.println("============main end=============");
    }

    private static void mySleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

```
=============10 end==============
=============20 end==============
结果: 20
结果: 10
============main end=============
```

可以发现，虽然执行 10 秒的任务先执行完，但调用 `future.get()` 拿结果时，休眠 20 秒的没完成，就会阻塞，从而影响了性能。

下面用 `CompletionService` 解决该问题：

```java
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class CompletionService2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<Callable<Integer>> callables = Arrays.asList(
                () -> {
                    mySleep(20);
                    System.out.println("=============20 end==============");
                    return 20;
                },
                () -> {
                    mySleep(10);
                    System.out.println("=============10 end==============");
                    return 10;
                }
        );

        // 构建 ExecutorCompletionService，与线程池关联
        CompletionService completionService = new ExecutorCompletionService(executorService);
        // 提交 Callable 任务
        completionService.submit(callables.get(0));
        completionService.submit(callables.get(1));

        // 获取 future 结果,不会阻塞
        Future<Integer> pollFuture = completionService.poll();
        // 这里因为没有执行完成的 Callable，所以返回null
        System.out.println(pollFuture);
        // 获取future结果,最多等待3秒,不会阻塞
        Future<Integer> pollTimeOutFuture = completionService.poll(3, TimeUnit.SECONDS);
        // 这里因为没有执行完成的Callable，所以返回null
        System.out.println(pollTimeOutFuture);
        // 通过take获取Future结果,此方法会阻塞
        for (int i = 0; i < callables.size(); i++) {
            System.out.println(completionService.take().get());
        }

        System.out.println("============main end=============");
    }

    private static void mySleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

```
null
null
=============10 end==============
10
=============20 end==============
20
============main end=============
```



