# ZonedTime

## 简介

Internet Assigned Numbers Authority (IANA)包含世界上所有时区的数据库，并每年更新多次。大部分更新都设计修改时间的规则。Java 使用 IANA 数据库。

每个时区都有一个 ID，如 `America/New_York` 或 `Europe/Berlin`。调用 `ZoneId.getAvailableZoneIds` 可以查看所有时区，目前已有仅 600 个时区。

给定时区 ID，static 方法 `ZoneId.of(id)` 返回对应的 `ZoneId` 对象。调用 `local.atZone(zoneId)` 可以使用该对象将 `LocalDateTime` 转换为 `ZonedDateTime`;或者调用 `ZonedDateTime.of(year, month, day, hour, minute, second, nano, zoneId)` 来创建 `ZonedDateTime`。例如：

```java
ZonedDateTime apollo11launch = ZonedDateTime.of(1969, 7, 16,
        9, 32, 0, 0,
        ZoneId.of("America/New_York"));
System.out.println(apollo11launch);
// 1969-07-16T09:32-04:00[America/New_York]
```

`ZonedDateTime` 是一个特定时刻。调用 `apollo11launch.toInstant` 可以将其转换为 `Instant`；相反，如果有一个 instant 时间，调用 `instant.atZone(ZoneId.of("UTC"))` 可以将其转换为格林威治时区的 `ZonedDateTime`。

`ZonedDateTime` 的许多方法与 `LocalDateTime` 一样。大多数很简单，不过夏令时引入了一定复杂性。

夏令时开始时，时钟提起一小时。当你构建的时间恰好在跳过的这一个小时，会发生什么？例如，2013年，中欧在 3 月 31 日凌晨 2 点切换到夏令时，如果你试图构造不存在的时间 3 月 31 日 2:30，实际会得到 3:30.

```java
ZonedDateTime skipped = ZonedDateTime.of(
        LocalDate.of(2013, 3, 31),
        LocalTime.of(2, 30),
        ZoneId.of("Europe/Berlin"));
// 2013-03-31T03:30+02:00[Europe/Berlin]
```

相反，当夏令时结束时，时钟向后拨一个小时，因此有两个 instants 具有相同的 LocalTime。当你在这两个 instants 之间构造时间时，会得到两者较早的一个：

```java
ZonedDateTime ambiguous = ZonedDateTime.of(
        LocalDate.of(2013, 10, 27), // End of daylight savings time
        LocalTime.of(2, 30),
        ZoneId.of("Europe/Berlin"));
// 2013-10-27T02:30+02:00[Europe/Berlin]
ZonedDateTime anHourLater = ambiguous.plusHours(1);
// 2013-10-27T02:30+01:00[Europe/Berlin]
```

一个小时后，时间有相同的小时和分钟，但是 zone offset 发生了变化。

调整日期跨越夏令时时也需要注意。例如，设置下周会议，不要添加 7 天:

```java
ZonedDateTime nextMeeting = meeting.plus(Duration.ofDays(7)); 
   // Caution! Won't work with daylight savings time
```

而应该使用 Period 类：

```java
ZonedDateTime nextMeeting = meeting.plus(Period.ofDays(7)); // OK
```

