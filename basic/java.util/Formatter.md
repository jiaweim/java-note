# Formetter

## 简介

`java.util.Formatter` 是 printf 风格格式字符串的解释器。该类支持对齐，支持数字、字符串和日期-时间数据的常用格式，以及特定地区的格式。支持常见 Java 类型，如 byte, `BigDecimal`, `Calendar` 等。

`Formatter` 非线程安全。

示例：

```java
StringBuilder sb = new StringBuilder();
// 输出到 sb
Formatter formatter = new Formatter(sb, Locale.US);

// 显式指定参数顺序
formatter.format("%4$2s %3$2s %2$2s %1$2s", "a", "b", "c", "d");
// " d  c  b  a"

// 可选 locale 作为第一个参数用来获得特定地区的数字格式
formatter.format(Locale.FRANCE, "e = %+10.4f", Math.E);
```

常见格式请求的便捷方法：

```java
  // 格式化输出到 System.out.
  System.out.format("Local time: %tT", Calendar.getInstance());
  // -> "Local time: 13:34:18"

  // 格式化输出到 System.err.
  System.err.printf("Unable to open file '%1$s': %2$s",
                    fileName, exception.getMessage());
  // -> "Unable to open file 'food': No such file or directory"
```

也可以使用 `String.format` 生成格式化字符串：

```java
  // Format a string containing a date.
  import java.util.Calendar;
  import java.util.GregorianCalendar;
  import static java.util.Calendar.*;

  Calendar c = new GregorianCalendar(1995, MAY, 23);
  String s = String.format("Duke's Birthday: %1$tb %1$te, %1$tY", c);
  // -> s == "Duke's Birthday: May 23, 1995"
```

## 概念

下面介绍格式化的基本概念。

### 格式化语法

每个方法至少需要两个参数：格式字符串和参数列表。

格式化字符串包含固定文本和一个或多个嵌入格式指定符的 `String`。例如：

```java
Calendar c = ...;
String s = String.format("Duke's Birthday: %1$tm %1$te,%1$tY", c);
```

`format` 方法的第一个参数就是格式字符串。其中包含 3 个格式指定符："`%1$tm`", "`%1$te`", "`%1$tY`"，用于指定如何处理参数，以及插入文本的位置。格式字符串其余部分为固定文本，保持不变。

参数列表包含格式字符串后传递给方法的所有参数。在上述示例中，参数列表长度为 1，包含一个 `Calendar` 对象 `c`。

**通用、字符和数字的格式说明符语法**

```java
%[argument_index$][flags][width][.precision]conversion
```

- `argument_index` (可选) - 一个十进制整数，指定参数在参数列表中的位置，第一个参数用 `"1$"` 引用，第二个参数用 `"2$"` 引用，以此类推
- `flags` (可选) - 一组用来修改输出格式的字符。有效的 flags 取决于后面的 `conversion`
- `width` (可选) - 宽度，十进制正整数，表示输出的**最小字符数**
- `precision` (可选) - 非负十进制整数，通常用于限制字符串，具体功能取决于 `conversion`
- `conversion` - 指定如何格式化参数的**单个字符**。给定参数的有效 `conversion` 取决于参数类型

**日期和时间的格式说明符语法**

```java
 %[argument_index$][flags][width]conversion
```

- `argument_index`, `flags` 和 `width`  的定义同上
- `conversion` - 两个字符串，第一个为 `t` 或 `T`，第二个字符指定要使用的格式。格式字符与 GNU `date` 和 POSIX `strftime` (3c) 定义的字符串相似，但不完全相同。

**与参数不对应的格式说明符语法**

```java
%[flags][width]conversion
```

- `flags` 和 `width` 含义同上
- `conversion` - 指定要插入的内容

### Conversion

| Conversion 类型      | 说明                                                                                                                                                                                |
| ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **General**        | 可应用于任何参数类型                                                                                                                                                                        |
| **Character**      | 可应用于 Unicode 字符的基本类型：`char`, `Character`, `byte`, `Byte`, `short`, `Short`。也可用于 `Character.isValidCodePoint(int)` 为 `true` 的 `int` 和 `Integer` 类型                                 |
| **Numeric**        | Java 整数类型：`byte`, `Byte`, `short`, `Short`, `int`, `Integer`, `long`, `Long`, `BigInteger` （不包含 `char` 或 `Character`）<br />浮点数：`float`, `Float`, `double`, `Double`, `BigDecimal` |
| **Date/Time**      | 应用于能够编码 date 或 time 的 Java 类型：`long`, `Long`, `Calendar`, `Date`, `TimporalAccessor`                                                                                              |
| **Percent**        | 生成一个字面量 `'%'` (`\u0025`)                                                                                                                                                          |
| **Line Separator** | 生成一个特定于平台的换行符                                                                                                                                                                     |

