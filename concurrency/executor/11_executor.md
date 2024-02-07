# 线程池

## 线程池实现

为了更好的理解线程池，下面自定义实现一个简单的线程池。

线程池接口定义：

```java
public interface JThreadPool{

    /**
     * submit task to thread pool
     */
    void execute(Runnable runnable);

    /**
     * close thread pool
     */
    void shutdown();

    /**
     * @return the initial size of the thread pool
     */
    int getInitSize();

    /**
     * @return the allowed maximum number of threads
     */
    int getMaxSize();

    /**
     * @return the number of core threads
     */
    int getCoreSize();

    /**
     * @return the queue size
     */
    int getQueueSize();

    /**
     * @return the number of active threads
     */
    int getActiveCount();

    /**
     * @return true if this pool is shutdown
     */
    boolean isShutdown();
}
```

任务队列接口：

```java
public interface JTaskQueue{

    /**
     * add a task to the queue
     */
    void offer(Runnable runnable);

    /**
     * @return task a task from the header
     */
    Runnable take();

    /**
     * @return number of task in this queue
     */
    int size();
}
```

线程工厂接口：

```java
public interface JThreadFactory{
    /**
     * Create a Thread for a task
     */
    Thread createThread(Runnable runnable);
}
```

拒绝策略，当队列已满时，对新添加的任务采取的策略：

```java
public interface JDenyPolicy{
    void reject(Runnable runnable, JThreadPool threadPool);

    /**
     * 直接舍弃，不作其它处理
     */
    class JDiscardDenyPolicy implements JDenyPolicy{
        @Override
        public void reject(Runnable runnable, JThreadPool threadPool)
        {
            // do nothing
        }
    }

    /**
     * 舍弃并抛出异常
     */
    class JAbortDenyPolicy implements JDenyPolicy{

        @Override
        public void reject(Runnable runnable, JThreadPool threadPool){
            throw new JRunnableDenyException("The task " + runnable + " is abort.");
        }
    }

    /**
     * 接受并执行
     */
    class JRunnerDenyPolicy implements JDenyPolicy{
        @Override
        public void reject(Runnable runnable, JThreadPool threadPool){
            if (!threadPool.isShutdown()) {
                runnable.run();
            }
        }
    }
}
```

对应的异常为：

```java
public class JRunnableDenyException extends RuntimeException
{
    public JRunnableDenyException(String message)
    {
        super(message);
    }
}
```

JInternalTask，用于从队列中取出任务执行：

```java
public class JInternalTask implements Runnable
{
    private final JTaskQueue taskQueue;

    private volatile boolean running = true;

    public JInternalTask(JTaskQueue taskQueue)
    {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run()
    {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = taskQueue.take();
                task.run();
            } catch (Exception e) {
                running = false;
                break;
            }
        }
    }

    /**
     * 停止当前任务
     */
    public void  stop(){
        this.running = false;
    }
}
```

其中添加的 stop 方法，方面停止当前线程，用在维护线程池线程数目和销毁线程池方面。

## ExecutorService
|方法|说明|
|---|---|
|invokeAny()|接收一个任务列表，运行任务，返回第一个没有抛出异常的任务的执行结果|

## invokeAll
一次提交多个 `Callable` 任务执行。

## invokeAny
类似于

## submit vs. invokeAll
`submit` 用于一次提交一个任务，`invokeAll` 用于一次提交多个任务。

如果不需要等待所有任务完成，可以使用 `submit`，如果需要