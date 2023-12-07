# 时间线

## 简介

从历史上看，基本时间单位**秒**从地球自转得出，自转一圈 24h 有 24x60x60=86400 秒，所以精确定义一秒的长度似乎是一个天文测量问题。可惜，地球自转略有晃动，时间需要更精确的定义。1967 年，根据 铯-133 原子的衰变性质得到一个与历史相符的秒的新精确定义，从那时起，光官方时间由原子钟确定。

官方计时为了将绝对时间和地球自转保持保持一致，从 1972 年起，偶尔会插入**润秒**。润秒使得时间处理更痛苦，许多计算机系统每天保持 86,400 秒，在润秒再手动调整。

Java Date 和 Time API 规范要求：

- 每天 86400 秒
- 每天中午与官方时间完全匹配

## 时间点

在 Java 中，用 `Instant` 类表示时间线上的一个点，将伦敦格林威治皇家天文台本初子午线 1970 年1月1日午夜定义为纪元起点。这与 UNIX/POSIX 使用的时间一致。从这个点开始，时间以纳秒精度，每天 86400 秒来计算。

- `Instant` 值可以追溯到 10 亿年前（`Instant.MIN`）。该时间不足以描述宇宙的年龄（大约 135 亿年），但日常应用足够。
- `Instant.MAX` 对应 10 亿年的 12 月 31 号。

静态方法 `Instant.now()` 返回当前时间。可以使用 `equals` 和 `compareTo` 比较 `Instant`，因此可以将其作为时间戳。

## 时间段

静态方法 `Duration.between` 返回两个 `Instant` 之间的差值。例如，查看算法运行时间：

```java
Instant start = Instant.now();
runAlgorithm();
Instant end = Instant.now();
Duration timeElapsed = Duration.between(start, end);
long millis = timeElapsed.toMillis();
```

`Duration` 表示两个时间点之间的时间段。调用 `Duration` 的 `toNanos`, `toMillis`, `toSeconds`, `toMinutes`, `toHours`, `toDays` 等方法可以转换为所需单位。

如果需要纳秒级的精度，需要注意溢出问题。`long` 