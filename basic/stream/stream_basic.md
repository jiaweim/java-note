# Stream 概述

- [Stream 概述](#stream-概述)
  - [简介](#简介)
  - [操作通道](#操作通道)
  - [并行](#并行)
    - [无干扰](#无干扰)
    - [无状态](#无状态)
    - [副作用](#副作用)
  - [从 Iterate 到 Stream](#从-iterate-到-stream)
  - [引用流](#引用流)

2023-11-22, 21:31
@author Jiawei Mao
****

## 简介

`Stream`, `IntStream`, `LongStream`, `DoubleStream` 提供了对 `Object`, `int`, `long`, `double` 流封装。

stream 和集合很像，允许转换和检索数据，但是也有所不同：

- stream 不保存元素，元素保存在底层集合，或在需要时生成
- stream 不修改数据源。例如，`filter` 方法不从流中移除元素，而是生成一个新的 stream
- stream 操作都尽可能采用 *lazy* 操作，即在需要结果时才真正开始执行。流操作分为中间操作和终结操作，其中中间操作总是 lazy 的。

创建流的方式有许多：

- 通过 `Collection` 的 `stream()` 和 `parallelStream()` 方法
- `Arrays.stream(Object[])`
- Stream 类中的工厂方法，如 `Stream.of(Object[])`, `IntStream.range(int, int)`, `Stream.iterate(Object, UnaryOperator)` 等
- 文件 lines 可以通过 `BufferedReader.lines()` 获得
- 文件路径 Stream 可以通过 `Files` 获得
- 随机数可以通过 `Random.ints()` 获得
- JDK 中还有其它兼容 Stream 方法，包括 `BitSet.stream()`, `Pattern.splitAsStream(java.lang.CharSequence)`, `JarFile.stream()`

## 操作通道

要对原数据进行一系列的操作，需要三部分：

- **源数据**：如集合、数组、generator 函数或 I/O channel
- **中间操作**：0 或多个，如 `Stream.filter`, `Stream.map` 等
- **末端操作**：如 `Stream.forEach`, `Stream.reduce` 等

**中间操作**返回新的 stream 对象，中间操作总是 `lazy`。诸如 `filter()` 之类的中间操作创建一个新的流，在遍历时返回包含满足 predicate 的 元素。但是，该遍历在执行末端操作时才开始。

**末端操作**，如 `Stream.forEach`, `IntStream.sum`，遍历流以生成结果或产生副作用。执行末端操作后，流被消耗，无法再用。如果需要再次遍历相同的数据源，只能返回数据源重新获取流。

- 在几乎所有情况，末端操作都是即时（`eager`）的，末端操作返回前完成数据源的遍历和管道处理。
- 末端操作 `iterator()` 和 `spliterator()` 例外，当现有操作无法完成任务，这两个操作为额外操作提供遍历管道。

**lazy** 操作的优点：

- `lazy` 操作可以显著提高效率；在诸如 filter-map-sum 之类的管道中，可以将 filtering, mapping 和 summing 融合到单次数据迭代中，并维护最小的中间状态。
- `lazy` 操作在没有必要时可以无需检查所有数据，例如找到一个长度超过 1000 个字符的字符串，该操作在找到所需字符串后，无序继续检查源数据的余下字符串。当源数据非常大甚至无限时，该特点十分重要。

**中间操作**可以进一步分为无状态操作和有状态操作：

- 无状态操作，如 `filter` 和 `map`，每个元素独立处理，不受其它元素干扰
- 有状态操作，如 `distinct` 和 `sorted`，在处理新元素时可能需要结合处理过的元素的状态

**有状态操作**在产生结果之前可能需要处理整个输入，如排序操作。因此，在并行计算中，包含有状态中间操作的管道可能需要多次遍历数据，或需要缓冲某些数据。中间操作完全是**无状态操作**的管道则只需要遍历一次数据，就能以最小的缓存完成串行或并行处理。

还有一类操作称为**短路操作**。包括：

- 将无限流转换为有限流的中间操作
- 对无限输入，能够在有限时间内完成的末端操作

## 并行

使用显式 `for` 循环处理元素本质上是串行的。流通过将计算重构为聚合操作的管道实现并行。所有流操作都可以串行或并行执行。例如，`Collection`包含 `Collection.stream()` 和 `Collection.parallelStream()` 方法，分别生成串行流和并行流；其它包含流的方法，如 `IntStream.range(int, int)` 虽然生成的是串行流，但调用 `BaseStream.parallel()` 即可并行化。例如，并行计算 widgets 的权重和：

```java
int sumOfWeights = widgets.parallelStream()
                          .filter(b -> b.getColor() == RED)
                          .mapToInt(b -> b.getWeight())
                          .sum();
```

流以串行还是并行方式执行，可以通过调用 `isParallel()` 判断；调用 `BaseStream.sequential()` 和 `BaseStream.parallel()` 可以修改执行方式。启动末端操作后，流管道将按串行或并行方式执行。

除了明确标记为**不确定的操作**，如 `findAny()`，串行和并行不应该改变计算结果。

大多数流操作都接受用户指定操作的参数（**行为参数**），这些参数通常是 lambda 表达式。为了保证正确性，这些操作必须互不干扰，大多时候还必须无状态。这些参数为函数接口，如 `Function`，且通常是 lambda 表达式或方法引用。

### 无干扰

Stream 可以对各种数据源并行执行操作，包括如 `ArrayList` 之类的非线程安全的集合。但是有个前提：在执行流管道时**不会干扰数据源**。除了 `iterator()` 和 `spliterator()`，操作从调用末端操作时开始，末端操作返回时结束。对大多数据源，防止干扰意味着在流管道执行期间确保数据源不被修改。有个例外值得注意，并发集合专门为并发修改设计，流管道执行期间也能修改。并发流数据源的 `Spliterator` 属性值为 `CONCURRENT`。

因此，当数据源非并发时，流通道中的行为参数不应修改数据源。当行为参数修改**非并发数据源**，就称该行为参数干扰数据源。不干扰数据源的需求适用于所有管道，包括并行和串行流。除非数据源为并发类型，否则在流管道执行期间修改数据源会抛出异常、得到错误答案或不一致的行为。

在末端操作开始前，可以修改数据源，这些修改会影响流执行结果。例如：

```java
List<String> l = new ArrayList(Arrays.asList("one", "two"));
Stream<String> sl = l.stream();
l.add("three");
String s = sl.collect(joining(" "));
```

其中 `s` 会 "one two three"。

### 无状态

![](images/2023-12-02-19-30-58.png){width="600px"}

如果流操作的行为参数是有状态的，则流管道结果可能不确定或不正确。lambda 表达式或实现函数接口的对象有状态，指其结果依赖于流管道执行期间可能发生变化的任何状态。例如：

```java
Set<Integer> seen = Collections.synchronizedSet(new HashSet<>());
stream.parallel().map(e -> { if (seen.add(e)) return 0; else return e; })...
```

如果并行执行 mapping 操作，由于线程调度差异，多次运行，相同输入可能获得不同结果，而使用无状态 lambda 表达式，结果总是相同的。

!!! note
    从行为参数访问可变状态会带来安全性和性能问题；如果不同步对数据的访问，会出现数据争用；如果同步对该可变状态的访问，又可能因为争用而破坏并行性，即无法从并行中受益。最好的办法是完全避免在流操作中使用有状态行为参数。

### 副作用

不建议在流操作的行为参数中包含副作用，因为它们通常会无意中导致违反无状态的需求，以及其它线程安全隐患。

如果行为参数包含副作用，除非明确声明，否则不能保证做这些副作用对其他线程可见。也不能保证在同一流管道中对相同元素的不同操作在同一线程中执行。此外，这些副作用的顺序可能与预期不一致。




## 从 Iterate 到 Stream

在处理集合时，通常会遍历元素。例如，计算书中长度大于 12 单词的数目：

```java
String contents = Files.readString(Path.of("alice.txt"));
List<String> words = List.of(contents.split("\\PL+"));
```

然后进行迭代：

```java
int count = 0;
for (String w : words) {
    if (w.length() > 12) count++;
}
```

使用 `Stream` 执行与迭代相同功能的操作：

```java
long count = words.stream()
        .filter(w -> w.length() > 12)
        .count();
```

将 `stream` 改为 `parallelStream`，上述操作就变成并行过滤和计数：

```java
long count = words.parallelStream()
        .filter(w -> w.length() > 12)
        .count();
```

代码解释：

- `stream()` 和 `parallelStream()` 为 `words` list 生成一个流
- `filter` 返回一个仅包含长度大于 12 单词的另一个流
- `count` 将流缩减为一个值

这是 stream 的典型工作流，可以将 stream 的操作分为三步：

1. 创建 stream
2. 指定将初始 stream 转换为其它 stream 的中间操作，可能包含多个步骤
3. 使用终端操作产生结果。该操作强制执行前面的 lazy 操作

**示例**

```java
URL resource = StreamTest.class.getResource("alice30.txt");
String contents = Files.readString(Path.of(resource.toURI()));
List<String> words = List.of(contents.split("\\PL+"));// \\PL+ 匹配所有非字符

long count = 0;
for (String w : words) {
    if (w.length() > 12) count++;
}
System.out.println(count);

count = words.stream().filter(w -> w.length() > 12).count();
System.out.println(count);

count = words.parallelStream().filter(w -> w.length() > 12).count();
System.out.println(count);
```

## 引用流

如果只使用中间操作，则可以对引用的流继续操作，如果包含终结操作，会导致流不再可用。

例如：

```java
Stream<String> stream = Stream.of("a", "b", "c")
                .filter(element -> element.contains("b"));
Optional<String> anyElement = stream.findAny();
```
然后再次使用该流：

```java
Optional<String> firstElement = stream.findFirst();
```

会抛出 `IllegalStateException`。即流不可重复使用。
