# 方法的同步

2023-06-16, 14:52
****
## 1. 简介

数据争用、内存不一致等多线程问题，可以通过临界区解决。**临界区**（critical section）是访问共享资源的代码块，不能被多个线程同时执行。Java 等语言通过同步实现临界区。

当一个线程需要访问临界区，它通过同步机制来确定是否有其它线程正在执行临界区：

- 如果为否，该线程进入临界区；
- 如果为是，该线程暂停执行，直到持有临界区的线程执行完临界区代码

当有多个线程等待同一个临界区，JVM 会选择其中一个线程执行，其它线程继续等待。

Java 平台的任意一个对象都有一个与之关联的锁，这种锁被称为监视器（monitor）或**内部锁**（intrisic lock）。内部锁是一种排它锁，能够保证原子性、可见性和有序性。

内部锁通过 `synchronized` 关键字实现，可用于方法或代码块的并发访问。synchronized 修饰的整个代码块就是临界区。

## 2. 方法同步

**示例：** 方法同步

```java
public class SafeCircularSeqGenerator implements CircularSeqGenerator {
    private short sequence = -1;
    public synchronized short nextSequence() {
        if (sequence >= 999) {
            sequence = 0;
        } else {
            sequence++ ;
        }
        return sequence ;
    }
}
```

由于 `nextSequence()` 会被多个客户端线程执行，因此 `sequence` 实例变量就成为这些线程的共享数据。`synchronized` 会确保 `nextSequence()` 一次只能被一个线程执行（锁的排它性），因此 `nextSequence()` 的客户端线程在执行该方法实际上是串行的（整体并发中的局部串行），这就排除了对 `sequence`  变量访问操作的交错的可能性。另外，锁对可见性的保障使得  `nextSequence()` 的当前执行线程对 `sequence` 变量的更新对该方法的下一个执行线程可见。因此，`synchronized` 方法保障了线程安全（原子性和可见性）。

## 3. 代码块同步

`synchronized` 关键字修饰的代码块称为同步块（synchronized block），其语法为：

```java
synchronized (锁句柄) {
    //在此代码块中访问共享数据
}
```

`synchronized` 关键字内部的代码块就是临界区。**锁句柄**是一个对象引用（或能够返回对象的表达式）。习惯上直接称锁句柄为锁，锁句柄对应的监视器为相应同步块的**引导锁**，相应的同步块为该锁引导的同步块。

当对方法使用 `synchronized` 关键字，锁句柄隐式为 `this`。

**示例：** 上面的 nextSequence() 等价于

```java
public short nextSequence() {
    synchronized (this) {
        if (sequence >= 999) {
            sequence = 0;
        } else {
            sequence++;
        }
        return sequence ;
    }
}
```

作为锁句柄的变量通常采用 final 字段。因为锁句柄变量的值一旦改变，会导致执行同一个同步块的多个线程实际上使用不同的锁，从而导致竞态。因此，通常使用 private final 作为所句柄变量。

## 4. static 方法

同步静态方法以当前类对象（Java 中的类本身也是一个对象）为锁。例如：

```java
public class SynchronizedMethodExample {
    public static synchronized void staticMethod() {
        //在此访问共享数据
    }
}
```

相当于：

```java
public class SynchronizedMethodExample {
    public static void staticMethod() {
        synchronized (SynchronizedMethodExample.class) {
            //在此访问共享数据
        }
    }
}
```

线程在指定临界区代码时必须持有该临界区的锁。执行完临界区代码后自动释放锁。在该过程中，线程对内部锁的申请与释放由 JVM 完成，所以 synchronized 称为内部锁。

static 方法的锁为类对象，而实例方法的锁为实例对象。所以当一个线程访问 `synchronized` 声明的 `static` 方法时，其它线程可以访问该对象的其它非 `static` 方法。即两个 `synchronized` 方法，一个是 static，一个不是，那么两个线程可以同时分别访问这两个方法。如果两个方法都更改了相同数据，就可能出现数据不一致的错误。

