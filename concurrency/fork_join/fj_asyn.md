# 异步运行任务

## 简介

在 ForkJoinPool 中运行 ForkJoinTask，可以采用同步和异步两种方式：

- 同步方式执行，将任务发送到 pool 的方法，在任务完成前不会返回；
- 异步方式执行，将任务发送到 pool 的方法，立即返回。

使用同步方法时，如 invokeAll()，task 挂起，直到它发送到 pool 的 subtasks 完成。在挂起期间，ForkJoinPool 可以使用工作窃取算法为 worker-thread 分配新的任务。

相反，使用异步方法，如 fork()，任务将继续执行，因此 ForkJoinPool 无法使用工作窃取算法来提高性能。此时，只有调用 join() 或 get() 方法等待任务结束时，ForkJoinPool 才能窃取任务。

除了 RecursiveAction 和 RecursiveTask 类，Java 8 引入一个新的 ForkJoinTask 实现 CountedCompleter 类。CountedCompleter 可以在任务完成没有 subtasks 时添加一个动作（onCompletion() 方法）。

- CountedCompleter 维护两个操作：完成动作(onCompletion)和异常动作(onExceptionalCompletion)，开发者可以重写这两个方法实现自定义的动作内容；
- CountedCompleter 维护一个父任务变量(completer)，当前任务的完成情况、异常可通过这个变量逐级传递上去；
- CountedCompleter维护挂起任务(子任务)的计数(pending)，挂起任务完成会触发父任务的 pending 减 1，父任务pending计数减到 0 触发父任务完成动作(onCompletion)；
- CountedCompleter依旧是个抽象类，核心的方法是compute，开发者需要自定这个方法，注意compute方法返回之前需要调用tryComplete；
- 作为一个ForkJoinTask，compute返回之后ForkJoinPool并没有将ForkJoinTask的状态置为Normal，如果此时调用join会阻塞线程，开发者可以在 onCompletion 调用 this.quietlyComplete() 设置Normal。

subtasks 数目通过一个 counter 计数。通常，在启动 subtasks 时增加 counter。在 subtask 完成时减 1。归 0 时 onCompletion() 执行，parent task 完成。

与其它 ForkJoinTask 实现相比，CountedCompleter 在存在 subtask 停顿和阻塞的情况下通常更 robust，但是编码更复杂。CountedCompleter 的使用和其它基于完成的组件类似，如 CompletionHandler，只是触发  onCompletion(CountedCompleter) 完成操作需要多个 pending 完成，而不是 1 个。pending-count 默认从 0 开始，但可以使用  setPendingCount(int), addToPendingCount(int) 和 compareAndSetPendingCount(int, int) 修改（均为原子操作）。在调用 tryComplete() 时，如果 pending-count 不为 0，则 pending-count 减 1。



## 示例

下面演示 ForkJoinPool 和 CountedCompleter 用于异步方法来管理任务。

任务：在文件夹及其子文件夹搜索指定扩展名的文件。

CountedCompleter 负责处理文件夹内容，对每个子文件夹，它以异步方式向 ForkJoinPool 发送一个新任务。

1. 任务类

```java
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;

public class FolderProcessor extends CountedCompleter<List<String>> {

    private String path;
    private String extension;
    // 当前 task 的 subtasks
    private List<FolderProcessor> tasks = new ArrayList<>();
    // 当前 task 的结果
    private List<String> resultList = new ArrayList<>();

    protected FolderProcessor(CountedCompleter<?> completer,
            String path, String extension) {
        super(completer);
        this.path = path;
        this.extension = extension;
    }

    public FolderProcessor(String path, String extension) {
        this.path = path;
        this.extension = extension;
    }

    @Override
    public void compute() {
        File file = new File(path);
        File[] content = file.listFiles();
        if (content != null) {
            for (File value : content) {
                // 如果是目录
                if (value.isDirectory()) {
                    // 创建任务
                    FolderProcessor task = new FolderProcessor(this,
                            value.getAbsolutePath(), extension);
                    task.fork(); // 异步执行，直接返回
                    // pending-task +1
                    addToPendingCount(1);
                    tasks.add(task);
                } else {
                    if (checkFile(value.getName())) {
                        resultList.add(value.getAbsolutePath());
                    }
                }
            }
            if (tasks.size() > 50) {
                System.out.printf("%s: %d tasks ran.\n",
                        file.getAbsolutePath(), tasks.size());
            }
        }
        // 尝试完成，如果 pending-count = 0，则执行 onCompletion
        tryComplete();
    }
    
    @Override
    public List<String> getRawResult() {
        return resultList;
    }

    // 当所有 subtasks 完成，触发下面的方法
    @Override
    public void onCompletion(CountedCompleter<?> completer) {
        for (FolderProcessor childTask : tasks) {
            resultList.addAll(childTask.getRawResult());
        }
    }

    private boolean checkFile(String name) {
        return name.endsWith(extension);
    }
}
```

2. main 类

```java
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main3 {

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        FolderProcessor system = new FolderProcessor("C:\\Windows",
                "log");
        FolderProcessor apps = new FolderProcessor("C:\\Program Files",
                "log");
        FolderProcessor documents = new FolderProcessor("C:\\ProgramData",
                "log");
        pool.execute(system);
        pool.execute(apps);
        pool.execute(documents);
        do {
            System.out.printf("******************************************\n");
            System.out.printf("Main: Active Threads: %d\n",
                    pool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n",
                    pool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n",
                    pool.getStealCount());
            System.out.printf("******************************************\n");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ((!system.isDone()) || (!apps.isDone()) ||
                (!documents.isDone()));
        pool.shutdown();
        List<String> results = system.join();
        System.out.printf("System: %d files found.\n", results.size());
        results = apps.join();
        System.out.printf("Apps: %d files found.\n", results.size());
        results = documents.join();
        System.out.printf("Documents: %d files found.\n", results.size());
    }
}
```

```
******************************************
Main: Active Threads: 2
Main: Task Count: 0
Main: Steal Count: 0
******************************************
******************************************
Main: Active Threads: 32
Main: Task Count: 5397
Main: Steal Count: 950
******************************************
******************************************
Main: Active Threads: 32
Main: Task Count: 1134
Main: Steal Count: 5748
******************************************
******************************************
Main: Active Threads: 32
Main: Task Count: 7713
Main: Steal Count: 22334
******************************************
System: 131 files found.
Apps: 12 files found.
Documents: 0 files found.
```

本示例的核心是 `FolderProcessor`。每个 task 处理 一个目录。

`addToPendingCount()` 增加 pending-count 值，其它修改 counter 值的方法：

- `setPendingCount()`：直接设置 pending-count 值
- `compareAndSetPendingCount()`：
