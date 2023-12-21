# TemporalAdjusters

在调度应用程序中，经常需要计算“每个月的星期二”这种日期。`TemporalAdjusters` 类提供了许多 static 方法用于执行常见的日期调整功能。

**示例：** 计算每个月的第一个星期二

```java
LocalDate firstTuesday = LocalDate.of(year, month, 1).with(
        TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
```

其中 with 方法的参数，为 `TemporalAdjuster` 类型，返回新的 `LocalDate` 对象。

也可以实现 `TemporalAdjuster` 接口提供自定义调整功能。例如，计算下个工作日：

```java
TemporalAdjuster NEXT_WORKDAY = w ->
{
    var result = (LocalDate) w;
    do {
        result = result.plusDays(1);
    }
    while (result.getDayOfWeek().getValue() >= 6);
    return result;
};
LocalDate backToWork = today.with(NEXT_WORKDAY);
```

需要注意，lambda 表达式的参数类型为 `Temporal`，需要将其转换为 `LocalDate`。

如果想避免该转换，可以使用 `ofDateAdjuster` 方法，其参数为 lambda 表达式，表达式参数为 `UnaryOperator<LocalDate>` 类型：

```java
TemporalAdjuster NEXT_WORKDAY = TemporalAdjusters.ofDateAdjuster(w ->
{
    LocalDate result = w; // No cast
    do {
        result = result.plusDays(1);

    }
    while (result.getDayOfWeek().getValue() >= 6);
    return result;
});
```