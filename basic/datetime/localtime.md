# LocalTime

- [LocalTime](#localtime)
  - [简介](#简介)
  - [LocalDateTime](#localdatetime)
  - [总结](#总结)

2023-12-19, 01:15
****

## 简介

LocalTime 表示一天的时间，如 15:30:00。可以使用 now 或 of 方法创建 LocalTime：

```java
LocalTime rightNow = LocalTime.now();
LocalTime bedtime = LocalTime.of(22, 30); // or 
LocalTime.of(22, 30, 0);
```

`LocalTime` 支持常见的时间操作。如 plus 和 minus：

```java
LocalTime wakeup = bedtime.plusHours(8); // wakeup is 6:30:00    
```

> [!NOTE]
>
> `LocalTime` 不关心 AM/PM 问题，这是格式化的问题。

## LocalDateTime

LocalDateTime 表示日期和时间。该类适合在固定时区中存储时间点。如果需要跨越夏令时的计算，或者需要在不同时区处理用户，那么建议使用 `ZonedDateTime`。



## 总结

