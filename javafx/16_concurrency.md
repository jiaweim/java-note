# 大纲
- [大纲](#%e5%a4%a7%e7%ba%b2)
- [参考](#%e5%8f%82%e8%80%83)
- [简介](#%e7%ae%80%e4%bb%8b)
- [Concurrent Framework API](#concurrent-framework-api)
- [Worker](#worker)
  - [Worker 属性](#worker-%e5%b1%9e%e6%80%a7)
- [Task](#task)
  - [创建 Task](#%e5%88%9b%e5%bb%ba-task)
  - [更新 Task 属性](#%e6%9b%b4%e6%96%b0-task-%e5%b1%9e%e6%80%a7)
  - [监听任务进程](#%e7%9b%91%e5%90%ac%e4%bb%bb%e5%8a%a1%e8%bf%9b%e7%a8%8b)
  - [取消任务](#%e5%8f%96%e6%b6%88%e4%bb%bb%e5%8a%a1)
- [Service<V>](#servicev)

# 参考
Oracle 官方资料
- http://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm#JFXIP546
book 
- Learn JavaFX 8: Building User Experience and Interfaces with Java 8

# 简介
和 Swing、AWT类似，JavaFX 也有一个用于处理 UI 事件的专用线程 JavaFX Application Thread (JAT)。JavaFX 正在使用的 UI 元素只能通过 JAT 访问。

如果要在其他线程修改 JavaFX UI 内容，可以通过 `Platformat` 的方法：
```java
public static boolean isFxApplicationThread()
public static void runLater(Runnable runnable)
```

JavaFX 提供了一个可运行一个或多个 background 线程，并将状态更新到 GUI 程序的并发框架。下面对该框架进行讨论

# Concurrent Framework API
JavaFX 并发框架构建在 `java.util.concurrent` 之上，单纯的用于GUI 环境，其类图如下：

![](2019-06-05-19-10-36.png)

很小的一个包，包括 1 个接口，4个类和一个 enum。  
Worker 接口，表示需要在后台线程执行的任务，任务的状态可以在 JAT 中查看。  
Task, Service, ScheduledSerivice 均实现了 Worker 接口，表示不同类型的任务。

|类|说明|
|---|---|
|Task|一次任务，不能重复执行|
|Service|可重复执行任务|
|ScheduledService|可根据指定间隔反复运行|

WorkerStateEvent 则表示 Worker 状态改变的事件，可以监听这些事件。

# Worker
Worker<V> 接口指定了 JavaFX 并发模块任务需要的常用方法。
泛型参数 V 指定了任务返回的结果类型。

任务执行的状态，可以被鉴定，然后发送到 JAT，从而可以和 scene graph 交流。

在任务执行期间，Worker 的状态变化如下：

![](2019-06-05-19-12-07.png)

## Worker 属性
Worker 包含 9 个只读属性
|属性|描述|
|---|---|
|title|任务标题|
|message|任务执行过程中的信息|
|running|Woker 是否在执行，SCHEDULED 或 RUNNING 状态，为 true，否则为 false|
|state|Worker 状态，State enum 类型|
|totalWork|任务总量，-1 表示未知|
|workDone|已完成任务数|
|progress|ratio of workDone and totalWork|
|value|the result of the task.|
|exception|represents the exception that is thrown during the progressing of the task. It is non null only when the state of the Worker is FAILED.|

# Task
Task<V> 表示一次运行任务。任务结束、取消或者失败后，无法重启。Task<V> 实现了 Worker<V> 接口，因此继承了 Worker 的所有属性和方法。

## 创建 Task
扩展 Task<V> 即可：
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
Task 提供了如下方法更新属性:
```java
protected void updateMessage(String message)
protected void updateProgress(double workDone, double totalWork)
protected void updateProgress(long workDone, long totalWork)
protected void updateTitle(String title)
protected void updateValue(V value)
```
所有的 updateXxx() 方法，它们在 Task 的 call() 方法中都可以安全执行，都是在 JAT 上执行。

## 监听任务进程
Task 有如下几个添加监听器的方法：
- onCalcelled
- onFailed
- onRunning
- onScheduled
- onSucceeded

## 取消任务
`cancel()` 有两个重载方法：
```java
public final boolean cancel()
public boolean cancel(boolean mayInterruptIfRunning)
```
第一个将任务从队列中移除，或停止任务执行；第二个表示是否中断正在执行任务的线程。

# Service<V>
Service<V> 实现了 Worker<V> 接口，该类包装了 Task<V>，使得 Task<V> 可以重复利用。
