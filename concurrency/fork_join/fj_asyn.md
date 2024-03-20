# 异步运行任务

## 简介

在 `ForkJoinPool` 中运行 `ForkJoinTask`，可以采用同步和异步两种方式：

- 同步方式执行，将任务发送到 pool 的方法，在任务完成前不会返回；
- 异步方式执行，将任务发送到 pool 的方法，立即返回。

使用同步方法时，如 `invokeAll()`，task 挂起，直到它发送到 pool 的 subtasks 完成。在挂起期间，`ForkJoinPool` 可以使用工作窃取算法为 worker-thread 分配新的任务。

相反，使用异步方法，如 `fork()`，任务将继续执行，因此 `ForkJoinPool` 无法使用工作窃取算法来提高性能。此时，只有调用 `join()` 或 `get()` 方法等待任务结束时，`ForkJoinPool` 才能窃取任务。

除了 `RecursiveAction` 和 `RecursiveTask`，Java 8 引入一个新的 `ForkJoinTask` 实现 `CountedCompleter` 类。`CountedCompleter` 可以在任务完成没有 subtasks 时添加一个动作（`onCompletion()` 方法）。

- `CountedCompleter` 维护两个操作：完成动作(`onCompletion()`)和异常动作(`onExceptionalCompletion()`)，开发者可以重写这两个方法实现自定义的动作内容；
- `CountedCompleter` 维护一个父任务变量(completer)，当前 task 的完成情况、异常可通过这个变量逐级传递上去；
- `CountedCompleter` 维护挂起任务(subtasks)的计数(pending-count)，pending-task 完成会触发父任务的 pending-count 减 1，父任务 pending-count 减到 0 触发父任务完成动作(`onCompletion`)；
- `CountedCompleter` 依旧是个抽象类，核心的方法是 `compute`，开发者需要自定这个方法，注意 `compute` 方法返回之前需要调用 `tryComplete()`；
- 作为一个 `ForkJoinTask`，`compute` 返回之后 `ForkJoinPool` 并没有将 `ForkJoinTask` 的状态置为`Normal`，如果此时调用 `join` 会阻塞线程，开发者可以在 `onCompletion` 调用 `this.quietlyComplete()` 设置 `Normal`。
- `CountedCompleter` 通常不生成结果，即一般声明为 `CountedCompleter<Void>`，返回 `null`。
  - 如果需要返回结果，则应该覆盖 `getRawResult()`方法，为 `join()`, `invoke()` 和相关方法提供结果。
  - `getRawResult()` 通常返回 `CountedCompleter` 的一个字段（或多个字段的函数值），该字段保存完成时的结果。
  - `setRawResult(T)` 在 `CountedCompleter` 默认不起作用。


与其它 `ForkJoinTask` 实现相比，`CountedCompleter` 处理 subtask 停顿和阻塞方面更 robust，但是编程方面更复杂。

pending-count 说明：

- subtasks 数目通过一个 counter 计数。通常，在启动 subtasks 时增加 counter。在 subtask 完成时减 1。归 0 时 `onCompletion()` 执行，parent task 完成。
- `CountedCompleter` 的使用和其它基于完成的组件类似，如 `CompletionHandler`，只是触发  `onCompletion(CountedCompleter)` 完成操作需要多个 pending 完成，而不是 1 个。
- pending-count 默认初始化为 0，但可以使用  `setPendingCount(int)`, `addToPendingCount(int)` 和 `compareAndSetPendingCount(int, int)` 修改（均为原子操作）。
- 在调用 `tryComplete()` 时，如果 pending-count 不为 0，则 pending-count 减 1；否则，执行 `onCompletion` 操作，如果包含 parent-completer，则继续在 parent-completer 执行该操作

没有 parent-completer 的 `CountedCompleter` (即 `getCompleter()` 返回 `null`)，可以作为常规的 `ForkJoinTask` 使用。 

## 示例：并行递归分解

`CountedCompleter` 可以组织为树形结构，类似 `RecursiveAction` 常用形式。在这里，每个 task 的 completer 对应计算树上的父节点。尽管需要更多的 bookkeeping，但对 CPU 密集型操作，`CountedCompleter` 更合适；特别是当有些元素的操作时间明显与其它元素不同时，因为 `CountedCompleter` 提供为异步执行。

例如，下面是一个程序的初始版本：通过递归二分拆分任务。即使将任务拆分到了单个调用（leaf-tasks）， tree-based 技术通常也优于直接拆分为 leaf-tasks，因为它们减少了线程间通信，并改善了负载平衡。在递归中，每对 subtasks 中的第二个 subtask 完成时会触发 parent-task 完成（因为不需要合并结果，所以没有覆盖 `onCompletion`）。下面的方法设置 root-task 并 invoke 它（这里隐式的使用 `ForkJoinPool.commonPool()`）。这里之间将 pending-count 设置为 subtasks 个数，直接又可靠（非最优），在返回时调用 `tryComplete()`。

