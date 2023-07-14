# StampedLock

2023-06-18, 15:04
****
## 1. 简介

Java 8 引入了包含三种控制读写模式的 `StampedLock`，其功能与 `ReadWriteLock` 类似，但更强大。

`ReadWriteLock` 虽然分离了读和写，使得读与读之间可以完全并发。但是，读和写之间依然是冲突的。如果有线程正在读，写线程需要等待读线程释放锁，即读的过程不允许写，称为悲观读锁。如果有大量的读线程，它可能引起写线程的“饥饿”。

`StampedLock` 提供了乐观读锁，读的过程允许获取写锁。这样，读的数据就可能出现不一致，所以，需要额外的代码来判断读的过程是否有写入。

乐观锁乐观地估计读的过程大概率不会写入，悲观锁则是读的过程拒绝写入。乐观锁并发效率更高，但有可能因为写入导致读取的数据不一致，提供判断是否有写入的方法，再读一遍即可。

`StampedLock` 的状态包含版本（version）和模式（mode）。三种模式：

- 写锁：独占锁
- 悲观读锁：非独占锁，其它线程可持有悲观读锁或乐观读锁
- 乐观读锁：非占有锁，其它线程可以持有写锁。使用乐观锁访问共享数据，需使用 `validate()` 检查是否有写锁修改共享数据

获取锁：

- `readLock()`，悲观读锁，非独占锁，可能阻塞；返回 stamp 可用于解锁或转换模式
- `writeLock()`，写锁，独占锁，可能阻塞；返回 stamp 可用于解锁或转换模式

尝试获取锁：

- `tryOptimisticRead()`，乐观读锁，返回 stamp 可用于验证，写锁独占时返回 0；
- `tryReadLock()`，悲观读锁，返回 stamp 可用于解锁或转换模式，写锁独占时返回 0；
- `tryWriteLock()`，写锁，返回 stamp 可用于解锁或转换模式，锁不可用时返回 0

`StampedLock` 是不可重入锁，如 Lock 和 ReadWriteLock。

和 `ReadWriteLock` 相比，写锁完全一样，不同的是读锁。上面用 `tryOptimisticRead()` 获取乐观锁，返回一个 stamp，接着继续读，完成读操作，通过 `validate(stamp)` 验证 stamp。如果在读过程中没有写入，stamp 不变，验证成功，就可以继续

## 2. 示例

```java
import java.util.concurrent.locks.StampedLock;

public class Point {

    private final StampedLock stampedLock = new StampedLock();

    private double x;
    private double y;

    public void move(double deltaX, double deltaY) {
        long stamp = stampedLock.writeLock(); // 获取写锁，独占锁
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            stampedLock.unlockWrite(stamp); // 释放写锁
        }
    }

    public double distanceFromOrigin() {
        long stamp = stampedLock.tryOptimisticRead(); // 乐观读锁
        // 注意下面两行代码不是原子操作
        double currentX = x;
        double currentY = y;
        if (!stampedLock.validate(stamp)) { // 检查乐观读锁后是否有写入
            stamp = stampedLock.readLock(); // 获取悲观读锁
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
```

这里重点解释 `tryOptimisticRead()`，该方法尝试获取乐观读锁，返回的 stamp 可用于验证。在读取 x 和 y 值时：

```java
double currentX = x;
double currentY = y;
```

因为乐观锁允许写如，所以在读取 x 时，可能有其它线程修改了 y，使得 currentX 和 currentY 不一致。所以需要验证：

```java
stampedLock.validate(stamp)
```

如果在乐观读过程中没有写入操作，validate(stamp) 返回 true，即乐观读有效，可以跳转到最后使用读取的值：

```java
return Math.sqrt(currentX * currentX + currentY * currentY);
```

如果乐观读过程中有写入操作，validate(stamp) 返回 false，即乐观读无效。此时可以选择在一个循环中一直乐观读，直到成功为止，也可以如上例，升级锁的级别，使用悲观读：

```java
stamp = stampedLock.readLock(); // 获取悲观读锁
try {
	currentX = x;
	currentY = y;
} finally {
	stampedLock.unlockRead(stamp); // 释放悲观读锁
}
```

总而言之，StampedLock 通过引入乐观读来增加并行度。

## 3. 总结

- Writing

**`public long writeLock()`**

获取独占锁，必要时阻塞直到可用。

**`public boolean isWriteLocked()`**

当前为独占锁，返回 true.
