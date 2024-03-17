# 拒绝任务

## 简介

`ThreadPoolExecutor` 构造函数的最后一个参数指定拒绝策略。

当任务数量超过系统实际承载能力，就要用到拒绝策略。拒绝策略是系统超负荷运行的补救措施。线程池中的线程用完了，同时，等待队列也已经排满了，放不下新的任务，此时需要拒绝策略。

JDK 内置的 4 种拒绝策略：

- `AbortPolicy`：直接抛出异常，系统停止工作；
- `CallerRunsPolicy`：如果线程池未关闭，直接在调用者线程中运行当前被丢弃的任务。这样不会真的丢弃任务，但是，任务提交线程的性能会受影响；
- `DiscardOldestPolicy`：丢弃最老的一个任务（即将被执行的任务），并尝试再次提交当前任务；
- `DiscardPolicy`：默默丢弃无法处理的任务，不做任何处理。

以上策略均实现了 `RejectedExecutionHandler` 接口。该接口很简单：

```java
public interface RejectedExecutionHandler {
    void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
}
```

`r` 是请求执行的任务，`executor` 为当前线程池。

