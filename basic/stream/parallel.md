# 并行 Stream

- [并行 Stream](#并行-stream)
  - [简介](#简介)
  - [线程安全](#线程安全)
  - [顺序](#顺序)
  - [要点](#要点)

2023-11-24, 17:21
@author Jiawei Mao
****

## 简介

`Stream` 使得批量操作并行化更容易。首先，需要一个并行 `Stream`，`Collection.parallelStream()` 可以从任何集合类型获得并行流：

```java
Stream<String> parallelWords = words.parallelStream();
```

另外，`parallel` 方法可以将串行流转换为并行流：

```java
Stream<String> parallelWords = Stream.of(wordArray).parallel();
```

只要终端方法执行时流处于并行模式，所有中间流操作都将并行化。

## 线程安全

流操作是无状态的，会以任意顺序执行。假设你要计算短单词数目，下面做法不对：

```java
var shortWords = new int[12];
words.parallelStream().forEach(s -> {
    if (s.length() < 12) shortWords[s.length()]++;
});
// ERROR--race condition! 
System.out.println(Arrays.toString(shortWords));
```

传递给 `forEach` 的函数会在多个线程同时运行，且都会更新共享数组 `shortWords`。这是典型的数据争用，多次运行该代码，可能每次结果都不同。

我们需要确保传递给并行流的操作都是线程安全的。最好的方法是不使用 mutable 状态。对上例，可以将字符串按长度分组，然后计数：

```java
Map<Integer, Long> shortWordCounts = words.parallelStream()
        .filter(s -> s.length() < 12)
        .collect(groupingBy(String::length, counting()));
```

## 顺序

**默认**情况下，来自有序集合（如数组，List）、迭代器、生成器，或调用 `Stream.sorted` 生成的流是有序的。计算生成结果的顺序与原元素一致。重复运行相同操作，可以获得完全相同的结果。

有序不代表不能并行化。例如，计算 `stream.map(fun)` 时，可以将流分为 n 个片段，并行处理后将结果按顺序组装起来。

如果不要求有序，部分操作并行化效果更好。调用 `Stream.unordered` 表示不要求有序。`Stream.distinct` 方法从中受益，在有序流中，`distinct` 对相同元素总是保留第一个，这有碍并行化。

放弃有序 `limit` 也能受益。如果你只需要 n 个元素，不在乎顺序，可调用：

```java
Stream<String> sample = words.parallelStream().unordered().limit(n);
```

如 [toMap](toMap.md) 中所述，合并 maps 操作也很昂贵。因此 `Collectors.groupingByConcurrent` 采用共享 `ConcurrentMap`。为了从并行受益，map 值顺序与 Stream 顺序不要求一样。

```java
Map<Integer, List<String>> result = 
words.parallelStream().collect( 
   Collectors.groupingByConcurrent(String::length)); 
   // Values aren't collected in stream order
```

当然，如果下游 collector 不受顺序影响，则无需在意顺序，例如：

```java
Map<Integer, Long> wordCounts 
   = words.parallelStream() 
      .collect( 
         groupingByConcurrent( 
            String::length, 
            counting()));
```

## 要点

不要认为将任何 Stream 转换为 parallelStream 就能提速。要考虑：

- 并行开销较大，数据量较大时才能从中受益
- 只有在底层数据能够有效地拆分为多个部分，并行才有益
- parallelStream 使用的线程池有可能被阻塞操作（如文件 IO，网络访问）堵死

并行流适合内存中大量数据的集合以及计算密集型任务。

!!! tip
    Java 9 之前，将 `Files.lines` 返回的 Stream 并行化没有任何意义，因为文件是按顺序读取的。现在该方法使用 memory-mapped 文件，能够有效分割任务，因此并行可能提高性能。

并行流默认使用 `ForkJoinPool.commonPool` 返回全局 fork-join 线程池。如果操作不阻塞，并且没有与其它任务共享池，则没问题。

有一个技巧可以替换为不同的线程池，将操作放入自定义池的 submit 方法：

```java
ForkJoinPool customPool = . . .; 
result = customPool.submit(() -> 
   stream.parallel().map(. . .).collect(. . .)).get();
```

或者，异步：

```java
CompletableFuture.supplyAsync(() -> 
   stream.parallel().map(. . .).collect(. . .), 
   customPool).thenAccept(result -> . . .);
```

