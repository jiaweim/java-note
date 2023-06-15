
# Thread 方法

| 方法                              | 说明                                                                                                                               |
| --------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| static void Thread.sleep()        | 暂停**当前线程**，休眠指定时间。该方法不改变锁行为                                                                                 |
| static void yield()               | 终止当前线程的执行：即使当前执行线程处于让步状态，如果有其他的可运行线程具有至少与此线程同样高的优先级，那么这些线程接下来会被调度 |
| void setPriority(int newPriority) | 设置线程的优先级。优先级在 `Thread.MIN_PRIORITY` 与 `Thread.MAX_PRIORITY` 之间。一般使用 `Thread.NORM_PRIORITY`  优先级            |
| `static Thread currentThread()`   | 返回代表当前执行线程的 Thread 对象                                                                                                 |
| void interrupt()                  | 向线程发送中断请求。线程的中断状态被设置为 true。如果当前线程被阻塞，抛出 `InterruptedException` 异常抛出                          |
| `static boolean interrupted()`    | 检测当前线程是否中断，并清除线程的中断状态，即调用后，线程的状态不再是中断状态                                                     |
| `boolean isInterrupted()`         | 检测当前线程是否被中断，不改变中断状态 flag。如果线程被阻塞，该方法产生 `InterruptedException`                                     |
| isAlive                           | 确定当前线程是否在运行。注意：Sleep 状态也是alive的                                                                                |

## wait

`wait`, `notify` 和 `nontifyAll` 都是 `Object` 的方法，而且都是 `native` 方法。它们必须在 `synchronized` 方法或代码块中调用，否则报错。

`wait` 方法使线程进入等待状态，并且会释放该线程持有的锁。直到其它线程调用该线程的 `notify()` 或 `notifyAll()` 方法唤醒该线程。

Thread.sleep 和 Object.wait 都会暂停当前线程，对于CPU来说，不管是哪种方式暂停的线程，都表示它暂时不需要CPU执行时间。OS 会将执行时间分配给其他线程。区别是，调用 `wait` 后，需要别的线程 `notify/notifyAll` 才能够重新获得CPU执行时间。

## yield

`yield` 属于建议式方法，告诉调度器我愿意放弃当前CPU资源，如果CPU资源不紧张，该提醒被忽略。`yield` 会使线程从 RUNNING 状态切换到 RUNNABLE 状态。
