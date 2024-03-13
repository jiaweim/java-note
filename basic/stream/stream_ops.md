# Stream 操作

- [Stream 操作](#stream-操作)
  - [简介](#简介)
  - [无状态操作](#无状态操作)
    - [filter](#filter)
    - [map](#map)
    - [flatMap](#flatmap)
    - [mapMulti](#mapmulti)
    - [peek](#peek)
  - [有状态操作](#有状态操作)
    - [limit](#limit)
    - [skip](#skip)
    - [takeWhile](#takewhile)
    - [dropWhile](#dropwhile)
    - [合并 Stream](#合并-stream)
    - [distinct](#distinct)
    - [排序](#排序)

2023-11-23, 10:42
@author Jiawei Mao
****

## 简介

Stream 操作可以分为两类：终结操作和中间操作。

- 中间操作返回新的流，用于数据处理，一个流可以有多个中间操作
- 终结操作返回最终的结果，一个流只能有一个终结操作

中间操作又可以分为无状态和有状态：

- 无状态操作的元素处理不受之前元素的影响
- 有状态操作只有拿到所有元素之后才能继续执行

终结操作又可以分为短路和非短路操作：

- 短路指遇到符合条件的元素，就可以停止计算得到最终结果
- 非短路操作指必须处理完所有元素才能得到最终结果

## 无状态操作

### filter

对数据进行过滤，满足条件的元素生成新的 `Stream`。简单易行：

```java
ArrayList<String> list = new ArrayList<>();
list.add("One");
list.add("OneAndOnly");
list.add("Derek");
list.add("Change");
list.add("factory");
list.add("justBefore");
list.add("Italy");
list.add("Italy");
list.add("Thursday");
list.add("");

Stream<String> stream = list.stream().filter(element -> element.contains("d"));
stream.forEach(System.out::println);
```

`filter` 的参数为 `Predicate<T>` 类型。

### map

`map` 方法根据指定函数将源流转换为一个新的流。即对每个元素应用函数，使用函数的返回值生成新的流。

**示例：** 将所有字母转换为小写

```java
Stream<String> lowercaseWords = words.stream().map(String::toLowerCase);
```

这里通过**方法引用**调用 `toLowerCase` 方法。

- 对自定义函数，一般采用 lambda 表达式

```java
Stream<String> firstLetters = words.stream().map(s -> s.substring(0, 1));
```

使用每个单词的第一个字母生成新的流。

- 用来转换类型

```java
List<String> uris = new ArrayList<>();
uris.add("C:\\My.txt");

Stream<Path> stream = uris.stream().map(uri -> Paths.get(uri));
```

### flatMap

`map` 对每个元素依次应用函数，如果 mapping 函数对一个元素返回一个可选结果，或多个结果，怎么办？

例如，假设函数 `codePoints` 生成字符串的所有代码点。那么，`codePoints("Hello 🌐")` 包含 "H", "e", "l", "l", "o", " ", "🌐"。其中 🌐（U+1F310）包含 2 个字符。

如果使用 `map` 函数：

```java
Stream<Stream<String>> result = words.stream().map(w ->
        codePoints(w));
```

会生成类似 [... ["y", "o", "u","r"], ["b", "o", "a", "t"], ...] 的**嵌套**结果，即包含流的六 `Stream<Stream<String>>`。

要将嵌套流展开为单个流，应该使用 `flatMap`：

```java
Stream<String> flatResult = words.stream().flatMap(w ->
        codePoints(w));
```

### mapMulti

Java 16 引入了 `mapMulti` 方法：

```java
default <R> Stream<R> mapMulti(BiConsumer<? super T,? super Consumer<R>> mapper)
```

**中间操作**，返回一个新的流：

- 原流的元素转换为 0 或多个新的元素；
- mapper 负责转换元素，`BiConsumer` 接受 `Stream` 元素 `T`，将其转换为类型 `R`；
- 调用 `Consumer::accept` 方法收集元素。

例如：

```java
Stream.of("Twix", "Snickers", "Mars")
  .mapMulti((s, c) -> {
    c.accept(s.toUpperCase());
    c.accept(s.toLowerCase());
  })
  .forEach(System.out::println);
```

```
TWIX
twix
SNICKERS
snickers
MARS
mars
```

`mapper` 是一个实现 `Consumer` 接口的缓冲区。每次调用 `Consumer::accept` 都会收集一个元素。

!!! note
    此方法功能类似 `flatMap`，都是将流的元素进行一对多转换。不过在以下情况，`mapMulti` 优于 `flatMap`：

    - 当每个元素映射到少量元素。`flatMap` 需要创建嵌套 `Stream`，`mapMulti` 不需要，性能相对更好；
    - `mapMulti` 使用命令式方法生成新元素，语法相对更容易。

**示例：** 选择偶数，转换为 double

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
double percentage = .01;
List<Double> evenDoubles = integers.stream()
        .<Double>mapMulti((integer, consumer) -> {
            if (integer % 2 == 0) {
                consumer.accept((double) integer * (1 + percentage));
            }
        })
        .toList();
```

在 `BiConsumer<T, Consumer<R>>` `mapper` 实现中首先选择偶数，然后通过 `(double) integer * (1 + percentage)` 转换为 `double` 类型，最后调用 `consumer.accept` 收集元素。

这是一个 1 对 1 或 1 对 0 的转换，取决于元素是奇数还是偶数。

上例中，`if` 语句扮演了 `Stream::filter` 的角色，而将 integer 转换为 double 功能类似 `Stream::map`。实现相同功能：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
double percentage = .01;
List<Double> evenDoubles = integers.stream()
        .filter(integer -> integer % 2 == 0)
        .map(integer -> ((double) integer * (1 + percentage)))
        .toList();
```

`mapMulti` 实现相对更直接，不需要调用太多中间操作。另外，`mapMulti` 在元素转换方面更自由。

对基本类型，有对应的 `mapMultiToDouble`, `mapMultiToInt`, `mapMultiToLong` 函数。

### peek

`peek` 不对元素做任何处理，其参数 `Consumer` 可辅助检查元素。一般用于 debugging：

```java
List<String> list = Stream.of("one", "two", "three", "four")
        .filter(e -> e.length() > 3)
        .peek(e -> System.out.println("Filtered value: " + e))
        .map(String::toUpperCase)
        .peek(e -> System.out.println("Mapped value: " + e))
        .toList();
System.out.println(list);
```

```
Filtered value: three
Mapped value: THREE
Filtered value: four
Mapped value: FOUR
[THREE, FOUR]
```

`peek` 也是 lazy 操作，因此只在终端操作需要的元素上执行。

## 有状态操作

### limit

`stream.limit(n)` 保留前 n 个元素

该方法对分割无限流非常有用。例如，生成 100 个随机数：

```java
Stream<Double> randoms =
        Stream.generate(Math::random).limit(100);
```

### skip

- `stream.skip(n)` 与 `limit` 相反，抛弃前 n 个元素

```java
List<Integer> list = List.of(1, 2, 3, 4, 5);
list.stream().skip(3).forEach(System.out::println);
```

```
4
5
```

### takeWhile

`stream.takeWhile(predicate)` 接受元素直到 `predicate` 不再为 `true`：

```java
Stream.of('A', 'B', 'c', 'D')
        .takeWhile(Character::isUpperCase)
        .forEach(System.out::println);
```

```
A
B
```

到 `c` 不再为大写字母，终止接收元素。

!!! note
    对无序流，并且某些元素（不是全部）满足 predicate，则 `takeWhile` 的行为不确定，可能返回各种元素子集，包括空集。

### dropWhile

`dropWhile` 当条件为 `true` 时丢弃元素，接收第一个条件为 `false` 元素后的所有元素

`dropWhile` 和 `takeWhile` 相反，不再赘述。

### 合并 Stream

`concat` 合并两个 streams

```java
Stream.concat(Stream.of(1, 2, 3), Stream.of(5, 6))
        .forEach(System.out::println);
```

```
1
2
3
5
6
```

### distinct

`distinct` 去掉重复元素，保留元素顺序。例如：

```java
Stream.of("merrily", "merrily", "merrily", "gently")
        .distinct()
        .forEach(System.out::println);
```

```
merrily
gently
```

### 排序

使用 `sorted` 进行排序。

- 无参 `sorted()` 对已实现 `Comparable` 的元素按自然顺序排序

```java
Stream.of(1, 2, 4, 3, 5)
        .sorted()
        .forEach(System.out::println);
```

```
1
2
3
4
5
```

- 带 `Comparator` 参数的 `sorted()` 则可以自定义排序规则

例如，字符串按长度排序，长的优先：

```java
Stream.of("a", "aa", "aaa", "ccc", "bbb")
        .sorted(Comparator.comparing(String::length).reversed())
        .forEach(System.out::println);
```

```
aaa
ccc
bbb
aa
a
```

和其它 stream 操作一样，`sorted` 生成一个新的 stream。

另外，从上面的输出可以看出，对相同长度的字符串，`sorted` 保留原顺序不变，即这是稳定排序。
