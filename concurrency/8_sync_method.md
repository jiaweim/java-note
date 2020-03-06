# Synchronization

Java 提供了两种基本的同步语法：

- 同步方法
- 同步语句

其中同步语句更为复杂。

## 同步方法


## 实例

### 同步方法和非同步方法是否可以同时调用

例如：

```java
public class SynchronizedYN{

    public synchronized void m1(){
        System.out.println(Thread.currentThread().getName() + " m1 start...");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " m1 end");
    }

    public void m2(){
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " m2 ");
    }

    public static void main(String[] args){
        SynchronizedYN yn = new SynchronizedYN();

        new Thread(yn::m1, "t1").start();
        new Thread(yn::m2, "t2").start();
    }
}
```

访问非 `synchronized` 方法不需要锁，所以在访问 `synchronized` 方法时，可以同时访问非同步方法。

### 对写加锁，对读不加锁

在写操作没完成，就读取，会导致读取的不是上次操作的结果。

```java
/**
 * 对业务写方法加锁
 * 对业务读方法不加锁
 * 容易产生脏读问题（dirtyRead）
 */
public class Account2
{
    private String name;
    double balance;

    public synchronized void set(String name, double balance)
    {
        this.name = name;
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.balance = balance;
    }

    public double getBalance(String name)
    {
        return this.balance;
    }

    public static void main(String[] args)
    {
        Account2 account2 = new Account2();
        new Thread(() -> account2.set("zhangshan", 100)).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(account2.getBalance("zhangshan")); // 0.0

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(account2.getBalance("zhangshan")); // 100.0
    }
}
```

### 一个同步方法可以调用另一个同步方法

一个线程已经拥有某个对象的锁，再次申请的时候仍然会得到对象的锁。即 `synchronized` 获得的锁是可重入的。

例如，下面的代码没有问题。

```java
public class T1
{
    synchronized void m1()
    {
        System.out.println("m1 start");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        m2();
    }

    synchronized void m2()
    {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("m2");
    }
}
```

还可能出现子类调用父类的同步方法，这也是可以的。

### 异常处理

程序在执行过程中，如果出现异常，默认情况锁会被释放。

所以在并发处理过程中，有异常要多加小心，不然可能会出现不一致的情况。

比如，在一个 web app 处理过程中，多个 servlet 线程共同访问同一个资源，这时如果异常处理不合适，在第一个线程抛出异常，其它线程就会进入同步代码区，有可能会访问到异常产生的数据。

因此要非常小心的处理同步业务逻辑中的异常。