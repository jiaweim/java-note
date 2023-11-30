# 创建 Stream

- [创建 Stream](#创建-stream)
  - [创建 stream 示例](#创建-stream-示例)
  - [集合](#集合)
  - [数组](#数组)
  - [Empty Stream](#empty-stream)
  - [无限流](#无限流)
    - [generate](#generate)
    - [iterate](#iterate)
  - [ofNullabble](#ofnullabble)
  - [Iterable](#iterable)
  - [Iterator](#iterator)
  - [Stream.builder](#streambuilder)
  - [基本类型流](#基本类型流)
  - [String 流](#string-流)
  - [concat](#concat)

2023-11-22, 21:47
@author Jiawei Mao
****

## 创建 stream 示例

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

## 集合

对所有集合类型，`Collection` 接口定义的 `stream` 方法返回 `Stream`：

```java
Collection<String> collection = Arrays.asList("a", "b", "c");
Stream<String> streamOfCollection = collection.stream();
```

## 数组

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

- `Arrays.stream(array, from, to)` 可以使用数组的部分或全部元素生成流

```java
Arrays.stream(array, from, to)
```

## Empty Stream

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

## 无限流

`Stream` 接口有两个生成无限流的静态方法，其中 `generate` 方法根据提供的函数生成流。函数是 `Supplier<T>` 类型，即无参函数。

### generate

循环调用 `Supplier<T>` 函数生成无限流。

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

方法参数：

- 第一个参数 `BigInteger.ZERO` 是序列的第一个元素，也是 `seed`
- 第二个参数是 `UnaryOperator<T>` 类型函数，以 `seed` 为参数生成下一个元素为 `f(seed)`
- 第三个元素为 `f(f(seed))`，依此类推

要生成有限流，可以添加一个 `Predicate` 指定迭代结束的条件

第三个参数为 `Predicate` 类型，满足 `Predicate` 条件时迭代结束。

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

## ofNullabble

`Stream.ofNullable` 使用指定对象生成包含 0 或 1 个元素的流，与 flatMap 结合使用体验较好。具体可参考 [Optional](optional.md)。

## Iterable

将 `Iterable` 类型转换为 `Stream`

```java
StreamSupport.stream(iterable.spliterator(), false);
```

## Iterator

```java
StreamSupport.stream(Spliterators.spliteratorUnknownSize( 
   iterator, Spliterator.ORDERED), false);
```

## Stream.builder

使用 builder 需要指定好类型，否则生成的流是 `Stream<Object>` 类型：

```java
Stream<String> streamBuilder =
  Stream.<String>builder().add("a").add("b").add("c").build();
```

- 生成 `DoubleStream` 流

```java
DoubleStream.Builder builder = DoubleStream.builder();

for (Double number : list) {
    builder.add(number);
}
return builder.build();
```


## 基本类型流

Java 8 提供了 3 种基本类型的流：int, long 和 double，分别为 `IntStream`, `LongStream`, `DoubleStream`。

使用这些类型可以避免不必要的开箱操作：
```java
IntStream intStream = IntStream.range(1, 3);
LongStream longStream = LongStream.rangeClosed(1, 3);
```

- 使用 Random 生成随机数流

```java
Random random = new Random();
DoubleStream doubleStream = random.doubles(10);
double average = doubleStream
        .parallel()
        .peek(System.out::println)
        .average().getAsDouble();
```

## String 流

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

## concat

`Stream.concat` 合并两个 Stream，例如：

```java
Stream<String> s1 = Stream.of("1", "2", "3", "4");
Stream<String> s2 = Stream.of("5", "6", "7", "8");

Stream<String> s = Stream.concat(s1, s2);
s.parallel().forEach(s3 -> System.out.printf("%s : ", s3));
```

```
6 : 5 : 8 : 7 : 3 : 4 : 2 : 1 : 
```