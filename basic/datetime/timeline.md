# 时间线

- [时间线](#时间线)
  - [简介](#简介)
  - [时间点](#时间点)
  - [时间段](#时间段)
  - [示例](#示例)

2023-12-18, 23:48
****

## 简介

从历史上看，基本时间单位**秒**从地球自转得出，自转一圈 24h 有 24x60x60=86400 秒，所以精确定义一秒的长度似乎是一个天文测量问题。可惜，地球自转略有晃动，时间需要更精确的定义。1967 年，根据铯-133 原子的衰变性质得到一个与历史相符的秒的新精确定义，从那时起，官方时间由原子钟确定。

官方计时为了将绝对时间和地球自转保持保持一致，从 1972 年起，偶尔会插入**润秒**。润秒使得时间处理更痛苦，许多计算机系统每天保持 86,400 秒，在润秒再手动调整。

Java Date 和 Time API 规范要求：

- 每天 86400 秒
- 每天中午与官方时间完全一致

## 时间点

在 Java 中，用 `Instant` 类表示时间线上的一个点，将伦敦格林威治皇家天文台本初子午线 1970 年1月1日午夜定义为纪元起点，与 UNIX/POSIX 使用的时间一致。从这个点开始，时间以纳秒精度，每天 86400 秒来计算。

- `Instant` 值可以追溯到 10 亿年前（`Instant.MIN`）。该时间不足以描述宇宙的年龄（大约 135 亿年），但日常使用足够。
- `Instant.MAX` 对应 10 亿年的 12 月 31 号。

静态方法 `Instant.now()` 返回当前时间。可以使用 `equals` 和 `compareTo` 比较 `Instant`，因此可以将其作为时间戳。

## 时间段

静态方法 `Duration.between` 返回两个 `Instant` 之间的差值。例如，查看算法运行时间：

```java
Instant start = Instant.now();
// runAlgorithm();
Instant end = Instant.now();
Duration timeElapsed = Duration.between(start, end);
long millis = timeElapsed.toMillis();
```

`Duration` 表示两个时间点之间的时间段。调用 `Duration` 的 `toNanos`, `toMillis`, `toSeconds`, `toMinutes`, `toHours`, `toDays` 等方法可以转换为所需单位。

!!! note
    在 Java 8 中，调用 `getSeconds` 而不是 `toSeconds`。

如果需要纳秒级精度，需要注意溢出问题。`long` 值能保存近 300 年的纳秒值，如果你的时间比这短，可以直接转换为纳秒。如果超过该范围，`Duration` 内部会用一个 `long` 值存储秒，一个额外的 `int` 存储余下纳秒值。

Duration 支持许多数学运算。例如，查看一个算法是否比另一个算法快 10 倍以上：

```java
Duration timeElapsed2 = Duration.between(start2, end2);
boolean overToenTimesFaster = timeElapsed.multipliedBy(10)
        .minus(timeElapsed2).isNegative();
```

这里只是为了展示语法。因为算法不可能运行上百年，所以可以直接使用：

```java
boolean overTenTimesFaster = timeElapsed.toNanos() * 10 <
        timeElapsed2.toNanos();
```

!!! note
    `Instant` 和 `Duration` 类都是 immutable，所有方法，如 `multipliedBy` 和 `minus`，都返回新的实例。

## 示例

使用 `Instant` 和 `Duration` 记录两个算法的运行时间：

```java
public static void runAlgorithm() {
    int size = 10;
    ArrayList<Integer> list = new Random().ints()
            .map(operand -> operand % 100).limit(size)
            .boxed().collect(Collectors.toCollection(ArrayList::new));
    Collections.sort(list);
    System.out.println(list);
}

public static void runAlgorithm2() {
    int size = 10;
    List<Integer> list = new Random().ints()
            .map(operand -> operand % 100).limit(size)
            .boxed().collect(Collectors.toCollection(ArrayList::new));
    while (!IntStream.range(1, list.size())
            .allMatch(value -> list.get(value - 1).compareTo(list.get(value)) <= 0))
        Collections.shuffle(list);
    System.out.println(list);
}

public static void main(String[] args) {
    Instant start = Instant.now();
    runAlgorithm();
    Instant end = Instant.now();
    Duration timeElapsed = Duration.between(start, end);
    long millis = timeElapsed.toMillis();
    System.out.printf("%d milliseconds\n", millis);

    Instant start2 = Instant.now();
    runAlgorithm2();
    Instant end2 = Instant.now();
    Duration timeElapsed2 = Duration.between(start2, end2);
    System.out.printf("%d milliseconds\n", timeElapsed2.toMillis());

    boolean overTenTimesFaster = timeElapsed.multipliedBy(10)
            .minus(timeElapsed2).isNegative();
    System.out.printf("The first algorithm is %smore than ten times faster", overTenTimesFaster ? "" : "not ");
}
```

```
[-94, -68, -44, -14, 0, 29, 64, 70, 90, 90]
3 milliseconds
[-85, -41, -37, 0, 17, 20, 23, 28, 34, 74]
420 milliseconds
The first algorithm is more than ten times faster
```