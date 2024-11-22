# 原子变量

## 简介

Java 5 引入了原子变量，以提供对单个变量的原子操作。

## CAS

CAS（compare and swap）是一种对处理器指令的称呼。许多 Java 多线程实现都会借助 CAS。

例如，使用内部锁实现计数器的原子性：

```java
public void increment(){
    synchronized (this){
        count++;
    }
}
```

这种使用锁来保障原子型，有点杀鸡用牛刀的感觉，毕竟其开销较大。CAS 能够将 read-modify-write 和 check-and-act 之类的操作转换为原子操作。

上面的 `increment()` 是一个典型的 read-modify-write 操作，它可以由 CAS 转换为一般性的 if-then-act 操作，并由处理器保障该操作的原子型。其基本假设为：当线程 A 执行 CAS 操作时，如果变量 `v` 的当前值和线程 A 调用 CAS 时提供的值相等，就说明其它线程没有修改变量 `v` 的值，线程 A 就能够安全的更新 `v` 的值；而其它线程的更新请求则失败。失败的线程可以再次尝试，直到成功。

## LongAdder

`AtomicLong` 是在高并发下对单个变量进行 CAS 操作，从而保证其原子性：

```java

```

## 参考

