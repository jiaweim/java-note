# Stream 基础

- [Stream 基础](#stream-基础)
  - [简介](#简介)
  - [从 Iterate 到 Stream](#从-iterate-到-stream)
  - [引用流](#引用流)
  - [串联操作](#串联操作)

2023-11-22, 21:31
@author Jiawei Mao
****

## 简介

stream 和集合很像，允许转换和检索数据，但是也有所不同：

- stream 不保存元素，元素保存在底层集合，或在需要时生成
- stream 不修改数据源。例如，`filter` 方法不从流中移除元素，而是生成一个新的 stream
- stream 操作都尽可能采用 *lazy* 操作，即在需要结果时才真正开始执行

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

## 串联操作

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
