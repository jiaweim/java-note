# Java 多线程的演变

JDK 1.x 只包含一些基本的类：

- `java.lang.Thread`
- `java.lang.ThreadGroup`
- `java.lang.Runnable`
- `java.lang.Process`
- `java.lang.ThreadDeath`

以及一些异常类：

- `java.lang.IllegalMonitorStateException`
- `java.lang.IllegalStateException`
- `java.lang.IllegalThreadStateException`

还有少量的同步集合，如 `java.util.Hashtable`。

JDK 1.2 和 JDK 1.3 在多线程方便没有什么变化。

JDK 1.4 有少许的 JVM 方面的改变，可以通过一次调用挂起或恢复线程，但是没有大的 API 改变。

JDK 1.5 是继 1.x 之后第一个主要版本，不包含多个并发工具：`Executors`, `Semaphore`, `Mutex`, `Barrier`，并发集合、阻塞队列等等。

JDK 1.6 主要修复了一些 bug，没怎么添加新内容。

JDK 1.7 添加了 ForkJoinPool，实现 work-stealing 技术，以最大化吞吐量。

JDK 1.8 除了 lambda 表达式，还添加了并发工具 `CompletableFuture` 等。
