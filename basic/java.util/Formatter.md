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






## 细节





## 参考

- https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Formatter.html