使用 `synchronized` 保护代码块，必须指定对象引用。通常使用 this 关键字引用执行方法的对象，但也可以使用其它对象引用。这类对象通常是专门为此创建，且应用为 private。例如，如果一个类中有两个独立属性由多个线程共享，则应该同步访问每个变量；但是，应该支持一个线程访问其中一个属性，另一个线程同时访问另一个属性。如果使用 `this` 将当前对象作为引用对象，可能会干扰其它同步代码。

## 2. 示例

使用 `synchronized` 实现一个模拟停车场，它通过传感器检测车辆的进出，存储车辆停放的统计数据，以及现金流的控制。

下面实现两个版本，一个不使用同步机制，看看如何得到错误的结果，另一个使用 `synchronized` 实现。

**无同步版本**

- `ParkingCash` 负责收钱

```java
public class ParkingCash {
    private static final int cost = 2;

    // 赚的钱
    private long cash;

    public ParkingCash() {
        cash = 0;
    }

    // 当有车离开时调用，收钱
    public void vehiclePay() {
        cash += cost;
    }

    // 将 cash 输出到控制台，然后初始化为 0
    public void close() {
        System.out.printf("Closing accounting\n");
        long totalAmmount;
        totalAmmount = cash;
        cash = 0;
        System.out.printf("The total ammount is : %d", totalAmmount);
    }
}
```

```java
public class ParkingStats {
     // 停车场目前 car 和 motor 数目
    private long numberCars;
    private long numberMotorcycles;

    private ParkingCash cash;

    public ParkingStats(ParkingCash cash) {
        numberCars = 0;
        numberMotorcycles = 0;
        this.cash = cash;
    }

	// 车进
    public void carComeIn() {
        numberCars++;
    }

	// 车出
    public void carGoOut() {
        numberCars--;
        cash.vehiclePay();
    }

    public void motoComeIn() {
        numberMotorcycles++;
    }

    public void motoGoOut() {
        numberMotorcycles--;
        cash.vehiclePay();
    }

    public long getNumberCars() {
        return numberCars;
    }

    public long getNumberMotorcycles() {
        return numberMotorcycles;
    }
}
```

- 最重要的任务类

```java
import java.util.concurrent.TimeUnit;

public class Sensor implements Runnable {

    private ParkingStats stats;

    public Sensor(ParkingStats stats) {
        this.stats = stats;
    }

    @Override
    public void run() {
        // 模拟 2 车 1 摩托的进出，模拟 10 次
        for (int i = 0; i < 10; i++) {
            stats.carComeIn();
            stats.carComeIn();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stats.motoComeIn();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stats.motoGoOut();
            stats.carGoOut();
            stats.carGoOut();
        }
    }
}
```

- main 实现

```java
public class Main {
    public static void main(String[] args) {

        ParkingCash cash = new ParkingCash();
        ParkingStats stats = new ParkingStats(cash);

        System.out.printf("Parking Simulator\n");
        int numberSensors = 2 * Runtime.getRuntime().availableProcessors();
        Thread threads[] = new Thread[numberSensors];
        for (int i = 0; i < numberSensors; i++) {
            Sensor sensor = new Sensor(stats);
            Thread thread = new Thread(sensor);
            thread.start();
            threads[i] = thread;
        }

		// 等待这些线程执行结束
        for (int i = 0; i < numberSensors; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

		// 输出统计信息
        System.out.printf("Number of cars: %d\n", stats.getNumberCars());
        System.out.printf("Number of motorcycles: %d\n", stats.getNumberMotorcycles());
        cash.close();
    }
}
```

```
Parking Simulator
Number of cars: -1
Number of motorcycles: -4
Closing accounting
The total ammount is : 462
```

对 4 核电脑，有 8 个 Sensor，每个 Sensor 迭代 10 次，每次进去 3 车，出来 3 车，因此每个 Sensor 对应 30 车。

如果运行正常，最后输出：

- 停车场没有车
- 8 个 Sensor 执行，每个模拟 30 次，每次收费 2 元，最终 480 元

然而结果并非如此。这是因为所有的 Sensor 共享 ParkingStats，没有同步保护。

**同步保护**

- 首先用 `synchronized` 保护 `vehiclePay()` 方法

