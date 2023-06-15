# 线程

## 启动线程

不管是实现 `Runnable` 还是继承 `Thread`，都通过调用 `start()` 启动线程，该方法的源码如下：

```java
public synchronized void start() {

    if (threadStatus != 0)
        throw new IllegalThreadStateException();

    group.add(this);

    boolean started = false;
    try {
        start0();
        started = true;
    } finally {
        try {
            if (!started) {
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {

        }
    }
}
```

状态 0 表示处于 `NEW` 状态，如果不是 `NEW` 状态，说明已启动。

该方法的核心部分是 `start0()` 方法，该方法是一个 JNI 方法：

```java
private native void start0();
```

因为 `start()` 方法最终会执行 `run()` 方法，所以 `start0()` 方法必然调用了 `run` 方法。

总结：

- 线程不能重复调用 `start()`，否则抛出 `IllegalThreadStateException`；
- 线程启动后被加入 `ThreadGroup`
- 线程结束后进入 `TERMINATED` 状态，再次调用 `start` 抛出异常。

不允许重复启动线程，否则抛出 `IllegalThreadStateException`，例如：

```java
Thread thread = new Thread(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

thread.start();
TimeUnit.SECONDS.sleep(2);
thread.start();
```

中间休眠 2 秒以保证现在执行完，再调用 `start()`，抛出异常。

## ThreadGroup 解析

`java.lang.ThreadGroup` 类表示一组线程。通过将多个线程放在一组，可以对多个线程同时操作，例如一次操作打断所有线程。

线程组还可以包含其它线程组，除了初始线程组，所有线程组都有一个父线程组，构成树结构。

线程可以获得其所在线程组的信息。

### 构造函数

`ThreadGroup` 有两个构造函数

- `ThreadGroup(ThreadGroup parent, String name)`, 指定名称和父线程组
- `ThreadGroup(String name)`, 采用当前线程所在线程组为父线程组

### ThreadGroup 方法

| 方法                     | 说明                                            |
| ------------------------ | ----------------------------------------------- |
| checkAccess()            | 当前运行的线程是否有权限修改线程组              |
| activeCount()            | 线程组及其子线程组中活动线程估计数目            |
| activeGroupCount()       | 线程组及其子线程组中活动线程组估计数目          |
| destroy()                | 销毁当前线程组及其所有子线程组                  |
| enumerate(Thread[] list) | 将线程组及其子组中所有活动线程复制到指定数组    |
| getMaxPriority()         | 线程组最大优先级                                |
| getName()                | 线程组名称                                      |
| getParent()              | 返回父线程组                                    |
| interrupt()              | 中断线程组中所有线程                            |
| isDaemon()               | 是否是守护线程组                                |
| isDestroyed()            | 线程组是否被销毁                                |
| list()                   | 将线程组相关信息打印到标准输出                  |
| parentOf(ThreadGroup g)  | 如果线程组是参数线程组，或其父线程组，返回 true |

实例：

```java
ThreadGroup g1 = new ThreadGroup("Parent ThreadGroup");

Runnable runnable = () -> System.out.println(Thread.currentThread().getName());

Thread t1 = new Thread(g1, runnable, "one");
t1.start();
Thread t2 = new Thread(g1, runnable, "two");
t2.start();
Thread t3 = new Thread(g1, runnable, "three");
t3.start();

g1.list();
```
