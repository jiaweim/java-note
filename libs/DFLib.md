# DBLib

## 简介

DFLib (DataFrame Library) 是一个轻量级的 `DataFrame` 的纯 Java 实现。在数据科学和大数据领域，`DataFrame` 是非常常见的结构，提供搜索、过滤、连接、聚合、统计等功能。

在 Python (`pandas`) 和 R 等语言中都有 `DataFrame` 实现，DFLib 项目的目标是提供 `DataFrame` 的纯 Java 实现。它是一个简单的库，且核心库没有依赖项。

## 添加包

```xml
<dependency>
    <groupId>org.dflib</groupId>
    <artifactId>dflib</artifactId>
    
</dependency>
```

创建 DataFrame，操作数据，打印结果：

```java
DataFrame df1 = DataFrame.foldByRow("a", "b", "c")
        .ofStream(IntStream.range(1, 10000));
DataFrame df2 = df1.rows(r -> r.getInt(0) % 2 == 0).select();
System.out.println(Printers.tabular.toString(df2));
```

```
   a    b    c
---- ---- ----
   4    5    6
  10   11   12
  16   17   18
...
9982 9983 9984
9988 9989 9990
9994 9995 9996
1666 rows x 3 columns
```

## 数据结构

DFLib 有两个基本类 `Series` 和 `DataFrame`。

`Series` 包含一维数据，`DataFrame` 包含二维数据。`DataFrame` 的 column 存储为 `Series` 对象。另外还有一个 `Index` 对象，用于存储 `DataFrame` 的 column 名称。

`DataFrame` 和 `Series` (包括 `Index`) 都是 immutable 对象，因此对它们的所有操作都返回一个新对象。在实现时，DFLib 在实例之间尽可能共享数据，因此复制对象不会导致显著的性能下降，同时使得 DFLib 线程安全。从而支持并发操作。此外，immutable 保证每个步骤都拥有数据的完整快照，从而简化了数据 pipeline 的调试。

### Series

`Series` 可以看作对数组的包装，为泛型类。并为基础类型提供优化类 `IntSeries`, `LongSeries`, `DoubleSeries`, `BooleanSeries`。

`Series` 是 `DataFrame` 构建模块，并定义了许多数据操作和转换功能。

#### 从数组创建 Series

使用 static 方法 `Series.of` 创建：

```java
Series<String> s = Series.of("a", "bcd", "ef", "g");
System.out.println(Printers.tabular.toString(s));
```

```
a  
bcd
ef 
g  
4 elements
```

primitive 类型有专门的工厂方法：

```java
IntSeries is = Series.ofInt(0, 1, -300, Integer.MAX_VALUE);
```

#### 使用 byElement 创建





## 参考

- https://github.com/dflib/dflib
