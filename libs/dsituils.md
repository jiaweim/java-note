# DSI Utilities

## 简介

DSI utilities 包含米兰大学信息科学系近十年来积累的各种类。这些类之前分布在不同项目中（主要在 MG4J 中），现在全部收集到了 DSI utilities。主要包含：

- 伪随机数生成器
- `BitVector` 及其实现：一组高性能且灵活的 bit-vector 类
- `it.unimi.dsi.compression` 包含多种编码类型
- `ProgressLogger` 是一个灵活的 logger，具有统计功能，可以标记需要长时间任务的进度
- `ObjectParser`，可以命令行轻松指定复杂对象的类
- `MutableString`, Java `String` 的 mutable 实现
- I/O 包，包含 `java.io` 中已有类的快速版本，提供许多可以轻松读取文本数据（`FileLinesMutableStringIterable`）, bit-stream 等类
- `it.unimi.dsi.util` 包含 tries, immutable prefix-map, bloom-filter 等
- `it.unimi.dsi.stat` 包含计算基本统计量的类，以及 Jackknife 方法的任意精度实现
- `MappedFrontCodedStringBigList` 提供紧凑字符串内存映射

## BitVector

位向量（bit vector），又称为 bit-sequence, bit-string, binary-word 等。

`BitVector` 接口定义了对有限 bit 序列的多种操作。`BitVector` 的高效实现（如 `LongArrayBitVector`）对向量中每个 bit 大约占用一个 bit 内存。

bit-vector 的部分操作与 boolean 类似（如向量之间的逻辑运算），有些与文本类似（如串联），部分与集合类似（如查看哪些 bits 被设置为 1）。为了兼容这些功能，`BitVector` 接口扩展了 fastutil 中的 `BooleanBigList` 接口，并提供 `asLongSet()` 方法以实现类似 `BitSet` 的视图，提供 `asLongBigList(int)` 以方位指定宽度的 bit-block。

大多数对 bit-vector 的操作可以看作对以上两种视图的操作：例如，设置为 1 的 bit 的数量就是 `asLongSet()` 返回的集合元素的数量，当然也提供了更直接的 `count()` 方法。可以用标准的 `Collection.addAll(java.util.Collection)` 方法串联向量，而 `sublist` 视图可用于执行对子向量的逻辑操作。

唯一需要注意的是，标准接口的命名与常规用法略有冲突，例如 `Collection.clear()` 不会将所有 bits 设置为 0（可以用 `fill(0)` 实现），而是将向量的长度设置为 0。此外，`add(long, int)` 不会在指定位置添加值，而是在指定位置插入一个指定值的 bit。



## 参考

- https://github.com/vigna/dsiutils