# DateTimeFormatter

## 简介

该类用于格式化和解析 date-time 对象。提供了格式化和解析的常见接口，并提供了 `DateTimeFormatter` 的常见实现：

- 使用预定义常量，如 [`ISO_LOCAL_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE)
- 使用 pattern letters, 如 `uuuu-MMM-dd`
- 使用本地样式，如 `long` or `medium`

[`DateTimeFormatterBuilder`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatterBuilder.html) 提供更复杂的格式化实现。

主要的 date-time 类提供两种方法，一个用于格式化 `format(DateTimeFormatter formatter)`，一个用于解析 `parse(CharSequence text, DateTimeFormatter formatter)`。例如：

```java
LocalDate date = LocalDate.now();
String text = date.format(formatter);
LocalDate parsedDate = LocalDate.parse(text, formatter);
```

除了格式，还可以使用所需的 `Locale`, `Chronology`, `ZoneId` 和 `DecimalStyle` 常见 formatter。

[`withLocale`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#withLocale-java.util.Locale-) 方法设置 locale 返回一个新的 formatter。locale 会影响某些格式化和解析。例如，[`ofLocalizedDate`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofLocalizedDate-java.time.format.FormatStyle-) 使用特定地区的日期格式格式化日期。

## 预定义 Formatter

| Formatter                                                    | Description                                              | Example                                   |
| :----------------------------------------------------------- | :------------------------------------------------------- | :---------------------------------------- |
| [`ofLocalizedDate(dateStyle)`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofLocalizedDate-java.time.format.FormatStyle-) | Formatter with date style from the locale                | '2011-12-03'                              |
| [`ofLocalizedTime(timeStyle)`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofLocalizedTime-java.time.format.FormatStyle-) | Formatter with time style from the locale                | '10:15:30'                                |
| [`ofLocalizedDateTime(dateTimeStyle)`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofLocalizedDateTime-java.time.format.FormatStyle-) | Formatter with a style for date and time from the locale | '3 Jun 2008 11:05:30'                     |
| [`ofLocalizedDateTime(dateStyle,timeStyle)`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofLocalizedDateTime-java.time.format.FormatStyle-) | Formatter with date and time styles from the locale      | '3 Jun 2008 11:05'                        |
| [`BASIC_ISO_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#BASIC_ISO_DATE) | Basic ISO date                                           | '20111203'                                |
| [`ISO_LOCAL_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE) | ISO Local Date                                           | '2011-12-03'                              |
| [`ISO_OFFSET_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_OFFSET_DATE) | ISO Date with offset                                     | '2011-12-03+01:00'                        |
| [`ISO_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_DATE) | ISO Date with or without offset                          | '2011-12-03+01:00'; '2011-12-03'          |
| [`ISO_LOCAL_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_TIME) | Time without offset                                      | '10:15:30'                                |
| [`ISO_OFFSET_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_OFFSET_TIME) | Time with offset                                         | '10:15:30+01:00'                          |
| [`ISO_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_TIME) | Time with or without offset                              | '10:15:30+01:00'; '10:15:30'              |
| [`ISO_LOCAL_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE_TIME) | ISO Local Date and Time                                  | '2011-12-03T10:15:30'                     |
| [`ISO_OFFSET_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_OFFSET_DATE_TIME) | Date Time with Offset                                    | 2011-12-03T10:15:30+01:00'                |
| [`ISO_ZONED_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_ZONED_DATE_TIME) | Zoned Date Time                                          | '2011-12-03T10:15:30+01:00[Europe/Paris]' |
| [`ISO_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_DATE_TIME) | Date and time with ZoneId                                | '2011-12-03T10:15:30+01:00[Europe/Paris]' |
| [`ISO_ORDINAL_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_ORDINAL_DATE) | Year and day of year                                     | '2012-337'                                |
| [`ISO_WEEK_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_WEEK_DATE) | Year and Week                                            | 2012-W48-6'                               |
| [`ISO_INSTANT`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_INSTANT) | Date and Time of an Instant                              | '2011-12-03T10:15:30Z'                    |
| [`RFC_1123_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#RFC_1123_DATE_TIME) | RFC 1123 / RFC 822                                       | 'Tue, 3 Jun 2008 11:05:30 GMT'            |

