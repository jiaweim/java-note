# 线程

- [线程](#%e7%ba%bf%e7%a8%8b)
  - [Thread 对象](#thread-%e5%af%b9%e8%b1%a1)
  - [创建线程](#%e5%88%9b%e5%bb%ba%e7%ba%bf%e7%a8%8b)
    - [继承 Thread](#%e7%bb%a7%e6%89%bf-thread)
    - [实现 Runnable 接口](#%e5%ae%9e%e7%8e%b0-runnable-%e6%8e%a5%e5%8f%a3)
  - [Thread 构造函数](#thread-%e6%9e%84%e9%80%a0%e5%87%bd%e6%95%b0)
    - [name](#name)
    - [init](#init)
    - [ID](#id)
    - [优先级](#%e4%bc%98%e5%85%88%e7%ba%a7)
    - [stackSize](#stacksize)
    - [ThreadGroup](#threadgroup)
  - [启动线程](#%e5%90%af%e5%8a%a8%e7%ba%bf%e7%a8%8b)
  - [ThreadGroup 解析](#threadgroup-%e8%a7%a3%e6%9e%90)
    - [构造函数](#%e6%9e%84%e9%80%a0%e5%87%bd%e6%95%b0)
    - [ThreadGroup 方法](#threadgroup-%e6%96%b9%e6%b3%95)

## Thread 对象

Java 中线程由 `Thread` 对象表示。使用 Thread 对象创建并发程序有两种基本策略：

- 直接控制线程的创建和管理，在程序需要异步任务时实例化 `Thread`。
- 将线程管理从程序中抽象出来，将任务传递给 `executor`。

## 创建线程

在 Java 中有两种实现线程业务逻辑的方式：

- 扩展 `Thread` 类实现 `run()` 方法
- 实现 `Runnable` 接口

两种方法都通过 `Thread` 的 `start()` 启动线程。

### 继承 Thread

`Thread` 类实现了 `Runnable` 接口，但是其 `run()` 方法什么都没干。一个应用可以继承 `Thread`，然后覆盖 `run()` 方法。

缺点：分离不好，重用性不好，比实现 `Runnable` 差。

优点：在简单应用中使用更方便。

线程的 `run()` 方法执行完毕后，线程执行结束。

实现方法：继承Thread 类，覆盖 `run()` 方法，通过调用 `start()` 启动线程。

```java
public class FirstThread{
    public static void main(String[] args){
        HelloThread t = new HelloThread(10);
        t.start();
    }
}

class HelloThread extends Thread{
    private int max;
    public HelloThread(int max){
        this.max = max;
    }

    @Override
    public void run(){
        for (int i = 0; i < max; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }
}
```

### 实现 Runnable 接口

`Runnable` 接口是 java 中用来实现需要并发执行任务的接口。任何实现线程功能的类都必须实现该接口。该接口仅定义了 `run()` 方法，用于包含在线程中运行的代码。

将`Runnable` 对象作为参数传递给 `Thread` 构造函数执行线程。

```java
public class FirstRunner{
    public static void main(String[] args){
        HelloRunner runner = new HelloRunner(1000);
        Thread thread = new Thread(runner, "线程一");
        thread.start();
    }
}

class HelloRunner implements Runnable{
    private int max;

    public HelloRunner(int max){
        this.max = max;
    }

    @Override
    public void run(){
        for (; max > 0; max--) {
            System.out.println(Thread.currentThread().getName() + ":" + max);
        }
    }
}
```

多线程编程具有如下特征：

- 多线程源于一个Runnable实例。
- 线程共享同样的数据和代码。

示例

```java
HelloRunner r = new HelloRunner(1000);
Thread t1 = new Thread(r, "[线程 One]");
Thread t2 = new Thread(r, "[线程 Two]");
```

## Thread 构造函数

Thread 类提供了 9 个构造函数，其中 8 个是 `public` 的：

```java
public Thread() {
    init(null, null, "Thread-" + nextThreadNum(), 0);
}
public Thread(Runnable target) {
    init(null, target, "Thread-" + nextThreadNum(), 0);
}
public Thread(Runnable target, String name) {
    init(null, target, name, 0);
}
public Thread(String name) {
    init(null, null, name, 0);
}
public Thread(ThreadGroup group, Runnable target) {
    init(group, target, "Thread-" + nextThreadNum(), 0);
}
public Thread(ThreadGroup group, Runnable target, String name) {
    init(group, target, name, 0);
}
public Thread(ThreadGroup group, Runnable target, String name,
                long stackSize) {
    init(group, target, name, stackSize);
}
public Thread(ThreadGroup group, String name) {
    init(group, null, name, 0);
}
```

下面依次说明构造函数各个参数的含义。

### name

`name` 是线程的名称，其默认名称为 "Thread-"+num。例如：

```java
public static void main(String[] args){
    IntStream.range(0, 5).boxed()
            .map(index -> new Thread(() -> System.out.println(Thread.currentThread().getName())))
            .forEach(Thread::start);
}
```

创建的线程采用了默认名称，可能输出：

```cmd
Thread-0
Thread-3
Thread-2
Thread-1
Thread-4
```

因为线程执行顺序问题，上面的输出顺序可能有所不同。

给线程取一个合适的名称，有助于跟踪错误信息，所以在需要的地方，建议给线程命名。

`Thread` 有多个构造函数可以指定名称，在线程 `start()` 前，还可以通过 `setName` 方法设置名称。下面是构造时命名实例：

```java
private static final String PREFIX = "MyThread-";

@Test
void testName(){
    IntStream.range(0, 3).mapToObj(ThreadName::createThread).forEach(Thread::start);
}

private static Thread createThread(int intName){
    return new Thread(() -> System.out.println(Thread.currentThread().getName()), PREFIX + intName);
}
```

输出：

```cmd
MyThread-0
MyThread-1
MyThread-2
```

### init

从前面的构造函数可以看到，`Thread` 的所有构造函数都会调用 `init` 方法，源码如下：

```java
private void init(ThreadGroup g, Runnable target, String name,
                    long stackSize, AccessControlContext acc,
                    boolean inheritThreadLocals) {
    if (name == null) {
        throw new NullPointerException("name cannot be null");
    }

    this.name = name;

    Thread parent = currentThread();
    SecurityManager security = System.getSecurityManager();
    if (g == null) {
        /* Determine if it's an applet or not */

        /* If there is a security manager, ask the security manager
            what to do. */
        if (security != null) {
            g = security.getThreadGroup();
        }

        /* If the security doesn't have a strong opinion of the matter
            use the parent thread group. */
        if (g == null) {
            g = parent.getThreadGroup();
        }
    }

    /* checkAccess regardless of whether or not threadgroup is
        explicitly passed in. */
    g.checkAccess();

    /*
        * Do we have the required permissions?
        */
    if (security != null) {
        if (isCCLOverridden(getClass())) {
            security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
        }
    }

    g.addUnstarted();

    this.group = g;
    this.daemon = parent.isDaemon();
    this.priority = parent.getPriority();
    if (security == null || isCCLOverridden(parent.getClass()))
        this.contextClassLoader = parent.getContextClassLoader();
    else
        this.contextClassLoader = parent.contextClassLoader;
    this.inheritedAccessControlContext =
            acc != null ? acc : AccessController.getContext();
    this.target = target;
    setPriority(priority);
    if (inheritThreadLocals && parent.inheritableThreadLocals != null)
        this.inheritableThreadLocals =
            ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
    /* Stash the specified stack size in case the VM cares */
    this.stackSize = stackSize;

    /* Set thread ID */
    tid = nextThreadID();
}
```

可以看到，在构造线程时，会通过 `currentThread()` 获取当前线程的父线程，该方法源码如下：

```java
public static native Thread currentThread();
```

即当前线。，对 `init` 方法来说，就是当前正在创建 `Thread` 的线程。

如果在 main 方法中创建线程，当前线程的父线程就是 main 线程。

### ID

在 `init()` 方法最后给线程分配了一个ID，ID是JVM用来识别线程的编号。在初始化时自动分配，无法修改。

### 优先级

每个线程都有一个优先级值，该值提示线程调度程序该线程的重要程序。

当线程调度程序需要选择一个线程执行，会倾向于选择优先级最高的程序。不过优先级的使用很有限，Sun 为 Linux 提供的 Java 虚拟机，线程的优先级被忽略。

- 多线程系统自动为每个线程分配一个优先级，缺省时，继承其父线程的优先级。
- 任务紧急的线程，其优先级较高。
- 同优先级的线程，按“先进先出”的原则。
- 优先级高的先执行，低的后执行。

一般不建议修改线程的优先级，因为并不能达到你想要的效果。

Thread 类有三个与优先级相关的静态量：

- `MAX_PRIORITY`, 最大优先级，10
- `MIN_PRIORITY`, 最小优先级，1
- `NORM_PRIORITY`, 默认优先级，5

设置优先级 `setPriority(int priorityLevel)`，优先级范围从 1 到 10.

### stackSize

为新线程的堆栈大小，如果未0，表示忽略该参数。

### ThreadGroup

`ThreadGroup` 将多个线程组合到一个对象。从下面源码可以看出，如果没有指定分组，自动分组到父线程所在的线程组。所谓父线程，就是启动该线程的线程，比如在 `main` 方法中调用 `.start()`，其父线程就是 `main` 线程。

```java
Thread parent = currentThread();
SecurityManager security = System.getSecurityManager();
if (g == null) {
    /* Determine if it's an applet or not */

    /* If there is a security manager, ask the security manager
        what to do. */
    if (security != null) {
        g = security.getThreadGroup();
    }

    /* If the security doesn't have a strong opinion of the matter
        use the parent thread group. */
    if (g == null) {
        g = parent.getThreadGroup();
    }
}
```

下面在单元测试：

```java
@Test
public void testThreadGroup(){

    Thread t1 = new Thread("t1");
    ThreadGroup group = new ThreadGroup("group1");
    Thread t2 = new Thread(group, "t2");
    ThreadGroup testGroup = Thread.currentThread().getThreadGroup();
    assertEquals(testGroup.getName(), "main");
    assertSame(testGroup, t1.getThreadGroup());
    assertSame(group, t2.getThreadGroup());
    assertNotEquals(testGroup, t2.getThreadGroup());
}
```

可以发现，TestNG 测试属于 main `ThreadGroup`，`t1` 由于没有指定分组，所以默认属于 main 分组，`t2` 指定了分组，所以归属于指定的 `testGroup`。

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
