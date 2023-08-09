# 引用

2023-08-06, 22:55
****
## 1. 简介

**引用对象**封装了对其它对象的引用，可以像检查其它对象一样检查和操作引用本身。在使用 Java 编程时，我们一般使用强引用（strong reference），且大多数情况下强引用是最佳选择。但是，如果我们需要更好地控制垃圾收集器清理对象的时间，就可能需要其它引用类型。

对象的可达性（reachability）反映了对象的生命周期，从强到弱：

- **强**（strong）可达：某个线程可以在不遍历任何引用对象的情况下访问某个对象。新创建的对象对创建它的线程是强可达性
- **软**（soft）可达：通过遍历软引用可达的对象
- **弱**（weak）可达：通过遍历 weak 引用可达的对象。弱可达性对象的弱引用被清除时，弱可达对象就可以被清除
- **虚**（phantom）可达：

`java.lang.ref` 支持与垃圾回收器的有限交互，除了默认的强引用，提供了余下的三种引用类型：

- 软引用（soft-ref）
- 弱引用（weak-ref）
- 虚引用（phantom-ref）

soft-ref 用于实现内存敏感的缓存；weak-ref 用于实现不阻止 key (或 value)被回收的规范化映射；phantom-ref 用于调度事后清理操作。

每种引用类型实现为抽象类 `Reference` 的子类，如下所示。每个 `Reference` 对象封装了特定对象引用，称为 referent。每个 `Reference` 对象提供了获取和清除引用的方法。除了清理操作引用对象为immutable，因为不提供 set 操作。我们可以进一步继承这些子类，根据需要添加必要的字段和方法。

![](Pasted%20image%2020230806220339.png)

在创建引用对象时可以将引用对象注册到 reference queue (ref-queue)，从而在对象的可达性发生变化时得到通知。垃圾回收器

## 2. Finalizer

`FinalReference` 和 `Finalizer` 均为包内可见，为 JVM 提供，无法直接使用。JVM 对那些实现了 `finalize()` 方法的类实例化 一个 `FinalReference`，提供对象被 GC 前需要执行的 `finalize()` 方法。

## 3. 强引用

硬引用（hard reference）或强引用（strong reference）是默认的引用类型，大多时候，我们不需要考虑引用对象何时以及如何进行垃圾回收。**通过强引用可以访问的对象不能对其进行垃圾回收**。例如，创建一个 `ArrayList` 对象，将其赋值给 list 变量：

```java
List<String> list = new ArrayList<>;
```

垃圾收集器无法回收该 `ArrayList`，因为在 list 变量中保存了对它的 strong-ref。

如果将 list 变量置空：

```java
list = null;
```

此时，`ArrayList` 对象可以被垃圾回收，因为没有任何变量持有对它的引用。

strong-ref 特点：

- 可以直接访问目标对象
- strong-ref 指向的对象不会被会后，JVM 宁愿抛出 OOM 异常也不会回收
- strong-ref 可能导致内存泄漏

## 4. 软引用

被软引用（soft-ref）的对象，不会很快被 JVM 回收，JVM 会根据当前内存情况判断何时回收。当内存使用率超过阈值时，才回收 soft-ref 中的对象。所以当 JVM 面临内存耗尽时，会先清除 soft-ref 目标对象，只能通过 soft-ref 访问的对象都应该在抛出 `OutOfMemoryError` 异常之前被清除。

**示例：** soft-ref

```java
Object obj = new Object();
SoftReference softRef = new SoftReference<Object>(obj);
    
//删除强引用
obj = null;
    
//调用gc
System.gc();
System.out.println("gc之后的值：" + softRef.get()); // 对象依然存在
```

soft-ref 可以和 ref-queue 联合使用，如果 soft-ref 的目标对象被回收，那么 soft-ref 会被 JVM 加入关联的 ref-queue 中。

**示例：** ref-queue

```java
ReferenceQueue<Object> queue = new ReferenceQueue<>();
Object obj = new Object();
SoftReference softRef = new SoftReference<Object>(obj,queue);
    
//删除强引用
obj = null;

//调用gc
System.gc();
System.out.println("gc之后的值: " + softRef.get()); // 对象依然存在
    
//申请较大内存使内存空间使用率达到阈值，强迫gc
byte[] bytes = new byte[100 * 1024 * 1024];
    
//如果obj被回收，则软引用会进入引用队列
Reference<?> reference = queue.remove();
if (reference != null){
    System.out.println("对象已被回收: "+ reference.get()); // 对象为null
}
```

