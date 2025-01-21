# Effective Java


- [3. 单例模式](./3_singleton.md)
- [11. 重写 equals 时要重写 hashCode](./11_hashCode.md)



## 并发

### 80. executor, task 和 stream 优于 thread

Executor 框架是一个灵活的基于接口的任务执行框架。例如，创建 一个任务队列只需要一行代码：

```java
ExecutorService exec = Executors.newSingleThreadExecutor();
```

提交任务：

```java
exec.execute(runnable);
```

正常终止线程池：

```java
exec.shutdown();
```

使用线程池可以做很多事情。例如，

## 参考



https://www.bookstack.cn/read/effective-java-3rd-chinese/docs-README.md

https://zhuanlan.zhihu.com/p/591609959

