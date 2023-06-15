# 线程基础

2023-06-14
*****
## 简介

在同一个进程中运行的**并发任务**称为线程，在 Java 中线程由 `Thread` 对象表示。使用 `Thread` 对象创建并发程序有两种基本策略：

- 直接控制线程的创建和管理，在程序需要异步任务时实例化 `Thread`
- 将线程管理从程序中抽象出来，将任务传递给 `executor`

## 线程属性

### ID

在 `init()` 方法最后给分配给线程的一个编号，JVM 通过编号识别线程。在线程初始化时自动分配，无法修改。

### name

是线程的名称，默认名称为 "Thread-"+num。例如：

```java
public static void main(String[] args){
    IntStream.range(0, 5).boxed()
            .map(index -> new Thread(() -> System.out.println(Thread.currentThread().getName())))
            .forEach(Thread::start);
}
```

创建的线程采用了默认名称，可能输出：

```
Thread-0
Thread-3
Thread-2
Thread-1
Thread-4
```

因为线程执行顺序问题，上面的输出顺序可能有所不同。

给线程取一个合适的名称，有助于跟踪错误信息，所以在需要的地方，建议给线程命名。

`Thread` 有多个构造函数可以指定名称，在线程 `start()` 前，还可以通过 `setName` 方法设置名称。

### init

`Thread` 的所有构造函数都会调用 `init` 方法，源码如下：

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

对 `init` 方法来说，就是当前正在创建 `Thread` 的线程。如果在 main 方法中创建线程，当前线程的父线程就是 main 线程。

### 优先级

每个线程都有一个优先级，该值提示线程调度程序该线程的重要程序。

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

### status

存储线程的状态。在 Java 中，线程有 6 种状态，定义在 `Thread.State` enum 中：

| 状态          | 说明                       |
| ------------- | -------------------------- |
| NEW           | 线程已创建，但还没启动     |
| RUNNABLE      | 线程正在 JVM 中运行        |
| BLOCKED       | 线程被阻塞                 |
| WAITING       | 线程在等待另一个线程       |
| TIMED_WAITING | 在指定时间内等待另一个线程 |
| TERMINATED    | 线程已执行完毕             |

## 创建线程

在 Java 中创建线程的方式有两种：

- 扩展 `Thread` 类实现 `run()` 方法
- 实现 `Runnable` 接口，推荐方式

两种方法都通过 `Thread` 的 `start()` 启动线程。

Thread 属性

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

## 示例

创建并运行 10 个线程，计算 20000 以内的质数。

- 执行计算任务的 `Calculator` 类

因为希望并发执行，因此实现 `Runnable` 接口。

```java
public class Calculator implements Runnable {

    @Override
    public void run() {
        long current = 1L;
        long max = 20000L;
        long numPrimes = 0L;

        System.out.printf("Thread '%s': START\n", Thread.currentThread().getName());
        while (current <= max) {
            if (isPrime(current)) {
                numPrimes++;
            }
            current++;
        }
        System.out.printf("Thread '%s': END. Number of Primes: %d\n", Thread.currentThread().getName(), numPrimes);
    }

    /**
     * 判断一个数是否为质数
     */
    private boolean isPrime(long number) {
        if (number <= 2) {
            return true;
        }
        for (long i = 2; i < number; i++) {
            if ((number % i) == 0) {
                return false;
            }
        }
        return true;
    }
}
```

- 应用 main 类实现

