# LocalDate

- [LocalDate](#localdate)
  - [简介](#简介)
  - [LocalDate](#localdate-1)
  - [Period](#period)
  - [LocalDate Stream](#localdate-stream)
  - [示例](#示例)
  - [方法总结](#方法总结)

2023-12-19, 00:34
****

## 简介

从绝对时间切换到人类时间，Java API 提供了两种人类时间：local date/time 和 zoned time。

local date/time 有日期和/或时间，但没有时区信息。例如，1903年6月14日，该日期既没有一天里的时间，也没有时区信息，因此没有对应的精确 instant 时间。相反，如 1969 年 7 月 16 日，09:32:00 EDT 为 zoned date/time，代表了时间线上的一个精确 instant。

在许多计算中，不需要时区信息，有时考虑时区反倒麻烦。生日、假期、日程安排等等，通常都推荐使用 local date/time。

## LocalDate

`LocalDate` 包含年月日信息。可以用 `static` 方法 `now` 或 `of` 创建：

```java
LocalDate today = LocalDate.now();
LocalDate birthday = LocalDate.of(1903, 6, 14);
birthday = LocalDate.of(1903, Month.JUNE, 14);
```

不像 UNIX 和 `java.util` 中的约定，这里可以直接使用年月日创建，其中月还可以使用 `Month` enum。

`LocalDate` 支持许多日期相关计算。例如，程序员节事每年的第 256 天，计算方式：

```java
LocalDate programmersDay = LocalDate.of(2014, 1, 1)
        .plusDays(255); // 9 月 13 号，闰年是 9 月 12
```

## Period

两个 `Instant` 之间的距离为 `Duration`。而两个 `LocalDate` 之间的距离为 `Period`。例如：

- `birthday.plus(Period.ofYears(1))` 获得下一年的生日，也可以用 `birthday.plusYears(1)`
- 使用 `birthday.plus(Period.ofDays(365))` 在闰年会出错

`until` 返回两个 `LocalDate` 之间的差值。例如：

```java
independenceDay.until(christmas)
```

返回包含 5 个月 21 天的 `Period`。该方法其实不是很有用，因为每个月的天数不同。获取天数：

```java
independenceDay.until(christmas, ChronoUnit.DAYS) // 174 days    
```

> [!CAUTION]
>
> `LocalDate` 的有些方法会创建不存在的日期。例如，1月31日增加一个月不应该返回2月31日。不过这些方法不会抛出异常，而是返回最后一个有效日期。

```java
LocalDate localDate = LocalDate.of(2016, 1, 31).plusMonths(1);
System.out.println(localDate);
```

```
2016-02-29
```

```java
LocalDate localDate = LocalDate.of(2016, 3, 31).minusMonths(1);
System.out.println(localDate);
```

```
2016-02-29
```

`getDayOfWeek` 返回一周的某一天。返回枚举 `DayOfWeek` 类型。`DayOfWeek.MONDAY` 对应数值 1，`DayOfWeek.SUNDAY` 对应数值 7。例如：

```java
LocalDate.of(1900, 1, 1).getDayOfWeek().getValue()
```

返回 1。

`DayOfWeek` enum 也有一些便捷方法，如 `plus`, `minus` 等。例如，`DayOfWeek.SATURDAY.plus(3)` 返回 `DayOfWeek.TUESDAY`。

## LocalDate Stream

Java 9 在 `LocalDate` 中添加了 `datesUntil` 方法，生成 `LocalDate` stream。

```java
LocalDate start = LocalDate.of(2000, 1, 1);
LocalDate endExclusive = LocalDate.now();
Stream<LocalDate> allDays = start.datesUntil(endExclusive);
Stream<LocalDate> firstDaysInMonth = start.datesUntil(endExclusive, Period.ofMonths(1));
```

## 示例

除了 LocalDate，还有 MonthDay, YearMonth, Year 等描述部分日期。下面是对 LocalDate 的完整示例：

```java
LocalDate today = LocalDate.now();
System.out.println("today:" + today);

LocalDate birthday = LocalDate.of(1903, 6, 14);
birthday = LocalDate.of(1903, Month.JUNE, 14);
System.out.println("birthday:" + birthday);

LocalDate programmersDay = LocalDate.of(2018, 1, 1).plusDays(255);
System.out.println("programmers day:" + programmersDay);

LocalDate independenceDay = LocalDate.of(2018, Month.JULY, 4);
LocalDate christmas = LocalDate.of(2018, Month.DECEMBER, 25);

System.out.println("Until christmas: " + independenceDay.until(christmas));
System.out.println("Until christmas: " + independenceDay.until(christmas, ChronoUnit.DAYS));

System.out.println(LocalDate.of(2016, 1, 31).plusMonths(1));
System.out.println(LocalDate.of(2016, 3, 31).minusMonths(1));

DayOfWeek startOfLastMillennium = LocalDate.of(1900, 1, 1).getDayOfWeek();
System.out.println("startOfLastMillennium: " + startOfLastMillennium);
System.out.println(startOfLastMillennium.getValue());
System.out.println(DayOfWeek.SATURDAY.plus(3));

LocalDate start = LocalDate.of(2000, 1, 1);
LocalDate endExclusive = LocalDate.now();
Stream<LocalDate> firstDaysInMonth = start.datesUntil(endExclusive, Period.ofMonths(1));
System.out.println("firstDaysInMonth:" + firstDaysInMonth.toList());
```

```
today:2023-12-19
birthday:1903-06-14
programmers day:2018-09-13
Until christmas: P5M21D
Until christmas: 174
2016-02-29
2016-02-29
startOfLastMillennium: MONDAY
1
TUESDAY
firstDaysInMonth:[2000-01-01, 2000-02-01, 2000-03-01, 2000-04-01, 2000-05-01, 2000-06-01, 2000-07-01, 2000-08-01, 2000-09-01, 2000-10-01, 2000-11-01, 2000-12-01, 2001-01-01, 2001-02-01, 2001-03-01, 2001-04-01, 2001-05-01, 2001-06-01, 2001-07-01, 2001-08-01, 2001-09-01, 2001-10-01, 2001-11-01, 2001-12-01, 2002-01-01, 2002-02-01, 2002-03-01, 2002-04-01, 2002-05-01, 2002-06-01, 2002-07-01, 2002-08-01, 2002-09-01, 2002-10-01, 2002-11-01, 2002-12-01, 2003-01-01, 2003-02-01, 2003-03-01, 2003-04-01, 2003-05-01, 2003-06-01, 2003-07-01, 2003-08-01, 2003-09-01, 2003-10-01, 2003-11-01, 2003-12-01, 2004-01-01, 2004-02-01, 2004-03-01, 2004-04-01, 2004-05-01, 2004-06-01, 2004-07-01, 2004-08-01, 2004-09-01, 2004-10-01, 2004-11-01, 2004-12-01, 2005-01-01, 2005-02-01, 2005-03-01, 2005-04-01, 2005-05-01, 2005-06-01, 2005-07-01, 2005-08-01, 2005-09-01, 2005-10-01, 2005-11-01, 2005-12-01, 2006-01-01, 2006-02-01, 2006-03-01, 2006-04-01, 2006-05-01, 2006-06-01, 2006-07-01, 2006-08-01, 2006-09-01, 2006-10-01, 2006-11-01, 2006-12-01, 2007-01-01, 2007-02-01, 2007-03-01, 2007-04-01, 2007-05-01, 2007-06-01, 2007-07-01, 2007-08-01, 2007-09-01, 2007-10-01, 2007-11-01, 2007-12-01, 2008-01-01, 2008-02-01, 2008-03-01, 2008-04-01, 2008-05-01, 2008-06-01, 2008-07-01, 2008-08-01, 2008-09-01, 2008-10-01, 2008-11-01, 2008-12-01, 2009-01-01, 2009-02-01, 2009-03-01, 2009-04-01, 2009-05-01, 2009-06-01, 2009-07-01, 2009-08-01, 2009-09-01, 2009-10-01, 2009-11-01, 2009-12-01, 2010-01-01, 2010-02-01, 2010-03-01, 2010-04-01, 2010-05-01, 2010-06-01, 2010-07-01, 2010-08-01, 2010-09-01, 2010-10-01, 2010-11-01, 2010-12-01, 2011-01-01, 2011-02-01, 2011-03-01, 2011-04-01, 2011-05-01, 2011-06-01, 2011-07-01, 2011-08-01, 2011-09-01, 2011-10-01, 2011-11-01, 2011-12-01, 2012-01-01, 2012-02-01, 2012-03-01, 2012-04-01, 2012-05-01, 2012-06-01, 2012-07-01, 2012-08-01, 2012-09-01, 2012-10-01, 2012-11-01, 2012-12-01, 2013-01-01, 2013-02-01, 2013-03-01, 2013-04-01, 2013-05-01, 2013-06-01, 2013-07-01, 2013-08-01, 2013-09-01, 2013-10-01, 2013-11-01, 2013-12-01, 2014-01-01, 2014-02-01, 2014-03-01, 2014-04-01, 2014-05-01, 2014-06-01, 2014-07-01, 2014-08-01, 2014-09-01, 2014-10-01, 2014-11-01, 2014-12-01, 2015-01-01, 2015-02-01, 2015-03-01, 2015-04-01, 2015-05-01, 2015-06-01, 2015-07-01, 2015-08-01, 2015-09-01, 2015-10-01, 2015-11-01, 2015-12-01, 2016-01-01, 2016-02-01, 2016-03-01, 2016-04-01, 2016-05-01, 2016-06-01, 2016-07-01, 2016-08-01, 2016-09-01, 2016-10-01, 2016-11-01, 2016-12-01, 2017-01-01, 2017-02-01, 2017-03-01, 2017-04-01, 2017-05-01, 2017-06-01, 2017-07-01, 2017-08-01, 2017-09-01, 2017-10-01, 2017-11-01, 2017-12-01, 2018-01-01, 2018-02-01, 2018-03-01, 2018-04-01, 2018-05-01, 2018-06-01, 2018-07-01, 2018-08-01, 2018-09-01, 2018-10-01, 2018-11-01, 2018-12-01, 2019-01-01, 2019-02-01, 2019-03-01, 2019-04-01, 2019-05-01, 2019-06-01, 2019-07-01, 2019-08-01, 2019-09-01, 2019-10-01, 2019-11-01, 2019-12-01, 2020-01-01, 2020-02-01, 2020-03-01, 2020-04-01, 2020-05-01, 2020-06-01, 2020-07-01, 2020-08-01, 2020-09-01, 2020-10-01, 2020-11-01, 2020-12-01, 2021-01-01, 2021-02-01, 2021-03-01, 2021-04-01, 2021-05-01, 2021-06-01, 2021-07-01, 2021-08-01, 2021-09-01, 2021-10-01, 2021-11-01, 2021-12-01, 2022-01-01, 2022-02-01, 2022-03-01, 2022-04-01, 2022-05-01, 2022-06-01, 2022-07-01, 2022-08-01, 2022-09-01, 2022-10-01, 2022-11-01, 2022-12-01, 2023-01-01, 2023-02-01, 2023-03-01, 2023-04-01, 2023-05-01, 2023-06-01, 2023-07-01, 2023-08-01, 2023-09-01, 2023-10-01, 2023-11-01, 2023-12-01]
```

## 方法总结

**LocalDate**

|方法|功能|
|---|---|
|`static LocalDate now()`|当前 `LocalDate`|
|`static LocalDate of(int year, int month, int dayOfMonth)`|使用指定年月日创建 LocalDatee|
|`static LocalDate of(int year, Month month, int dayOfMonth)`|
|`LocalDate plus(long amountToAdd, TemporalUnit unit)`|日期加法|
|`LocalDate plusDays(long daysToAdd)`|
|`LocalDate plusMonths(long monthsToAdd)`|
|`LocalDate plusWeeks(long weeksToAdd)`|
|`LocalDate plusYears(long yearsToAdd)`|