对 General,  Character, Numeric 和 Date/Time conversion，除非另有说明，否则当参数 `arg` 为 `null`，结果为 `null`。

下表总结了支持的 conversions。大写字母（如 `'B'`, `'H'`, `'S'`, `'C'`, `'X'`, `'E'`, `'G'`, `'A'`, and `'T'`）和对应的小写字母功能相同，只是根据 `Locale` 规则将结果转换为大写。如果没有明确指定 locale，则使用默认 locale。

| Conversion | 分类           | 说明                                                         |
| ---------- | -------------- | ------------------------------------------------------------ |
| 'b', 'B'   | general        | 如果 `arg` 为 `null`，则结果为 `"false"`。如果 `arg` 为 `boolean` 或 `Boolean`，则结果为 `String.valueOf(arg)` 返回的字符串。否则，结果为 "true" |
| 'h', 'H'   | general        | `Integer.toHexString(arg.hashCode())`，即哈希值              |
| 's', 'S'   | general        | 如果 `arg` 实现 `Formattable`，则调用 `arg.formatTo`。否则调用 `arg.toString()` |
| 'c', 'C'   | character      | 一个 Unicode 字符                                            |
| 'd'        | integral       | 十进制整数                                                   |
| 'o'        | integral       | 格式化为八进制整数                                           |
| 'x', 'X'   | integral       | 格式化为十六进制整数                                         |
| 'e', 'E'   | floating point | 科学计数法表示的十进制数                                     |
| 'f'        | floating point | 十进制浮点数                                                 |
| 'g', 'G'   | floating point | 科学计数法和十进制格式，具体取决于精度和四舍五入后的值（`f` 和 `e` 中较短的） |
| 'a', 'A'   | floating point | 带尾数和指数的十六进制浮点数。不支持 `BigDecimal`            |
| 't', 'T'   | date/time      | data/time conversion 的前缀                                  |
| '%'        | percent        | 结果为字面量 '%' ('\u0025')                                  |
| 'n'        | line separator | 特定于平台的换行符                                           |

### Date/Time Conversions

以下日期和时间 conversion 后缀字符用于 't' 和 'T' conversions。

**以下 conversion 字符用于格式化时间**

