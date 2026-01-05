# fastutil

## 简介

fastutil 提供特定类型的 map, set, list 和 priority-queue 来扩展 Java 集合框架，它们占用内存更少，访问和插入速度快，并提供大型数组（64-bit）、set 和 list，以及用于二进制和文本文件的快速 I/O。

fastutil 由三部分组成：

- 扩展 Java 集合框架的类型特定类；
- 大型集合类；
- 二进制和文本文件的快速 I/O

在 8.5 版本新增：

- 类型特定的 spliterators;
- primitive stream 方法；
- 覆盖更多 default 方法以提供性能，或避免 boxing/unboxing

> [!WARNING]
>
> 强烈建议在开发环境中启用 deprecation 警告。因为 fastutil 系统地启用了所有具有特定类型替代方案的 JDK 方法。

### 类型特定类

fastutil 为 `HashSet`, `HashMap`, `LinkedHashSet`, `LinkedHashMap`, `TreeSet`, `TreeMap`, `IdentityHashMap`, `ArrayList`, `Stack` 针对基础类型提供了专门实现。此外，还有多种类型的 priority-queue 和大量的 static 对象和方法，如不可变的空容器，comparators，iterators 等。

### 支持大型集合

fastutil 支持大小超过 $2^{31}$ 的大型集合：

- `BigArrays` 通过数组的数组实现，提供的方法对大数组的操作就像具有 64 为索引的一维数组一样；
- `BigList` 提供 64-bit list 访问；
- `IntOpenHashBigSet` 支持大小仅受内存限制的集合

`BigArrays` 提供 `Arrays` 中常用的方法，具体可查看 `BigArrays` 和 `IntBigArrays` 的文档。

对 interger 和 long 的原子类型 big 数组提供有限支持。

### 快速 I/O

fastutil 替代了一些 `java.io` 的 标准类，这些类存在许多问题，如参考 `FastBufferedInputStream`。

`BinIO` 和 `TextIO` 静态容器包含上十种方法，可以快速将大型数组读写到磁盘。

### core

标准 fastutil jar 太大，fastutil-core 只包含 integer, long 和 double，要小很多。

## 大型集合

### BigArrays



## 参考

- https://fastutil.di.unimi.it/docs/