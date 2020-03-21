# TOC
- [TOC](#toc)
- [简介](#%e7%ae%80%e4%bb%8b)
- [创建 Stream](#%e5%88%9b%e5%bb%ba-stream)
  - [Empty Stream](#empty-stream)
  - [集合](#%e9%9b%86%e5%90%88)
  - [数组](#%e6%95%b0%e7%bb%84)
  - [Stream.builder](#streambuilder)
  - [generate](#generate)
  - [iterate](#iterate)
  - [基本类型流](#%e5%9f%ba%e6%9c%ac%e7%b1%bb%e5%9e%8b%e6%b5%81)
  - [String 流](#string-%e6%b5%81)
- [引用流](#%e5%bc%95%e7%94%a8%e6%b5%81)
- [串联操作](#%e4%b8%b2%e8%81%94%e6%93%8d%e4%bd%9c)
- [多线程 Stream](#%e5%a4%9a%e7%ba%bf%e7%a8%8b-stream)
- [Stream 操作](#stream-%e6%93%8d%e4%bd%9c)
  - [Lazy](#lazy)
  - [迭代](#%e8%bf%ad%e4%bb%a3)
  - [过滤](#%e8%bf%87%e6%bb%a4)
  - [Mapping](#mapping)
  - [flatMapping](#flatmapping)
  - [匹配](#%e5%8c%b9%e9%85%8d)
  - [Collect](#collect)
- [Reduce](#reduce)

# 简介
stream 和集合很像，允许你转换和检索数据，但是也有所不同：
- stream 不保存元素，元素保存在底层集合，或在需要时再生成；
- stream 不修改源数据。例如，`filter` 方法不从流中移除元素，而是生成一个新的 stream；
- stream 的操作都尽可能采用 *lazy* 操作。即在需要结果时才真正开始执行。


# 创建 Stream

## Empty Stream
`empty` 方法创建不包含任何元素的流：
```java
Stream<String> silence = Stream.empty();
// Generic type <String> is inferred; same as Stream.<String>empty()
```
其功能类似于 `Optional.empty()`，可用于避免返回 null：
```java
public Stream<String> streamOf(List<String> list) {
    return list == null || list.isEmpty() ? Stream.empty() : list.stream();
}
```

## 集合
对所有的集合类型，在 `Collection` 接口中的 `stream` 方法可以将其转换为 `Stream`：
```java
Collection<String> collection = Arrays.asList("a", "b", "c");
Stream<String> streamOfCollection = collection.stream();
```

## 数组
对数组使用 `Stream.of` 方法：
```java
Stream<String> song = Stream.of("gently", "down", "the", "stream");
```

对已有的数组：
```java
Stream<String> words = Stream.of(contents.split("\\PL+"));
// split returns a String[] array
String[] arr = new String[]{"a", "b", "c"};
Stream<String> stream1 = Stream.of(arr);
Stream<String> stream2 = Arrays.stream(arr);
```

或者使用部分数组生成流：
```java
Arrays.stream(array, from, to)
```

## Stream.builder
使用 builder 需要指定好类型，否则生成的流是 `Stream<Object>` 类型：
```java
Stream<String> streamBuilder =
  Stream.<String>builder().add("a").add("b").add("c").build();
```

## generate
`Stream` 接口有两个生成无限流的静态方法，`generate` 方法根据提供的函数生成流。
函数是 `Supplier<T>` 类型。

如生成常量的无限流：
```java
Stream<String> echos = Stream.generate(() -> "Echo");
```

生成随机数的无限流：
```java
Stream<Double> randoms = Stream.generate(Math::random);
```

或者限制流的大小：
```java
Stream<String> streamGenerated =
  Stream.generate(() -> "element").limit(10);
```

## iterate
`iterate` 根据初始值和函数生成序列值。例如：
```java
Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.ONE));
```
方法的第一个参数是初始值 `BigInteger.ZERO`，第二个参数是 `UnaryOperator<T>` 类型的函数。

要生成有限流，可以添加一个 `Predicate` 指定迭代结束的条件：
```java
BigInteger limit = new BigInteger("10000000");
Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO, n -> n.compareTo(limit) < 0, n -> n.add(BigInteger.ONE));
```
或者：
```java
Stream<Integer> streamIterated = Stream.iterate(40, n -> n + 2).limit(20);
```

## 基本类型流
Java 8 提供了 3 种基本类型的流：int, long 和 double，分别为 `IntStream`, `LongStream`, `DoubleStream`。

使用这些类型可以避免不必要的开箱操作：
```java
IntStream intStream = IntStream.range(1, 3);
LongStream longStream = LongStream.rangeClosed(1, 3);
```

## String 流
`String` 的 `chars()` 方法生成字符流，由于没有 `CharStream`，以 `IntStream` 表示字符流：
```java
IntStream streamOfChars = "abc".chars();
```

# 引用流
如果只使用中间操作，则可以对引用的流进行操作，如果包含终结操作，会导致流不再可用。

例如：
```java
Stream<String> stream = 
  Stream.of("a", "b", "c").filter(element -> element.contains("b"));
Optional<String> anyElement = stream.findAny();
```
然后再次使用该流：
```java
Optional<String> firstElement = stream.findFirst();
```
会抛出 `IllegalStateException`。即流不可重复使用。

# 串联操作
要对原数据进行一系列的操作，需要三部分：
- 源数据
- 中间操作
- 终结操作

中间操作返回 stream 对象，可以继续添加其它操作。使用 `skip` 可以忽略部分源数据：
```java
Stream<String> onceModifiedStream =
  Stream.of("abcd", "bbcd", "cbcd").skip(1);
```

一系列的中间操作后，使用终结操作获得最终想要的结果：
```java
List<String> list = Arrays.asList("abc1", "abc2", "abc3");
long size = list.stream().skip(1)
  .map(element -> element.substring(0, 3)).sorted().count();
```
# 多线程 Stream
```java
List<Integer> list = Arrays.asList(1, 2, 3);
Stream<Integer> stream = list.parallelStream();
```

# Stream 操作
在流上面可以进行很多操作，这些操作可以分为两类：
- 中间操作（返回 `Stream<T>`）；
- 终结操作（返回特定的结果类型）。

## Lazy
中间操作懒惰类型操作。只有在必须时，中间操作才会执行，否则会等到最终的终结操作才开始执行。

## 迭代
Stream 可以替代 `for`, `for-each` 和 `while` 循环。将操作集中在逻辑而非循环本身。例如：
```java
for (String string : list) {
    if (string.contains("a")) {
        return true;
    }
}
```
转换成流只有一行：
```java
boolean isExist = list.stream().anyMatch(element -> element.contains("a"));
```

## 过滤
对数据进行过滤，简单易行：

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

## Mapping
流转换根据指定的条件从原来的流生成一个新的流。

`map` 方法根据指定函数将源流转换为一个新的流：
```java
Stream<String> lowercaseWords = words.stream().map(String::toLowerCase);
```

对应自定义函数，一般采用 lambda 表达式：
```java
Stream<String> firstLetters = words.stream().map(s -> s.substring(0, 1));
```

或者转换类型：
```java
List<String> uris = new ArrayList<>();
uris.add("C:\\My.txt");
Stream<Path> stream = uris.stream().map(uri -> Paths.get(uri));
```

## flatMapping
如果流的元素本身包含元素序列，创建内部元素的流，可以使用 `flatMap` 方法：
```java
List<Detail> details = new ArrayList<>();
details.add(new Detail());
Stream<String> stream = details.stream().flatMap(detail -> detail.getParts().stream());
```

## 匹配
根据特定的 predicate 验证元素：
- `anyMatch`
- `allMatch`
- `nonMatch`

```java
boolean isValid = list.stream().anyMatch(element -> element.contains("h")); // true
boolean isValidOne = list.stream().allMatch(element -> element.contains("h")); // false
boolean isValidTwo = list.stream().noneMatch(element -> element.contains("h")); // false
```

## Collect
`collect()` 顾名思义，将流的元素收集起来，转换为特定的类型：
```java
List<String> resultList 
  = list.stream().map(element -> element.toUpperCase()).collect(Collectors.toList());
```

# Reduce
`reduce` 方法有三个版本，可能的参数有三个：
- `identity`, 收集器的初始值或默认值；
- `accumulator`, 指定收集策略的函数；
- `combiner`，将 `accumulator` 的结果进行合并的函数。

例如：
```java
OptionalInt reduced =
  IntStream.range(1, 4).reduce((a, b) -> a + b);
```


根据初始值和指定的函数，根据流生成一个值：
```java
List<Integer> integers = Arrays.asList(1, 1, 1);
Integer reduced = integers.stream().reduce(23, (a, b) -> a + b);
```