```java
public synchronized void vehiclePay() {
	cash += cost;
}
```

- 然后保护 `close()` 方法中的 `totalAmmount` 赋值部分

```java
public void close() {
	System.out.printf("Closing accounting\n");
	long totalAmmount;
	synchronized (this) {
		totalAmmount = cash;
		cash = 0;
	}
	System.out.printf("The total ammount is : %d", totalAmmount);
}
```

- 再在 `ParkingStats` 添加两个属性作为同步引用对象

对所有修改属性的访问，都用同步保护起来。

```java
public class ParkingStats {
    private long numberCars;
    private long numberMotorcycles;

    // ControlCars 用于同步 numberCars
    // controlMotorcycles 用于同步 numberMotorcycles
    private final Object controlCars, controlMotorcycles;

    private ParkingCash cash;

    public ParkingStats(ParkingCash cash) {
        numberCars = 0;
        numberMotorcycles = 0;
        controlCars = new Object();
        controlMotorcycles = new Object();
        this.cash = cash;
    }

    public void carComeIn() {
        synchronized (controlCars) {
            numberCars++;
        }
    }

    public void carGoOut() {
        synchronized (controlCars) {
            numberCars--;
        }
        cash.vehiclePay(); // 这个方法已被保护
    }

    public void motoComeIn() {
        synchronized (controlMotorcycles) {
            numberMotorcycles++;
        }
    }

    public void motoGoOut() {
        synchronized (controlMotorcycles) {
            numberMotorcycles--;
        }
        cash.vehiclePay();
    }

    public long getNumberCars() { // 这个方法不保护也没事
        synchronized (controlCars) {
            return numberCars;
        }
    }

    public long getNumberMotorcycles() {
        synchronized (controlMotorcycles) {
            return numberMotorcycles;
        }
    }
}
```

`Sensor` 和 `main` 不用修改。再次运行示例：

```
Parking Simulator
Number of cars: 0
Number of motorcycles: 0
Closing accounting
The total ammount is : 480
```

上面演示了 `synchronized` 的做种用法：

- 用 `synchronized` 保护 `vehiclePay()` 方法。当多个 `Sensor` 任务同时调用该方法，只有一个执行，其它等待。从而确保最终金额正确。
- 使用两个不同的对象对 `numberCars` 和 `numberMotorcycles` 进行保护。这样，一个 `Sensor` 在修改 `numberCars` 时，另一个可以同时修改 `numberMotorcycles`。

## 3. 总结

`synchronized` 会降低应用性能，因此应该只在并发应用修改共享数据的方法中使用。如果你确信某个方法只会被一个线程访问，那没必要同步。

在 `synchronized` 保护的临界区，要避免调用 blocking 操作，如 I/O 操作。

`synchronized` 关键字用于锁定对象，`synchronized` 代码块是原子操作。例如：

```java
public class SynObj{
    private int count = 10;
    private Object o = new Object();

    public void m(){
        synchronized (o) { // 任何线程要执行下面的代码，必须先拿到 o 的锁
            count--;
            System.out.println(Thread.currentThread().getName() + " count = " + count);
        }
    }
}
```

这里创建 `Object` 作为锁对象。

也可以使用自身作为锁对象。

```java
public class SynThisLock{
    private int count = 10;

    public void m(){
        synchronized (this) { // 任何线程要执行下面的代码，必须先拿到 this 的锁
            count--;
            System.out.println(Thread.currentThread().getName() + " count = " + count);
        }
    }
}
```

如果从方法的开始就使用 `synchronized(this)`，上面还可以简化为：

```java
public class SynMethod{
    private int count = 10;

    public synchronized void m(){ // 等价于从方法开始使用 synchronized(this)
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }
}
```

静态的 `synchronized` 方法的锁是类的 Class。如：

```java
public class Test{
    private static int count = 10;

    public synchronized static void m(){ // 这里等同于 synrhonized(Test.class)
        count--;
        System.out.println(Thread.currentThread.getName() + " count = " + count);
    }

    public static void mm(){
        synchronized(Test.class){
            count--;
        }
    }
}
```
