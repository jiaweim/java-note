# Stream
- [Stream](#stream)
  - [Stream 操作](#stream-操作)
    - [Lazy](#lazy)
    - [迭代](#迭代)
    - [flatMap](#flatmap)
    - [mapMulti](#mapmulti)
    - [distinct](#distinct)
    - [排序](#排序)
    - [拆分与合并 Stream](#拆分与合并-stream)
    - [peek](#peek)
  - [集约操作](#集约操作)
  - [Collect](#collect)
  - [Reduce](#reduce)

## Stream 操作

在流上面可以进行很多操作，这些操作可以分为两类：
- 中间操作（返回 `Stream<T>`）；
- 终结操作（返回特定的结果类型）。

### Lazy
中间操作懒惰类型操作。只有在必须时，中间操作才会执行，否则会等到最终的终结操作才开始执行。

### 迭代
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


### flatMap

如果 mapping 函数生成一个可选结果，或多个结果。例如，假设函数 `codePoints` 生成字符串的所有代码点。那么，`codePoints("Hello 🌐")` 包含 "H", "e", "l", "l", "o", " ", "🌐"。其中 🌐（U+1F310）包含 2 个字符。

如果使用 `map` 函数：

```java
Stream<Stream<String>> result = words.stream().map(w ->
        codePoints(w));
```

会生成类似 [... ["y", "o", "u","r"], ["b", "o", "a", "t"], ...] 的**嵌套**结果，要获得 [... "y", "o", "u", "r", "b", "o", "a", "t", ...] 形式，应该使用 `flatMap`：

```java
Stream<String> flatResult = words.stream().flatMap(w ->
        codePoints(w));
```

### mapMulti

```java
<R> Stream<R> mapMulti(BiConsumer<T, Consumer<R>> mapper)
```

`BiConsumer` 接受 Stream 元素 `T`，将其转换为类型 `R`，然后调用 `mapper` 的 `Consumer::accept` 方法。

在 Java 的 `mapMulti` 实现中，`mapper` 是一个实现 `Consumer` 接口的缓冲区。每次调用 Consumer::accept，mapper 收集元素并传递到 stream pipeline。例如：

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

在 `BiConsumer<T, Consumer<R>>` `mapper` 实现中，首先选择偶数，然后通过 `(double) integer * (1 + percentage)` 转换为 `double` 类型，最后调用 `consumer.accept`。

这里，`consumer` 只是一个缓冲区，它将返回值传递给 stream pipeline。

这是一个 1对1 或 0 对 0 的转换，取决于元素是奇数还是偶数。

上例中，`if` 语句扮演了 `Stream::filter` 的角色，而将 integer 转换为 double 功能类似 `Stream::map`。使用它们实现相同功能：

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

!!! info
    当 `Stream` 的元素 map 出少量元素，使用 `flatMap` 需要为每个元素创建 Stream，开销相对较大，此时推荐使用 `mapMulti`。

### distinct

`distinct` 抛出重复元素，保留元素顺序。例如：

```java
Stream<String> uniqueWords = Stream.of("merrily", "merrily", "merrily", "gently")
        .distinct();
```

### 排序

使用 `sorted` 进行排序。例如，更长的字符串优先：

```java
Stream<String> longestFirst = words.stream()
        .sorted(Comparator.comparing(String::length).reversed());
```

和其它 stream 操作一样，`sorted` 生成一个新的 stream。

当然，不使用流也能对集合进行排序，`sorted` 适用于排序是一个 stream pipeline 一部分时。

### 拆分与合并 Stream

- `stream.limit(n)` 返回包含前 n 个元素新 stream

该方法对分割无限流非常有用。例如，生成 100 个随机数：

```java
Stream<Double> randoms =
        Stream.generate(Math::random).limit(100);
```

- `stream.skip(n)` 与 `limit` 相反，抛弃前 n 个元素
- `stream.takeWhile(predicate)` 接受元素直到 `predicate` 不再为 true
- `dropWhile` 当条件为 `true` 时丢弃元素，接收第一个条件为 `false` 元素后的所有元素
- `concat` 合并两个 streams

### peek

`peek` 生成 stream 的元素与原 stream 相同，但对每个元素都调用一个函数，适合用于 debug：

```java
Object[] powers = Stream.iterate(1.0, p -> p * 2)
        .peek(e -> System.out.println("Fetching " + e))
        .limit(20).toArray();
```

## 集约操作

集约操作（reduction）为终端操作，这类操作将 Stream 转换为非 stream，终止 stream pipeline。例如：

- `count` 返回 stream 的元素个数
- `max` 返回最大值
- `min` 返回最小值

**示例：** 最大字符串

```java
Optional<String> largest =
        words.max(String::compareToIgnoreCase);
```

注意，`max` 和 `min` 返回的都是 `Optional<T>` 类型，以处理 `null` 情况。

- `findFirst` 返回非空集合的第一个元素

**示例：** 第一个以 Q 开头的单词

```java
Optional<String> startsWithQ
        = words.filter(s -> s.startsWith("Q")).findFirst();
```

- `findAny` 返回任意一个满足要求的元素，适合与并行 stream 联用

```java
Optional<String> startsWithQ
        = words.parallel().filter(s -> s.startsWith("Q")).findAny();
```

- `anyMatch` 判断是否有满足条件的元素

```java
boolean aWordStartsWithQ
        = words.parallel().anyMatch(s -> s.startsWith("Q"));
```

- `allMatch` 是否所有元素满足条件
- `noneMatch` 是否所有元素都不满足条件

## Collect

- `iterator` 返回元素的 iterator
- `forEach` 为每个元素依次应用函数
  - 对 parallelStream，`forEach` 以任意顺序遍历元素。`forEachOrdered` 可以按顺序访问元素，但可能放弃部分或全部并行的好处
- `toArray` 返回元素数组，由于运行时无法创建泛型数组，所以 `stream.toArray()` 返回 `Object[]`，如果想返回泛型数组，可以传入数组的构造函数

```java
String[] result = stream.toArray(String[]::new); 
   // stream.toArray() 返回 Object[]
```

`collect()` 顾名思义，将流的元素收集起来，转换为特定的类型。

- 收集并转换为 List

```java
List<String> result = stream.collect(Collectors.toList());
```

- 收集并转换为 Set

```java
Set<String> result = stream.collect(Collectors.toSet());
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



## Reduce

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