# 运行多个任务，处理所有结果

2024-03-13
@author Jiawei Mao

## 简介

`Executor` 框架支持并发执行任务，无需担心线程的创建和执行。其提供的 `Future` 类可用于控制任务状态，获取任务执行结果。

如果需要等待一个任务结束，可以使用以下两种方法：

- 当任务完成时，`Future.isDone()` 返回 `true`；
- `ThreadPoolExecutor.awaitTermination()`，在调用 `shutDown()` 后，将当前线程进入 sleep 状态，直到所有任务执行完成。

这两个方法各有优缺点。第一个方法只能查看一个任务是否完成；第二个，必须先关闭 executor，否则 `awaitTermination()` 直接返回。

`ThreadPoolExecutor` 提供的 `invokeAll` 方法，向线程池添加任务列表，并等待所有任务完成。

## 示例

1. 创建 `Result` 类，表示任务执行结果

```java
public class Result {

    private String name;
    private int value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
```

2. 创建 Task2 类，表示需要并发执行的任务

 ```java
 import java.util.concurrent.Callable;
 import java.util.concurrent.TimeUnit;
 
 public class Task2 implements Callable<Result> {
 
     private final String name;
 
     public Task2(String name) {
         this.name = name;
     }
 
     @Override
     public Result call() throws Exception {
         System.out.printf("%s: Staring\n", this.name);
         // 随机 sleep 一段时间
         try {
             long duration = (long) (Math.random() * 10);
             System.out.printf("%s: Waiting %d seconds for results.\n",
                     this.name, duration);
             TimeUnit.SECONDS.sleep(duration);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         // 计算 5 个随机数的加和，作为 Result
         int value = 0;
         for (int i = 0; i < 5; i++) {
             value += (int) (Math.random() * 100);
         }
         Result result = new Result();
         result.setName(this.name);
         result.setValue(value);
         System.out.println(this.name + ": Ends");
 
         return result;
     }
 }
 ```

3. 创建 Main2 类

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main2 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        // 创建 10 个 Task2
        List<Task2> taskList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Task2 task = new Task2("Task-" + i);
            taskList.add(task);
        }

        List<Future<Result>> resultList = null;
        try {
            // 所有任务完成，才返回
            resultList = executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();

        System.out.println("Main: Printing the results");
        for (Future<Result> future : resultList) {
            try {
                Result result = future.get();
                System.out.println(result.getName() + ": " + result.getValue());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
```

```java
Task-0: Staring
Task-4: Staring
Task-3: Staring
Task-2: Staring
Task-1: Staring
Task-5: Staring
Task-6: Staring
Task-7: Staring
Task-8: Staring
Task-0: Waiting 2 seconds for results.
Task-7: Waiting 8 seconds for results.
Task-6: Waiting 6 seconds for results.
Task-1: Waiting 6 seconds for results.
Task-3: Waiting 6 seconds for results.
Task-5: Waiting 9 seconds for results.
Task-4: Waiting 4 seconds for results.
Task-8: Waiting 7 seconds for results.
Task-2: Waiting 1 seconds for results.
Task-9: Staring
Task-9: Waiting 7 seconds for results.
Task-2: Ends
Task-0: Ends
Task-4: Ends
Task-6: Ends
Task-3: Ends
Task-1: Ends
Task-8: Ends
Task-9: Ends
Task-7: Ends
Task-5: Ends
Main: Printing the results
Task-0: 273
Task-1: 306
Task-2: 152
Task-3: 198
Task-4: 242
Task-5: 287
Task-6: 243
Task-7: 279
Task-8: 309
Task-9: 180
```

该示例的关键是 `ExecutorService.invokeAll` 方法，该方法接收 `Callable` 对象列表，返回相同长度的 `Future` 对象列表，且顺序一致，即第一个 `Future` 对象对应第一个 `Callable` 对象。

比较重要的是，`invokeAll()` 方法会**阻塞直到所有任务完成**，因此通过 `invokeAll()` 方法返回的 `Future` 对象，其 `isDone()` 肯定为 `true`。