# 方法的同步

`synchronized` 关键字是 Java 提供的最简单的同步方式，可用于方法或代码块的并发访问。

所有 `synchronized` 语句都需要一个对象引用。当对方法使用 `synchronized` 关键字，对象引用是隐式的。当对一个对象的一个或多个方法使用 `synchronized` 关键字，只有一个线程能够访问这些方法。其它试图访问相同对象的 `synchronized` 方法的线程被挂起。

换句话说，`synchronized` 关键字声明的每个方法都是临界区，而 Java 一次只允许执行对象的其中一个临界区方法。此时的对象引用是 `this` 关键字表示的对象自身。`static` 方法的行为不同，只能有一个线程访问 `synchronized` 声明的 `static` 方法，但是其它线程可以访问该类对象的其它非 `static` 方法。即两个 synchronized 方法，一个是 static，一个不是，那么两个线程可以同时分别访问这两个方法。如果两个方法都更改了相同数据，就可能出现数据不一致的错误。

使用 synchronized 关键字保护代码块，必须指定对象引用。通常使用 this 关键字引用执行方法额对象，但也可以使用其它对象引用。这类对象通常是专门为此而创建，且应用保持 private。例如，如果一个类中有两个独立属性由多个线程共享，则必须同步访问每个变量；但是，应该支持一个线程访问其中一个属性，另一个线程同时访问另一个属性。如果使用 this 作为引用对象，可能会干扰其它同步代码。

### 示例

使用 synchronized 关键字来实现一个模拟停车场，它使用传感器检测车辆的进出，存储车辆停放的统计数据，以及现金流的控制。

下面实现两个版本，一个不使用同步机制，看看如何得到错误的结果，另一个使用 synchronized 关键字实现。

- 无同步版本

```java
public class ParkingCash {

    private static final int cost = 2;

    // 赚的钱
    private long cash;

    public ParkingCash() {
        cash = 0;
    }

    /**
     * 当有车离开时调用，收钱
     */
    public void vehiclePay() {
        cash += cost;
    }

    /**
     * 将 cash 输出到控制台，然后初始化为 0
     */
    public void close() {
        System.out.printf("Closing accounting");
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

    /**
     * Constructor of the class
     */
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

