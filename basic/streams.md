# Stream
- [Stream](#stream)
  - [简介](#简介)
    - [从 Iterate 到 Stream](#从-iterate-到-stream)
  - [创建 Stream](#创建-stream)
    - [创建 stream 示例](#创建-stream-示例)
    - [集合](#集合)
    - [Empty Stream](#empty-stream)
    - [数组](#数组)
    - [Stream.builder](#streambuilder)
    - [generate](#generate)
    - [iterate](#iterate)
    - [基本类型流](#基本类型流)
    - [String 流](#string-流)
  - [引用流](#引用流)
  - [串联操作](#串联操作)
  - [多线程 Stream](#多线程-stream)
  - [Stream 操作](#stream-操作)
    - [Lazy](#lazy)
    - [迭代](#迭代)
    - [filter](#filter)
    - [map](#map)
    - [flatMap](#flatmap)
    - [mapMulti](#mapmulti)
    - [distinct](#distinct)
    - [排序](#排序)
    - [拆分与合并 Stream](#拆分与合并-stream)
    - [peek](#peek)
  - [集约操作](#集约操作)
  - [Optional](#optional)
  - [Collect](#collect)
  - [Reduce](#reduce)

## 简介

stream 和集合很像，允许转换和检索数据，但是也有所不同：

- stream 不保存元素，元素保存在底层集合，或在需要时生成
- stream 不修改数据源。例如，`filter` 方法不从流中移除元素，而是生成一个新的 stream
- stream 操作都尽可能采用 *lazy* 操作，即在需要结果时才真正开始执行

### 从 Iterate 到 Stream

在处理集合时，通常会遍历元素。例如，计算一本书中长度大于 12 单词的数目：

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

使用 Stream 执行相同操作：

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

Stream 指定**做什么，而不是怎么做**：

- `stream` 和 `parallelStream` 为 `words` list 生成一个流
- `filter` 返回一个仅包含长度大于 12 单词的另一个流
- `count` 将流缩减为一个值

这是 stream 的典型工作流，可以将 stream 的操作分为三步：

1. 创建 stream
2. 指定将初始 stream 转换为其它 stream 的中间操作，可能包含多个步骤
3. 使用终端操作产生结果。该操作强制执行前面的 lazy 操作

## 创建 Stream

### 创建 stream 示例

```java
public static <T> void show(String title, Stream<T> stream) {

    final int SIZE = 10;
    List<T> firstElements = stream
            .limit(SIZE + 1)
            .toList();
    System.out.print(title + ": ");
    for (int i = 0; i < firstElements.size(); i++) {
        if (i > 0) System.out.print(", ");
        if (i < SIZE)
            System.out.print(firstElements.get(i));
        else System.out.print("...");
    }
    System.out.println();
}

@Test
void testIter() throws IOException {
    Path path = Path.of("../gutenberg/alice30.txt");
    String contents = Files.readString(path);

    // 数组 -> Stream
    Stream<String> words = Stream.of(contents.split("\\PL+"));
    show("words", words);

    // 数组 -> Stream
    Stream<String> song = Stream.of("gently", "down", "the", "stream");
    show("song", song);

    // 空 Stream
    Stream<String> silence = Stream.empty();
    show("silence", silence);

    // 根据 Supplier 函数生成常量的无限流
    Stream<String> echos = Stream.generate(() -> "Echo");
    show("echos", echos);

    // 根据 Supplier 函数生成随机数的无限流
    Stream<Double> randoms = Stream.generate(Math::random);
    show("randoms", randoms);

    // 生成序列
    Stream<BigInteger> integers = Stream.iterate(BigInteger.ONE, n -> n.add(BigInteger.ONE));
    show("integers", integers);

    // String -> line stream
    Stream<String> greetings = "Hello\nGuten Tag\nBonjour".lines();
    show("greetings", greetings);

    // String -> 单词 stream
    Stream<String> wordsAnotherWay = Pattern.compile("\\PL+").splitAsStream(contents);
    show("wordsAnotherWay", wordsAnotherWay);

    // 文件 -> line stream
    try (Stream<String> lines = Files.lines(path)) {
        show("lines", lines);
    }

    // Iterable -> stream
    Iterable<Path> iterable = FileSystems.getDefault().getRootDirectories();
    Stream<Path> rootDirectories = StreamSupport.stream(iterable.spliterator(), false);
    show("rootDirectories", rootDirectories);

    // Iterator -> Stream
    Iterator<Path> iterator = Path.of("/usr/share/dict/words").iterator();
    Stream<Path> pathComponents = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                    iterator, Spliterator.ORDERED), false);
    show("pathComponents", pathComponents);
}
```

!!! warning
    在执行 stream 操作时，不要修改底层的集合。因为 stream 的中间操作是 lazy 的，所以在终端操作前修改集合，会导致 stream 操作结果变化。

### 集合

对所有集合类型，`Collection` 接口定义的 `stream` 方法可以将集合转换为 `Stream`：

```java
Collection<String> collection = Arrays.asList("a", "b", "c");
Stream<String> streamOfCollection = collection.stream();
```

### Empty Stream

`empty` 方法创建不包含任何元素的流：

```java
Stream<String> silence = Stream.empty();
// 等价于 Stream.<String>empty()
```

其功能类似于 `Optional.empty()`，用于避免返回 `null`：

```java
public Stream<String> streamOf(List<String> list) {
    return list == null || list.isEmpty() ? Stream.empty() : list.stream();
}
```


### 数组

使用 `Stream.of` 将数组转换为 stream：

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

### Stream.builder
使用 builder 需要指定好类型，否则生成的流是 `Stream<Object>` 类型：
```java
Stream<String> streamBuilder =
  Stream.<String>builder().add("a").add("b").add("c").build();
```

### generate

`Stream` 接口有两个生成无限流的静态方法，`generate` 方法根据提供的函数生成流。函数是 `Supplier<T>` 类型，即无参数。

- 生成常量的无限流

```java
Stream<String> echos = Stream.generate(() -> "Echo");
```

- 生成随机数的无限流

```java
Stream<Double> randoms = Stream.generate(Math::random);
```

- 或者限制流的大小

```java
Stream<String> streamGenerated =
  Stream.generate(() -> "element").limit(10);
```

### iterate

`iterate` 根据初始值和函数生成**序列**。例如：

```java
Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO,
        n -> n.add(BigInteger.ONE));
```

方法的第一个参数 `BigInteger.ZERO` 是第一个元素，第二个参数是 `UnaryOperator<T>` 类型的函数，下一个元素为 `f(seed)`，第三个元素为 `f(f(seed))`，依此类推。

- 要生成有限流，可以添加一个 `Predicate` 指定迭代结束的条件

第二个参数为 `Predicate` 类型，满足 `Predicate` 条件时迭代结束。

```java
BigInteger limit = new BigInteger("10000000");
Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO, 
        n -> n.compareTo(limit) < 0, 
        n -> n.add(BigInteger.ONE));
```

或者：

```java
Stream<Integer> streamIterated = Stream.iterate(40, n -> n + 2).limit(20);
```

- 将 `Iterable` 类型转换为 Stream

```java
StreamSupport.stream(iterable.spliterator(), false);
```

### 基本类型流

Java 8 提供了 3 种基本类型的流：int, long 和 double，分别为 `IntStream`, `LongStream`, `DoubleStream`。

使用这些类型可以避免不必要的开箱操作：
```java
IntStream intStream = IntStream.range(1, 3);
LongStream longStream = LongStream.rangeClosed(1, 3);
```

### String 流

- `String` 的 `chars()` 方法生成字符流，由于没有 `CharStream`，以 `IntStream` 表示字符流

```java
IntStream streamOfChars = "abc".chars();
```

- `String.lines` 生成 line stream

```java
Stream<String> greetings = "Hello\nGuten Tag\nBonjour".lines()
```

- `Pattern.splitAsStream` 根据正则表达式将 `CharSequence` 拆分为 stream

例如，将字符串拆分为单词 stream

```java
Stream<String> words =
        Pattern.compile("\\PL+").splitAsStream(contents);
```

- `Scanner.tokens` 将 scanner 内容生成 stream of tokens

```java
Stream<String> words = new Scanner(contents).tokens();
```

- `Files.lines` 将文件内容生成 stream of lines

```java
try (Stream<String> lines = Files.lines(path)) 
{ 
   // Process lines 
}
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

## 多线程 Stream

```java
List<Integer> list = Arrays.asList(1, 2, 3);
Stream<Integer> stream = list.parallelStream();
```

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

### filter

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

`filter` 的参数为 `Predicate<T>` 类型。

### map

`map` 方法根据指定函数将源流转换为一个新的流。即对每个元素应用函数，将函数的返回值生成新的流。

**示例：** 将所有字母转换为小写

```java
Stream<String> lowercaseWords = words.stream().map(String::toLowerCase);
```

- 对自定义函数，一般采用 lambda 表达式

```java
Stream<String> firstLetters = words.stream().map(s -> s.substring(0, 1));
```

- 用来转换类型

```java
List<String> uris = new ArrayList<>();
uris.add("C:\\My.txt");

Stream<Path> stream = uris.stream().map(uri -> Paths.get(uri));
```

### flatMap

如果 mapping 函数生成一个可选结果，或多个结果。例如，假设函数 `codePoints` 生成字符串的所有代码点。那么，`codePoints("Hello 🌐")` 包含 "H", "e", "l", "l", "o", " ", "🌐"。其中 🌐（U+1F310）包含 2 个字符。

如果使用 `map` 函数：

```java
Stream<Stream<String>> result = words.stream().map(w ->
        codePoints(w));
```

会生成类似 [... ["y", "o", "u","r"], ["b", "o", "a", "t"], ...] 的嵌套结果，要获得 [... "y", "o", "u", "r", "b", "o", "a", "t", ...] 形式，应该使用 `flatMap`：

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

## Optional

可选值：

- `Optional.orElse()` 设置默认值
- `Optional.orElseGet()` 计算默认值
- `Optional.orElseThrow` 缺失使抛出异常

执行操作：

- `Optional.ifPresent` 可选值存在时，执行操作
- `Optional.ifPresentOrElse` 可选值存在时执行一个操作，不存在时执行另一个操作

`map` 操作：

```java
Optional<String> transformed =
        optionalString.map(String::toUpperCase);
```

如果 `optionalString` 为空，`transformed` 也为空。

**示例：** 

```java
optionalValue.map(results::add);
```

`optionalValue` 存在时将值条件到 `results` list，为空时不执行任何操作。

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