ref-queue 提供了外部访问 soft-ref 的功能，一方面可以判断 soft-ref 的目标对象是否被回收，另一方面可以继续对 soft-ref 本身进行清理。


使用 get 方法查询引用对象。因为该对象可能已经被清除，所有需要检查：

```java
List<String> list = listReference.get();
if (list == null) {
    // object was already cleared
}
```

### 4.1. 应用

软引用有利于缓解内存不足问题。例如，可以创建一个内存敏感的缓存，在内存不足时自动清除对象。我们不需要手动管理内存，垃圾回收器会为我们做这件事。

## 5. 弱引用

weak-ref 的目标对象生命周期很短，在 GC 时，只要发现 weak-ref，不管内存是否充足，都会将其对象进行回收。由于垃圾回收线程的优先级很低，所以不一定很快回收那些 weak-ref 的目标对象。

**示例：** weak-ref

```java
Object obj = new Object();
    
WeakReference weakRef = new WeakReference<Object>(obj);
    
//删除强引用
obj = null;
System.out.println("gc之后的值：" + weakRef.get()); // 对象依然存在
    
//调用gc
System.gc();
System.out.println("gc之后的值：" + weakRef.get()); // 对象为null
```

weak-ref 也可以和 ref-queue 联合使用。如果 weak-ref 中的目标对象被回收，那么 weak-ref 被 JVM 加入关联的 ref-queue 中。

```java
ReferenceQueue<Object> queue = new ReferenceQueue<>();

Object obj = new Object();
WeakReference weakRef = new WeakReference<Object>(obj,queue);

//删除强引用
obj = null;
System.out.println("gc之后的值: " + weakRef.get()); // 对象依然存在

//调用gc
System.gc();

//如果obj被回收，则弱引用会进入引用队列
Reference<?> reference = queue.remove();
if (reference != null){
    System.out.println("对象已被回收: "+ reference.get());  // 对象为null
}
```

```ad-tip
soft-ref 和 weak-ref 都适合保存那些可有可无的缓存数据，当内存不足时，缓存数据被回收（再通过备选方案查询）；当内存充足时，可以存在较长时间，起到加速的作用。
```

### 5.1. 应用

- WeakHashMap

当 key 只有 weak-ref 时，GC 发现后自动清理 key 和 value，作为简单的缓存表解决方案。

- ThreadLocal

ThreadLocal.ThreadLocalMap.Entry 继承了 soft-ref，key 为当前线程实例，和 WeakHashMap 基本相同。

## 6. 虚引用

虚引用（phantom-ref）不能决定对象的生命周期，任何时候 phantom-ref 的目标对象都可能被回收。因此，虚引用只要用来跟踪对象的回收，清理被回收对象的相关资源。PhantomReference.get() 永远返回 null，而且只提供了与 ref-queue 一起的构造函数，即虚引用必须和 ref-queue 一起使用：

```java
Map<Object, String> map = new HashMap<>();

ReferenceQueue<Object> queue = new ReferenceQueue<>();
Object obj = new Object();
PhantomReference phantomRef = new PhantomReference<Object>(obj,queue);

map.put(obj,"obj val");
new CheckRefQueue(queue,map).start();

//删除强引用
obj = null;

Thread.sleep(1000);
int i = 1;
while (true){
    System.out.println("第"+i+"次gc");
    System.gc();
    Thread.sleep(1000);
}
```

```java
public class CheckRefQueue extends Thread {

    private ReferenceQueue queue;
    private Map<Object, String> map;

    public CheckRefQueue(ReferenceQueue queue, Map<Object, String> map) {
        this.queue = queue;
        this.map = map;
    }

    @Override
    public void run() {
        // 等待，直到对象呗回收
        Reference reference = queue.remove();

        // 释放引用对象的引用
        map.remove(reference.get());
    }
}
```

## 7. 总结

![](Pasted%20image%2020230806225509.png)
