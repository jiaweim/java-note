# CountDownLatch

`CountDownLatch` 是一种同步辅助类，它使得一个或多个线程等待其它线程中特定操作完成。

`CountDownLatch` 以一个数值初始化。`countDown` 方法用于降低数值，在数值到 0 之前，所有 `await` 方法阻塞，数值到0后，所有的等待线程释放，后面调用 `await` 方法也会立刻返回。

计数是一次性的，即数值到0后无法重置，如果需要重置数值，可以考虑使用 `CyclicBarrier`。

## 功能

`CountDownLatch` 是一个多功能同步工具，具有多种用途。

以 1 初始化的 `CountDownLatch` 是个简单的开关，或门：所有调用 `await` 的线程在门口等着，直到 `countDown()` 的调用打开门。

以 N 初始化的 `CountDownLatch` 可以让一个线程阻塞，直到N个线程中某个任务完成，或者某个任务完成了N次。

`CountDownLatch` 的一个重要特性是，调用 `countDown()` 的线程不需要等到计算到0，它只是防止线程越过 `await` 方法。

## 实例

工作线程使用两个 `CountDownLatch`：

- 第一个是启动信号，保证 driver 准备好前工作线程暂停；
- 第二个是完成信号，让 driver 等待所有工作线程完成。

```java
class Driver { // ...
   void main() throws InterruptedException {
     CountDownLatch startSignal = new CountDownLatch(1);
     CountDownLatch doneSignal = new CountDownLatch(N);

     for (int i = 0; i < N; ++i) // create and start threads
       new Thread(new Worker(startSignal, doneSignal)).start();

     doSomethingElse();            // don't let run yet
     startSignal.countDown();      // let all threads proceed
     doSomethingElse();
     doneSignal.await();           // wait for all to finish
   }
 }

 class Worker implements Runnable {
   private final CountDownLatch startSignal;
   private final CountDownLatch doneSignal;
   Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
     this.startSignal = startSignal;
     this.doneSignal = doneSignal;
   }
   public void run() {
     try {
       startSignal.await();
       doWork();
       doneSignal.countDown();
     } catch (InterruptedException ex) {} // return;
   }

   void doWork() { ... }
 }
```

另一个经典用法是将问题分为 N 份，每个对应一个 `Runnable`，所有 `Runnable` 排队执行，当所有子部分完成，才能继续执行：

```java
class Driver2 { // ...
   void main() throws InterruptedException {
     CountDownLatch doneSignal = new CountDownLatch(N);
     Executor e = ...

     for (int i = 0; i < N; ++i) // create and start threads
       e.execute(new WorkerRunnable(doneSignal, i));

     doneSignal.await();           // wait for all to finish
   }
 }

 class WorkerRunnable implements Runnable {
   private final CountDownLatch doneSignal;
   private final int i;
   WorkerRunnable(CountDownLatch doneSignal, int i) {
     this.doneSignal = doneSignal;
     this.i = i;
   }
   public void run() {
     try {
       doWork(i);
       doneSignal.countDown();
     } catch (InterruptedException ex) {} // return;
   }

   void doWork() { ... }
 }
```