| Conversion | Description                                                  |
| ---------- | ------------------------------------------------------------ |
| `'H'`      | Hour of the day for the 24-hour clock, formatted as two digits with a leading zero as necessary i.e. `00 - 23`. |
| `'I'`      | Hour for the 12-hour clock, formatted as two digits with a leading zero as necessary, i.e. `01 - 12`. |
| `'k'`      | Hour of the day for the 24-hour clock, i.e. `0 - 23`.        |
| `'l'`      | Hour for the 12-hour clock, i.e. `1 - 12`.                   |
| `'M'`      | Minute within the hour formatted as two digits with a leading zero as necessary, i.e. `00 - 59`. |
| `'S'`      | Seconds within the minute, formatted as two digits with a leading zero as necessary, i.e. `00 - 60` ("`60`" is a special value required to support leap seconds). |
| `'L'`      | Millisecond within the second formatted as three digits with leading zeros as necessary, i.e. `000 - 999`. |
| `'N'`      | Nanosecond within the second, formatted as nine digits with leading zeros as necessary, i.e. `000000000 - 999999999`. |
| `'p'`      | Locale-specific [morning or afternoon](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/DateFormatSymbols.html#getAmPmStrings()) marker in lower case, e.g."`am`" or "`pm`". Use of the conversion prefix `'T'` forces this output to upper case. |
| `'z'`      | [RFC 822](https://www.ietf.org/rfc/rfc822.txt) style numeric time zone offset from GMT, e.g. `-0800`. This value will be adjusted as necessary for Daylight Saving Time. For `long`, [`Long`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Long.html), and [`Date`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Date.html) the time zone used is the [default time zone](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/TimeZone.html#getDefault()) for this instance of the Java virtual machine. |
| `'Z'`      | A string representing the abbreviation for the time zone. This value will be adjusted as necessary for Daylight Saving Time. For `long`, [`Long`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Long.html), and [`Date`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Date.html) the time zone used is the [default time zone](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/TimeZone.html#getDefault()) for this instance of the Java virtual machine. The Formatter's locale will supersede the locale of the argument (if any). |
| `'s'`      | Seconds since the beginning of the epoch starting at 1 January 1970 `00:00:00` UTC, i.e. `Long.MIN_VALUE/1000` to `Long.MAX_VALUE/1000`. |
| `'Q'`      | Milliseconds since the beginning of the epoch starting at 1 January 1970 `00:00:00` UTC, i.e. `Long.MIN_VALUE` to `Long.MAX_VALUE`. |

**格式化日期的 conversion 字符**

| Conversion | Description                                                  |
| ---------- | ------------------------------------------------------------ |
| `'B'`      | Locale-specific [full month name](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/DateFormatSymbols.html#getMonths()), e.g. `"January"`, `"February"`. |
| `'b'`      | Locale-specific [abbreviated month name](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/DateFormatSymbols.html#getShortMonths()), e.g. `"Jan"`, `"Feb"`. |
| `'h'`      | Same as `'b'`.                                               |
| `'A'`      | Locale-specific full name of the [day of the week](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/DateFormatSymbols.html#getWeekdays()), e.g. `"Sunday"`, `"Monday"` |
| `'a'`      | Locale-specific short name of the [day of the week](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/DateFormatSymbols.html#getShortWeekdays()), e.g. `"Sun"`, `"Mon"` |
| `'C'`      | Four-digit year divided by `100`, formatted as two digits with leading zero as necessary, i.e. `00 - 99` |
| `'Y'`      | Year, formatted as at least four digits with leading zeros as necessary, e.g. `0092` equals `92` CE for the Gregorian calendar. |
| `'y'`      | Last two digits of the year, formatted with leading zeros as necessary, i.e. `00 - 99`. |
| `'j'`      | Day of year, formatted as three digits with leading zeros as necessary, e.g. `001 - 366` for the Gregorian calendar. |
| `'m'`      | Month, formatted as two digits with leading zeros as necessary, i.e. `01 - 13`. |
| `'d'`      | Day of month, formatted as two digits with leading zeros as necessary, i.e. `01 - 31` |
| `'e'`      | Day of month, formatted as two digits, i.e. `1 - 31`.        |

**格式化 data/time 组合的 conversion 字符**

| Conversion | Description                                                  |
| ---------- | ------------------------------------------------------------ |
| `'R'`      | Time formatted for the 24-hour clock as `"%tH:%tM"`          |
| `'T'`      | Time formatted for the 24-hour clock as `"%tH:%tM:%tS"`.     |
| `'r'`      | Time formatted for the 12-hour clock as `"%tI:%tM:%tS %Tp"`. The location of the morning or afternoon marker (`'%Tp'`) may be locale-dependent. |
| `'D'`      | Date formatted as `"%tm/%td/%ty"`.                           |
| `'F'`      | [ISO 8601](http://www.w3.org/TR/NOTE-datetime) complete date formatted as `"%tY-%tm-%td"`. |
| `'c'`      | Date and time formatted as `"%ta %tb %td %tT %tZ %tY"`, e.g. `"Sun Jul 20 16:17:00 EDT 1969"`. |

### Flags

下表为支持的 flags。`y` 表示支持对应参数类型

| Flag | General | Character | Integral | Floating Point | Date/Time | Description                                                  |
| ---- | ------- | --------- | -------- | -------------- | --------- | ------------------------------------------------------------ |
| '-'  | y       | y         | y        | y              | y         | The result will be left-justified.                           |
| '#'  | y1      | -         | y3       | y              | -         | The result should use a conversion-dependent alternate form  |
| '+'  | -       | -         | y4       | y              | -         | The result will always include a sign                        |
| ' '  | -       | -         | y4       | y              | -         | The result will include a leading space for positive values  |
| '0'  | -       | -         | y        | y              | -         | The result will be zero-padded                               |
| ','  | -       | -         | y2       | y5             | -         | The result will include locale-specific [grouping separators](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/DecimalFormatSymbols.html#getGroupingSeparator()) |
| '('  | -       | -         | y4       | y5             | -         | The result will enclose negative numbers in parentheses      |

1. 取决于 `Formattable` 的定义
2. 只适用于 'd' conversion
3. 只适用于 'o', 'x' 和 'X'
4. 

## 细节





## 参考

- https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Formatter.html
