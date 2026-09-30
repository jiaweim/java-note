# Eclipse Collections 示例

下面介绍通过一些示例展示 Eclipse Collections 中常用方法。

## RichIterable

### collect

```java
<V> RichIterable<V> collect(Function<? super T, ? extends V> function);
```

对源集合的每个元素应用指定函数，将结果存入一个新集合。此方法也被称为 transform（转换）或 map（映射）。

示例：

```java
MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3, 4);

// 1. 每个元素乘以 2，返回 MutableList<Integer>
MutableList<Integer> doubled = numbers.collect(n -> n * 2);
System.out.println(doubled); // [2, 4, 6, 8]

// 2. 转换为字符串
MutableList<String> strings = numbers.collect(n -> "N" + n);
System.out.println(strings); // [N1, N2, N3, N4]

// 3. 使用 collectInt 转换为原始 int 列表，避免装箱
MutableIntList ints = numbers.collectInt(n -> n * 10);
System.out.println(ints); // [10, 20, 30, 40]

// 4. 惰性求值
MutableList<Integer> squared = numbers.asLazy()
        .collect(n -> n * n)
        .toList();
System.out.println(squared); // [1, 4, 9, 16]
```

### select



### reject

### count

### anySatisfy

### allSatisfy

### noneSatisfy

### groupBy

### countBy

### makeString

### asLazy

### containsBy

### detectWith

### detectWithIfNone

### injectInto

### partition

### chunk

### sumByInt

### collectInt

### flatCollectChar

## ListIterable

### asParallel

```java
ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize);
```

将该 `ListIterable` 转换为执行并行操作的 `ParallelListIterable`。

示例：

```java
```



## MutableList

### toImmutable

### distinct

## MutableListFactory

### with

```java
<T> MutableList<T> with(T... items);
```

使用传入的 `items` 参数作为底层数组来创建一个新的 `MutableList`。

注意：该方法直接使用底层传入的数组，如果在其它地方还保留对该原始数据的引用，会不安全。

示例：

```java
MutableList<String> fruit = Lists.mutable.with("🍎", "🍑", "🍌", "🍒", "🍊");
```

## MutableSetFactory

### of

```java
default <T> MutableSet<T> of(T... items);
```

