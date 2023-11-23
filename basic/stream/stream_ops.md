# Stream 操作

- [Stream 操作](#stream-操作)
  - [filter](#filter)
  - [map](#map)

***

## filter

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

## map

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