```java
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;

public class Main {
    public static void main(String[] args) {

        // 线程优先级信息
        System.out.printf("Minimum Priority: %s\n", Thread.MIN_PRIORITY);
        System.out.printf("Normal Priority: %s\n", Thread.NORM_PRIORITY);
        System.out.printf("Maximun Priority: %s\n", Thread.MAX_PRIORITY);

        Thread threads[];
        Thread.State status[];

        // 创建 10 个线程
        // 其中 5 个优先级设到最大
        // 另 5 个优先级设到最小
        threads = new Thread[10];
        status = new Thread.State[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator());
            if ((i % 2) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("My Thread " + i);
        }

        // 等待线程结束，同时将线程状态写入文件
        try (FileWriter file = new FileWriter(".\\data\\log.txt"); 
	        PrintWriter pw = new PrintWriter(file);) {

            // 将线程状态写入文件
            for (int i = 0; i < 10; i++) {
                pw.println("Main : Status of Thread " + i + " : " 
		                + threads[i].getState());
                status[i] = threads[i].getState();
            }

            // 启动 10 个线程
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }

			// 等待线程结束
			// 当线程状态发生变化，将线程信息写入文件
            boolean finish = false;
            while (!finish) {
                for (int i = 0; i < 10; i++) {
                    if (threads[i].getState() != status[i]) {
                        writeThreadInfo(pw, threads[i], status[i]);
                        status[i] = threads[i].getState();
                    }
                }

                finish = true;
                for (int i = 0; i < 10; i++) {
                    finish = finish && (threads[i].getState() == State.TERMINATED);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将线程状态写入文件
     * @param state  : 线程 old 状态
     */
    private static void writeThreadInfo(PrintWriter pw, Thread thread, State state) {
        pw.printf("Main : Id %d - %s\n", thread.getId(), thread.getName());
        pw.printf("Main : Priority: %d\n", thread.getPriority());
        pw.printf("Main : Old State: %s\n", state);
        pw.printf("Main : New State: %s\n", thread.getState());
        pw.printf("Main : ************************************\n");
    }
}
```

- 控制台输出

```
Minimum Priority: 1
Normal Priority: 5
Maximun Priority: 10
Thread 'My Thread 0': START
Thread 'My Thread 4': START
Thread 'My Thread 1': START
Thread 'My Thread 6': START
Thread 'My Thread 5': START
Thread 'My Thread 3': START
Thread 'My Thread 2': START
Thread 'My Thread 7': START
Thread 'My Thread 9': START
Thread 'My Thread 8': START
Thread 'My Thread 5': END. Number of Primes: 2263
Thread 'My Thread 1': END. Number of Primes: 2263
Thread 'My Thread 3': END. Number of Primes: 2263
Thread 'My Thread 7': END. Number of Primes: 2263
Thread 'My Thread 2': END. Number of Primes: 2263
Thread 'My Thread 9': END. Number of Primes: 2263
Thread 'My Thread 8': END. Number of Primes: 2263
Thread 'My Thread 6': END. Number of Primes: 2263
Thread 'My Thread 0': END. Number of Primes: 2263
Thread 'My Thread 4': END. Number of Primes: 2263
```

偶数线程的优先级最高，所以第一个执行的线程是编号为偶数的线程，但是从后面输出可以看出，JVM 并没有严格按照优先级来执行线程。

- 文件输出

下面是输出文件的部分内容

```
Main : Status of Thread 5 : NEW  
Main : Status of Thread 6 : NEW  
Main : Status of Thread 7 : NEW  
Main : Status of Thread 8 : NEW  
Main : Status of Thread 9 : NEW  
Main : Id 25 - My Thread 0  
Main : Priority: 10  
Main : Old State: NEW  
Main : New State: RUNNABLE  
Main : ************************************  
Main : Id 26 - My Thread 1  
Main : Priority: 1  
Main : Old State: NEW  
Main : New State: RUNNABLE  
Main : ************************************  
Main : Id 27 - My Thread 2  
Main : Priority: 10  
Main : Old State: NEW  
Main : New State: RUNNABLE  
Main : ************************************
```

每个 Java 线程至少有一个执行线程。运行程序时，JVM 运行执行线程调用 `main()` 方法。

当调用 `Thread` 的 `start() `方法，就创建了另一个执行线程。`Thread` 类存储有线程相关的所有信息。操作系统的调度器利用线程的优先级为线程分配 CPU 时间。

线程的 ID 和 status 不能修改，ID 是初始化线程时自动创建，status 根据线程状态自动设置。

当所有线程执行完毕，Java 程序结束（确切地说是所有的非守护线程）。出现线程（执行 `main()` 方法的线程）结束了，其它线程还可以继续执行。如果其中一个线程调用了 `System.exit()` 方法，所有线程都被强制结束。

创建 `Thread` 类的对象不会创建新的执行线程，调用实现 `Runnable` 接口类的 `run()` 方法也不会创建新的执行线程，只有调用 `Thread` 的 `start()` 方法才会创建新的执行线程。

```ad-summary
这个示例主要演示如何创建线程，以及如何查询线程信息。其中创建的不同线程没有共享资源，因此没有数据争用等并行应用的问题，所以也不需要同步。
```

## 总结

- 通过实现 `Runnable` 接口创建并发任务。
- 建议给线程设置有意义的名称。
- 调用 `Thread.start()` 启动线程