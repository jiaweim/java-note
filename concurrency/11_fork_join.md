# Fork/Join Framework

fork/join 框架实现 `ExecutorService` 接口，为充分利用多处理器，为那些可以拆分为小任务的工作而设计。

和其它实现 `ExecutorService` 的框架一样，fork/join 也是在线程池中执行任务，fork/join 不同点在于，它使用了工作窃取算法（work-stealing）。执行完任务的工作线程可以从其它很忙的线程中窃取任务执行。

fork/join 框架的核心类是 `ForkJoinPool`，该类扩展 `AbstractExecutorService` 类，实现工作窃取算法，可以执行 `ForkJoinTask`。

## 基本使用