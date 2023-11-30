# 终结操作

- [终结操作](#终结操作)
  - [简介](#简介)
  - [集约操作](#集约操作)
    - [count](#count)
    - [min 和 max](#min-和-max)
    - [findFirst](#findfirst)
    - [findAny](#findany)
    - [anyMatch](#anymatch)
  - [收集结果](#收集结果)
    - [iterator](#iterator)
    - [forEach](#foreach)
    - [toList](#tolist)
    - [toArray](#toarray)
    - [collect](#collect)
    - [collect2](#collect2)
    - [summarizing](#summarizing)

2023-11-23, 13:10
@author Jiawei Mao
****

## 简介

终结操作将 Stream 转换为非 Stream。调用终结操作后，流不再可用。

## 集约操作

集约操作将流转换为单个值。

### count

`count` 返回 stream 的元素个数：

```java
long count = Stream.of("one", "two", "three", "four").count();
System.out.println(count);
```

```
4
```

### min 和 max

- `max` 返回最大值
- `min` 返回最小值

```java
Optional<Integer> min = Stream.of(1, 2, 3, 10, 3)
        .min(Comparator.naturalOrder());
System.out.println("min=" + min.orElse(-1));

Optional<Integer> max = Stream.of(1, 2, 3, 10, 3)
        .max(Comparator.naturalOrder());
System.out.println("max=" + max.orElse(100));
```

```
min=1
max=10
```

这两个方法都返回 `Optional<T>` 类型，因为流为空是没有最小或最大值。

### findFirst

`findFirst` 返回流的第一个元素。

**示例：** 第一个以 Q 开头的单词

```java
Optional<String> first = Stream.of("a", "b", "c", "Qb", "Qc", "d")
        .filter(s -> s.startsWith("Q"))
        .findFirst();
System.out.println("first Q=" + first.orElse(""));
```

```
first Q=Qb
```

这里用 `filter` 过滤只保留以 "Q" 开头的元素，然后用 `findFirst` 返回第一个。

### findAny

`findAny` 返回任一满足要求的元素，适合与并行 stream 联用。

```java
Optional<String> first = Stream.of("a", "b", "c", "Qb", "Qc", "d")
        .parallel()
        .filter(s -> s.startsWith("Q"))
        .findAny();
System.out.println("first Q=" + first.orElse(""));
```

理论上可能返回 "Qb" 或 "Qc"。

### anyMatch

`anyMatch` 判断是否有满足条件的元素

```java
boolean anyMatch = Stream.of("a", "b", "c", "Qb", "Qc", "d")
        .parallel()
        .anyMatch(s -> s.startsWith("Q"));
```

`anyMatch` 以 `Predicate` 为参数，返回 `boolean` 值。

Match 总结：

- `anyMatch` 是否有元素满足条件
- `allMatch` 是否所有元素满足条件
- `noneMatch` 是否所有元素都不满足条件

## 收集结果

通过流处理完后，需要收集或查看处理结果。

### iterator

`iterator` 方法生成一个老式的迭代器，可用于访问元素。

```java
Iterator<String> it = Stream.of("a", "B", "c")
        .map(String::toUpperCase)
        .iterator();
while (it.hasNext()) {
    System.out.println(it.next());
}
```

```
A
B
C
```

### forEach

`forEach` 为每个元素依次应用 `Consumer` 函数：

```java
Stream.of("a", "B", "c")
        .forEach(System.out::println);
```

```
a
B
c
```

对 parallelStream，`forEach` 无序遍历元素。`forEachOrdered` 可以按顺序访问元素，但意味着放弃并行的好处。

### toList

`toList()` 将收集 Stream 元素保存为 `List`：

```java
List<String> list = Stream.of("a", "b", "c").toList();
```

### toArray

`toArray` 返回元素数组，由于运行时无法创建泛型数组，所以 `stream.toArray()` 返回 `Object[]`，如果想返回泛型数组，可以传入数组的构造函数：

```java
String[] result = stream.toArray(String[]::new); 
   // stream.toArray() 返回 Object[]
```

### collect

`collect()` 顾名思义，将元素收集起来，转换为特定的类型。其参数 `Collector` 提供了创建常见集合类型的工厂方法。

在 Java 16 引入 `toList` 之前，只能通过 `Collectors.toList()` 生成 List:

```java
List<String> list = Stream.of("a", "b", "c", "a").collect(Collectors.toList());
System.out.println(list);
```

```
[a, b, c, a]
```

- 使用 `Collectors.toSet()` 将元素保存到 Set

```java
Set<String> collect = Stream.of("a", "b", "c", "a").collect(Collectors.toSet());
System.out.println(collect);
```

```
[a, b, c]
```

- 控制生成集合的类型

```java
TreeSet<String> result = stream.collect(Collectors.toCollection(TreeSet::new));
```

- 将所有字符串串联

```java
String result = stream.collect(Collectors.joining());
```

- 串联字符串：指定分隔符

```java
String result = stream.collect(Collectors.joining(", "));
```

- 对非 String，可以先用 `toString` 转换为字符串，再串联

```java
String result = stream.map(Object::toString)
        .collect(Collectors.joining(", "));
```

### collect2

`collect` 有一个重载方法：

```java
<R> R collect(Supplier<R> supplier,
                BiConsumer<R, ? super T> accumulator,
                BiConsumer<R, R> combiner)
```

该操作返回 mutable 类型容器，如 `ArrayList`，对新元素采用更新而非替换操作。等价于：

```java
R result = supplier.get(); 
for (T element : this stream)      
    accumulator.accept(result, element);  
return result;
```

和 `reduce(Object, BinaryOperator)` 一样，`collect` 不需要额外同步就能并行化。

参数：

- `supplier`:创建新的 mutable 容器。对并行，该函数可能被调用多次，每次都应该返回一个新的值。
- `accumulator`：

- 将字符串保存到 `ArrayList`

```java
List<String> asList = stringStream.collect(ArrayList::new,
        ArrayList::add,
        ArrayList::addAll);
```

### summarizing

`summarizing(Int|Long|Double)` 方法将 Stream 元素映射为数值，返回 `(Int|Long|Double)SummaryStatistics`，通过该对象可以获取相关统计信息，如加和、个数、平均、最小和最大值等。

```java
IntSummaryStatistics summary = Stream.of("a", "b", "bb", "c", "cc", "ccc")
        .collect(Collectors.summarizingInt(String::length));
System.out.println(summary.getCount());
System.out.println(summary.getAverage());
System.out.println(summary.getMin());
System.out.println(summary.getMax());
System.out.println(summary.getSum());
```

```
6
1.6666666666666667
1
3
10
```