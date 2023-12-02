# Spliterator

## 简介

JDK 8 添加了 `Spliterator` 接口。其功能类似 `Iterator`，但是比 `Iterator` 和 `ListIterator` 功能更强。更重要的是，`Spliterator` 支持序列的并行迭代。在并并行环境也可以使用 `Spliterator`，它将 `hasNext` 和 `next` 合并为一个操作。

`Spliterator` 接口定义：

```java
interface Spliterator<T>
```

其中 `T` 是待迭代的元素类型。

`Spliterator` 遍历并划分数据源。

`Spliterator` 支持两种遍历模式：

- 一次遍历一个元素 `tryAdvance()`
- 批量遍历元素 `forEachRemaining()`

`Spliterator` 可以使用 `trySplit()` 将部分元素分出来生成另一个 `Spliterator`，用于可能的并行操作。不能拆分、拆分不平衡或拆分低效的 `Spliterator` 则不能从并行中受益。遍历并拆分所有元素，每个 `Spliterator` 仅用于单个批量计算。

`Spliterator` 的 `characteristics()` 报告与其结构、数据源和元素相关的一组特征：`ORDERED`, `DISTINCT`, `SORTED`, `SIZED`, `NONNULL`, `IMMUTABLE`, `CONCURRENT`, `SUBSIZED`。`Spliterator` 用户可以使用它们来控制或简化计算。例如：

- 用于 `Collection` 的 `Spliterator` 将报告 `SIZED`
- 用于 `Set` 的 `Spliterator` 报告 `DISTINCT`
- 用于 `SortedSet` 的 `Spliterator` 报告 `SORTED`

特征值以简单的 union bit-set 返回。有些特征还约束了方法的行为，例如对 `ORDERED`，遍历方法必须符合它们的记录顺序。

不报告 IMMUTABLE 或 CONCURRENT 的 `Spliterator` 

## characteristics

### ORDERED

```java
static final int ORDERED
```




## tryAdvance

```java
boolean tryAdvance(Consumer<? super T> action)
```

处理下一个元素，对其应用指定 `action`，并返回 `true`；没有元素则返回 `false`。如果 `Spliterator` 为 `ORDERED`，则按顺序对元素执行操作。

## forEachRemaining

```java
default void forEachRemaining(Consumer<? super T> action)
```

在当前线程对余下元素执行指定操作，直到处理完所有元素或抛出异常。

如果该 `Spliterator` 为 `ORDERED`，则按元素迭代顺序执行操作。

默认实现重复调用 `tryAdvance(Consumer<? super T>` 直到返回 `false`。应该尽可能重写该方法。

## trySplit

```java
Spliterator<T> trySplit()
```

如果该 Spliterator 可以分割，则返回一个新的 Spliterator，新 Spliterator 包含该方法返回时当前 Spliterator 没有覆盖的元素。

如果 Spliterator 为 `ORDERED`，则返回的 Spliterator 必须严格包含元素前缀。

除非这个 Spliterator 包含无限元素，否则反复调用 Spliterator 最终必须返回 null。对 non-null 返回值：

- 拆分前 `estimateSize()` 返回值，必须大于或等于拆分后当前 Spliterator 的 `estimateSize()` 以及返回 Spliterator 的 `estimateSize()`
- 如果当前 Spliterator 为 SUBSIZED，则其 `estimateSize()` 比如是拆分后 `estimateSize()` 和返回 Spliterator 的 `estimateSize()` 的加和

该方法可能返回 null，如没有剩余元素、无法拆分或基于效率考虑等。

!!! note
    理想的 `trySplit` 方法有效地（无序遍历）将元素精确地分成两半，从而允许平衡的并行计算。稍微偏离平衡依然高效，但偏离太远会导致并行性能差。


## estimateSize

```java
long estimateSize()
```

返回该 `Spliterator` 要处理元素的个数。对无限的、未知的、或计算太麻烦的情况返回 `Long.MAX_VALUE`。

如果 `Spliterator` 为 `SIZED`，且尚未遍历或拆分；或者是 `SUBSIZED`，且尚未遍历，则该方法必须返回准确的元素个数。

## 参考

- https://docs.oracle.com/javase/8/docs/api/java/util/Spliterator.html