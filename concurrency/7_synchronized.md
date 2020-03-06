# synchronized 方案

`synchronized` 关键字用于锁定对象。`synchronized` 代码块是原子操作。

例如：

```java
public class SynObj{
    private int count = 10;
    private Object o = new Object();

    public void m()
    {
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

静态的 `synchronized` 方法的锁是单签类的 Class.如：

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

## 注意事项

### 不要用 String 对象

不要以字符串常量作为锁定对象。