```java
public static <E> void forEach(E[] array, Consumer<E> action) {
    class Task extends CountedCompleter<Void> {

        final int lo, hi;

        Task(Task parent, int lo, int hi) {
            super(parent);
            this.lo = lo;
            this.hi = hi;
        }

        public void compute() {
            // 大于 2 个就拆，因此每个 leaf-task 只有 1 个元素
            if (hi - lo >= 2) {
                int mid = (lo + hi) >>> 1;
                // 在 fork 前必须设置 pending-count
                setPendingCount(2);
                new Task(this, mid, hi).fork(); // right child
                new Task(this, lo, mid).fork(); // left child
            } else if (hi > lo)
                action.accept(array[lo]);
            tryComplete();
        }
    }
    // root-task，其 parent 为 null
    new Task(null, 0, array.length).invoke();
}
```

**改进一：** 注意到，在 fork right-task 后，这个 task 就无事可做，因此可以在返回前 invoke left-task（类似 tail-recursion removal）。另外，当 task 的最后一个动作是 fork 或 invoke 一个 subtask 时，可以优化对 `tryComplete()` 的调用，代价就是 pending-count 减 1。

```java
public void compute() {
    if (hi - lo >= 2) {
        int mid = (lo + hi) >>> 1;
        setPendingCount(1); // pending-count 只有 1
        new Task(this, mid, hi).fork(); // fork right-subtask
        new Task(this, lo, mid).compute(); // left-subtask，直接执行
    } else {
        if (hi > lo)
            action.accept(array[lo]);
        tryComplete();
    }
}
```

**改进二：** 其实不需要创建 left-task。可以沿用原 task，对每个 fork 操作 pending-count +1。此外，由于此 task-tree 没有实现 `onCompletion(CountedCompleter)` 的 task，因此可以用 `propagateCompletion()` 替换 `tryComplete`。

```java
public void compute() {
    int n = hi - lo;
    for (; n >= 2; n /= 2) {
        addToPendingCount(1);
        new Task(this, lo + n / 2, lo + n).fork();
    }
    if (n > 0)
        action.accept(array[lo]);
    propagateCompletion();
}
```

**改进三：** 如果能提前计算出 pending-count，可以直接在构造函数指定：

```java
public static <E> void forEach(E[] array, Consumer<E> action) {
    class Task extends CountedCompleter<Void> {

        final int lo, hi;

        Task(Task parent, int lo, int hi) {
            super(parent, 31 - Integer.numberOfLeadingZeros(hi - lo));
            this.lo = lo;
            this.hi = hi;
        }

        public void compute() {
            for (int n = hi - lo; n >= 2; n /= 2)
                new Task(this, lo + n / 2, lo + n).fork();
            action.accept(array[lo]);
            propagateCompletion();
        }
    }
    if (array.length > 0)
        new Task(null, 0, array.length).invoke();
}
```

还可以对 leaf 进行优化，例如，每个细分迭代除以 4 而不是 2，使用自定义阈值，而不是总是细分到单个元素。

## 示例：搜索

`CountedCompleter` tree 可以在数据结构的不同部分搜索值，找到后，使用  `AtomicReference` 报告结果。其它部分可以轮询结果以避免不必要的工作。再次以完全划分的数组为例（在实践中，leaf-tasks 一般处理多个元素）：

```java
class Searcher<E> extends CountedCompleter<E> {
    final E[] array;
    final AtomicReference<E> result;
    final int lo, hi;

    Searcher(CountedCompleter<?> p, E[] array, AtomicReference<E> result,
            int lo, int hi) {
        super(p);
        this.array = array;
        this.result = result;
        this.lo = lo;
        this.hi = hi;
    }

    public E getRawResult() {return result.get();}

    public void compute() { // similar to ForEach version 3
        int l = lo, h = hi;
        while (result.get() == null && h >= l) {
            if (h - l >= 2) {
                int mid = (l + h) >>> 1;
                addToPendingCount(1);
                new Searcher(this, array, result, mid, h).fork();
                h = mid;
            } else {
                E x = array[l];
                if (matches(x) && result.compareAndSet(null, x))
                    quietlyCompleteRoot(); // root task is now joinable
                break;
            }
        }
        tryComplete(); // normally complete whether or not found
    }

    boolean matches(E e) { ...} // return true if found

    public static <E> E search(E[] array) {
        return new Searcher<E>(null, array,
                new AtomicReference<E>(), 0, array.length).invoke();
    }
}
```

在本例，以及其它除了 `compareAndSet` 设置结果没有其它操作，那么后面的无条件 `tryComplete` 可以改为有条件语句：

```java
if (result.get() == null) tryComplete();
```

## 示例：记录子任务

需要合并 subtasks 结果的 `CountedCompleter` 通常需要在 `onCompletion(CountedCompleter)` 中访问结果。如下所示，执行一个简化的 map-reduce 操作，其中 map 和 reduce 都是 E 类型，在分治法中实现该功能的一个方法是让每个 subtask 记录其 sibling-task，因此可以在 `onCompletion` 中访问。该技术适用于 left, right 顺序不重要

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
- `compareAndSetPendingCount()`：这个方法有两个参数，第一个是期望值，第二个是新的值。只有新值与期望值相等，设置才有效
- `decrementPendingCountUnlessZero()`：将 pending-count 减 1，除非 已经为 0.

`CountedCompleter` 还包含其它管理任务完成的方法：

- `complete()`：不管 pending-count，直接调用 `onCompletion()`，将 task 标记为完成，
