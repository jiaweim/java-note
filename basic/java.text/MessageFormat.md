# MessageFormat

## 简介

`MessageFormat` 提供一种与语言无关的方式来生成拼接消息。可使用该类构建展示给终端用户的消息。

`MessageFormat` 接收一组对象，对其进行格式化，然后将格式化后的字符串插入到模式的对应位置。

> [!NOTE]
>
> `MessageFormat` 与其它 `Format` 类不同，需要通过构造函数创建，而非 `getInstance` 风格的工厂方法。
>
> 由于 `MessageFormat` 不实现与区域设置相关的行为，因此无需工厂方法。所有区域相关设置，由你提供的模式以及用于插入参数的子格式定义。

## 模式和含义

`MessageFormat` 使用如下形式的模式：

```
 MessageFormatPattern:
         String
         MessageFormatPattern FormatElement String

 FormatElement:
         { ArgumentIndex }
         { ArgumentIndex , FormatType }
         { ArgumentIndex , FormatType , FormatStyle }

 FormatType:
         number
         dtf_date
         dtf_time
         dtf_datetime
         pre-defined DateTimeFormatter(s)
         date
         time
         choice
         list

 FormatStyle:
         short
         medium
         long
         full
         integer
         currency
         percent
         compact_short
         compact_long
         or
         unit
         SubformatPattern
```

**ArgumentIndex**

`ArgumentIndex` 是一个使用数字 0 到 9 表示的非负整数，代表传入 `format` 方法的参数 `arguments` 数组中的索引。



在 `String` 中，用一对单引号引用任意字符（单引号自身除外）。例如，模式字符串 `"'{0}'"` 表示字面量 `{0}`，而不是一个格式化元素（FormatElement）。

`String` 中单引号自身需要使用两个连续的单引号 `''` 来表示。例如，模式字符串 `"'{''}'"` 被解析为：左大括号 `{`、单引号和有大括号 `}`，最终为 `{'}`。

`SubformatPattern` 

> [!NOTE]
>
> 在参考实现中，参数索引 `ArgumentIndex` 的上限为 10000.

## 格式

创建子格式方法：

- **FormatType**: (none), **FormatStyle**: (none)

```java
null
```

- **number**

| FormatType         | Subformat Created                                            |
| ------------------ | ------------------------------------------------------------ |
| *(none)*           | [`NumberFormat.getInstance`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.html#getInstance(java.util.Locale))`(getLocale())` |
| `integer`          | [`NumberFormat.getIntegerInstance`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.html#getIntegerInstance(java.util.Locale))`(getLocale())` |
| `currency`         | [`NumberFormat.getCurrencyInstance`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.html#getCurrencyInstance(java.util.Locale))`(getLocale())` |
| `percent`          | [`NumberFormat.getPercentInstance`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.html#getPercentInstance(java.util.Locale))`(getLocale())` |
| `compact_short`    | [`NumberFormat.getCompactNumberInstance`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.html#getCompactNumberInstance(java.util.Locale,java.text.NumberFormat.Style))`(getLocale(),` [`NumberFormat.Style.SHORT`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.Style.html#SHORT)) |
| `compact_long`     | [`NumberFormat.getCompactNumberInstance`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.html#getCompactNumberInstance(java.util.Locale,java.text.NumberFormat.Style))`(getLocale(),` [`NumberFormat.Style.LONG`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/NumberFormat.Style.html#LONG)) |
| *SubformatPattern* | `new` [`DecimalFormat`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/DecimalFormat.html#(java.lang.String,java.text.DecimalFormatSymbols))`(subformatPattern,` [`DecimalFormatSymbols.getInstance`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/text/DecimalFormatSymbols.html#getInstance(java.util.Locale))`(getLocale()))` |

- dtf_date

| *(none)*           | [`DateTimeFormatter.ofLocalizedDate(`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofLocalizedDate(java.time.format.FormatStyle))[`FormatStyle.MEDIUM`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/FormatStyle.html#MEDIUM)`).withLocale(getLocale())` |
| ------------------ | ------------------------------------------------------------ |
| `short`            | [`DateTimeFormatter.ofLocalizedDate(`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofLocalizedDate(java.time.format.FormatStyle))[`FormatStyle.SHORT`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/FormatStyle.html#SHORT)`).withLocale(getLocale())` |
| `medium`           | [`DateTimeFormatter.ofLocalizedDate(`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofLocalizedDate(java.time.format.FormatStyle))[`FormatStyle.MEDIUM`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/FormatStyle.html#MEDIUM)`).withLocale(getLocale())` |
| `long`             | [`DateTimeFormatter.ofLocalizedDate(`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofLocalizedDate(java.time.format.FormatStyle))[`FormatStyle.LONG`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/FormatStyle.html#LONG)`).withLocale(getLocale())` |
| `full`             | [`DateTimeFormatter.ofLocalizedDate(`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofLocalizedDate(java.time.format.FormatStyle))[`FormatStyle.FULL`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/FormatStyle.html#FULL)`).withLocale(getLocale())` |
| *SubformatPattern* | [`DateTimeFormatter.ofPattern`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofPattern(java.lang.String,java.util.Locale))`(subformatPattern, getLocale())` |

## 使用

下面演示 `MessageFormat` 的基本用法。在国际化程序中，消息格式模式以及其他静态字符串可以从 resource-bundle 获取。

```java
int planet = 7;
String event = "a disturbance in the Force";
String result = MessageFormat.format(
    "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
    planet, new GregorianCalendar(2053, Calendar.JULY, 3, 12, 30).getTime(), event);
```

```
At 12:30:00 PM on Jul 3, 2053, there was a disturbance in the Force on planet 7.
```



## 同步

`MessageFormat` 不是线程安全的，建议为每个线程单独创建实例。

如果多个线程并发访问某个实例，则必须在外部对其进行同步处理。