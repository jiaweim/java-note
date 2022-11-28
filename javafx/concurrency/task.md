# Task

- [Task](#task)
  - [简介](#简介)
  - [创建 Task](#创建-task)
  - [更新 Task 属性](#更新-task-属性)
  - [监听任务进程](#监听任务进程)
  - [取消任务](#取消任务)
  - [执行任务](#执行任务)

2022-04-27, 15:29
****

## 简介

`Task<V>` 表示一次性任务。任务结束、取消或者失败后，无法重启。

`Task<V>` 实现了 `Worker<V>` 接口，因此继承了 `Worker` 的所有属性和方法。

`Task<V>` 继承自 `FutureTask<V>` 类，该类实现了 `Future<V>`, `RunnableFuture<V>` 以及 `Runnable` 接口，因此 `Task<V>` 也实现的所有这些接口。

## 创建 Task

扩展 `Task<V>`，并实现 `call()` 方法即可。`call()` 发方法包含任务逻辑。

```java
// A Task that produces an ObservableList<Long>
public class PrimeFinderTask extends Task<ObservableList<Long>> {
  @Override
  protected ObservableList<Long>> call() {
    // Implement the task logic here...
  }
}
```

## 更新 Task 属性

Task 提供了如下更新属性的方法:

```java
protected void updateMessage(String message)
protected void updateProgress(double workDone, double totalWork)
protected void updateProgress(long workDone, long totalWork)
protected void updateTitle(String title)
protected void updateValue(V value)
```

使用 `updateValue()` 可以提交部分结果，任务的最终结果是 `call()` 方法的返回值。

对 `updateProgress()`，如果 `workDone` 大于 `totalWork`，或者两者都小于 -1.0，会抛出异常。

所有的 `updateXXX()` 方法在 JAT 线程执行，可以在 `call()` 方法中放心调用。

如果在 `Task.call()` 中不用 `updateXXX()` 系列方法更新属性，则需要将更新属性代码放在 `Platform.runLater()` 中。

## 监听任务进程

Task 包含如下几个添加监听器的方法：

- onCalcelled
- onFailed
- onRunning
- onScheduled
- onSucceeded

例如，添加 `onSucceeded` 事件监听器，在任务转到 `SUCCEEDED` 状态时执行：

```java
Task<ObservableList<Long>> task = create a task...
task.setOnSucceeded(e -> {
    System.out.println("The task finished. Let us party!")
});
```

## 取消任务

`cancel()` 有两个重载方法：

```java
public final boolean cancel()
public boolean cancel(boolean mayInterruptIfRunning)
```

第一个将任务从任务队列中移除，如果任务已经在执行，则停止任务执行；第二个表示是否中断正在执行任务的线程。

**在 `call()` 方法中**要处理 `InterruptedException`，检测到该异常，应尽快结束 `call()` 方法，否则调用 `cancel(true)` 可能无法可靠的取消任务。

`cancel()` 方法可以在任何线程调用。

当 `Task` 达到特定状态时，下面的对应方法自动调用：

- `protected void scheduled()`
- `protected void running()`
- `protected void succeeded()`
- `protected void cancelled()`
- `protected void failed()`

这些方法在 `Task` 类中是空的，由继承的子类根据需要实现。

## 执行任务

`Task` 是 `Runnable`，也是 `FutureTask`。可以创建后台线程或 `ExecutorService` 执行 `Task`。

```java
// 将任务放在后台线程执行
Thread backgroundThread = new Thread(task);
backgroundThread.setDaemon(true); // 设置为守护线程，这样退出界面后，不会有后台线程继续执行
backgroundThread.start();

// Use the executor service to schedule the task
ExecutorService executor = Executors.newSingleThreadExecutor();
executor.submit(task);
```
