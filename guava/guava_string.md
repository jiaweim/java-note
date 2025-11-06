# String 工具

2025-11-06⭐
@author Jiawei Mao
***

## Joiner

使用分隔符连接字符串可能很麻烦。如果序列包含 null 值，则更麻烦。fluenty-style 的 `Joiner` 使其变得简单。

```java
Joiner joiner = Joiner.on("; ").skipNulls();
return joiner.join("Harry", null, "Ron", "Hermione");
```

返回 "Harry; Ron; Hermione"。除了使用 `skipNulls`，还可以使用 `useForNull(String)` 指定替换 null 的字符串。

也可以在 object 上使用 `Joiner`，会自动调用 `toString()` 转换后连接：

```java
Joiner.on(",").join(Arrays.asList(1, 5, 7)); // returns "1,5,7"
```

> [!WARNING]
>
> `Joiner` 是 immutable，其设置方法总是返回一个新的 `Joiner`。因为 `Joiner` 是线程安全的，可以作为 `static final` 常量使用。

## Splitter

Java 内置的拆分字符串工具行为有点奇怪。例如，`String.split` 默认丢弃末尾分隔符，`StringTokenizer` 只支持 5 种空格字符串。

例如，`",a,,b,".split(",")` 得到什么？

1. `"", "a", "", "b", ""`
2. `null, "a", null, "b", null`
3. `"a", null, "b"`
4. `"a", "b"`
5. None of the above

正确答案是 `"", "a", "", "b"`，丢弃末尾的空字符串。

`Splitter` 通过 fluent 模式提供对这些行为的完全控制。

```java
Splitter.on(',')
    .trimResults()
    .omitEmptyStrings()
    .split("foo,bar,,   qux");
```

返回的 `Iterable<String>` 包含 "foo", "bar", "qux"。`Splitter` 可以设置一任何 `Pattern`, `char` 或 `CharMatcher` 作为分隔。

### 工厂方法

| 方法                                                         | 说明                                                         | 示例                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`Splitter.on(char)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#on-char-) | 根据指定单个字符拆分                                         | `Splitter.on(';')`                                           |
| [`Splitter.on(CharMatcher)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#on-com.google.common.base.CharMatcher-) | 根据某个类别中任何字符进行拆分                               | `Splitter.on(CharMatcher.BREAKING_WHITESPACE)` `Splitter.on(CharMatcher.anyOf(";,."))` |
| [`Splitter.on(String)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#on-java.lang.String-) | 根据 `String` 字面量拆分                                     | `Splitter.on(", ")`                                          |
| [`Splitter.on(Pattern)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#on-java.util.regex.Pattern-) [`Splitter.onPattern(String)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#onPattern-java.lang.String-) | 根据正则表达式拆分                                           | `Splitter.onPattern("\r?\n")`                                |
| [`Splitter.fixedLength(int)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#fixedLength-int-) | 拆分为固定长度的子串，最后一个长度可能小于 `length`，但不会为空 | `Splitter.fixedLength(3)`                                    |

### 配置

| 方法                                                         | 说明                                                         | 示例                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`omitEmptyStrings()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#omitEmptyStrings--) | 省略空字符串                                                 | `Splitter.on(',').omitEmptyStrings().split("a,,c,d")` 返回 `"a", "c", "d"` |
| [`trimResults()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#trimResults--) | 从结果中去掉空格，等价于`trimResults(CharMatcher.WHITESPACE)` | `Splitter.on(',').trimResults().split("a, b, c, d")` 返回 `"a", "b", "c", "d"` |
| [`trimResults(CharMatcher)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#trimResults-com.google.common.base.CharMatcher-) | 从结果中提出匹配指定 `CharMatcher` 的字符                    | `Splitter.on(',').trimResults(CharMatcher.is('_')).split("_a ,_b_ ,c__")` returns `"a ", "b_ ", "c"`. |
| [`limit(int)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#limit-int-) | 返回指定数量的字符串后停止拆分                               | `Splitter.on(',').limit(3).split("a,b,c,d")` returns `"a", "b", "c,d"` |

如果希望得到一个 `List`，则使用 `splitToList()` 替代 `split()`。

> [!WARNING]
>
> `Splitter` 实例为 immutable，以上配置方法均返回新的 `Splitter`，因此 `Splitter` 线程安全，可以作为 `static final` 常量使用

### Map Splitters

可以使用 [`withKeyValueSeparator()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/Splitter.html#withKeyValueSeparator-java.lang.String-) 指定第二个分隔符来去序列化 map。得到的 `MapSplitter` 会使用分隔符将输入拆分为 entries，然后根据指定的 key-value 分隔符将 entry 拆分为 key 和 value，得到 `Map<String, String>`。

## CharMatcher

可以将 `CharMatcher` 识别特定类别的字符，如数字、空格。实际上，`CharMatcher` 就是字符串的 `Predicate`，它实现了 `Predicate<Character>` 接口。

通过 `CharMatcher` 可以对指定类别的字符串执行：修剪、折叠、删除、保留等操作。`CharMatcher` 表示：

1. 匹配字符有哪些
2. 对匹配的字符执行什么操作

```java
String noControl = CharMatcher.javaIsoControl().removeFrom(string); // remove control characters
String theDigits = CharMatcher.digit().retainFrom(string); // only the digits
String spaced = CharMatcher.whitespace().trimAndCollapseFrom(string, ' ');
  // trim whitespace at ends, and replace/collapse whitespace into single spaces
String noDigits = CharMatcher.javaDigit().replaceFrom(string, "*"); // star out all digits
String lowerAndDigit = CharMatcher.javaDigit().or(CharMatcher.javaLowerCase()).retainFrom(string);
  // eliminate all characters that aren't digits or lowercase
```

> [!NOTE]
>
> `CharMatcher` 只处理 `char` 值，不支持 0x10000 到 0x10FFFF 的 Unicode 补充。

### 获得 CharMatcher

`CharMatcher` 的工厂方法可以满足大多需求：

- [`any()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#any--)
- [`none()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#none--)
- [`whitespace()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#whitespace--)
- [`breakingWhitespace()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#breakingWhitespace--)
- [`invisible()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#invisible--)
- [`digit()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#digit--)
- [`javaLetter()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#javaLetter--)
- [`javaDigit()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#javaDigit--)
- [`javaLetterOrDigit()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#javaLetterOrDigit--)
- [`javaIsoControl()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#javaIsoControl--)
- [`javaLowerCase()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#javaLowerCase--)
- [`javaUpperCase()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#javaUpperCase--)
- [`ascii()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#ascii--)
- [`singleWidth()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#singleWidth--)

其它方法还有：

| 方法                                                         | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`anyOf(CharSequence)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#anyOf-java.lang.CharSequence-) | 指定所有你想匹配的字符。例如，`CharMatcher.anyOf("aeiou")` 匹配小写英语元音 |
| [`is(char)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#is-char-) | 只指定一个要匹配的字符                                       |
| [`inRange(char, char)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#inRange-char-char-) | 指定要匹配的字符范围，例如 `CharMatcher.inRange('a', 'z')`   |

此外，`CharMatcher` 还提供 `negate()`, `add(CharMatcher)` 和 `or(CharMatcher)` 用于 `CharMatcher` 的逻辑运算。

### 使用 CharMatcher

`CharMatcher` 提供了许多方法，对任何 `CharSequence` 中指定字符的出现进行操作。下面是一些常用的：

| Method                                                       | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`collapseFrom(CharSequence, char)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#collapseFrom-java.lang.CharSequence-char-) | 将每组连续出现的字符替换为指定字符。例如 `WHITESPACE.collapseFrom(string, ' ')` 将多个字符折叠为一个空格 |
| [`matchesAllOf(CharSequence)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#matchesAllOf-java.lang.CharSequence-) | 测试该 matcher 是否与指定 `CharSequence` 的所有字符匹配。例如， `ASCII.matchesAllOf(string)` 测试字符串中的所有字符是否都是 ASCII |
| [`removeFrom(CharSequence)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#removeFrom-java.lang.CharSequence-) | 从序列中删除匹配的字符                                       |
| [`retainFrom(CharSequence)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#retainFrom-java.lang.CharSequence-) | 从序列中删除不匹配的字符                                     |
| [`trimFrom(CharSequence)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#trimFrom-java.lang.CharSequence-) | 删除开头和结尾匹配的字符                                     |
| [`replaceFrom(CharSequence, CharSequence)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CharMatcher.html#replaceFrom-java.lang.CharSequence-java.lang.CharSequence-) | 将匹配的字符替换为指定序列                                   |

> [!NOTE]
>
> 除了 `matchesAllOf`，所有方法都返回 `String`。

## Charsets

`Charsets` 已过时，推荐 `StandardCharsets` 中的常量。使用它可以将代码：

```java
try {
  bytes = string.getBytes("UTF-8");
} catch (UnsupportedEncodingException e) {
  // how can this possibly happen?
  throw new AssertionError(e);
}
```

修改为：

```java
bytes = string.getBytes(UTF_8);
```

## CaseFormat

`CaseFormat` 用于 ASCII 大小写约定的转换。支持的格式包括：

| Format                                                       | Example            |
| ------------------------------------------------------------ | ------------------ |
| [`LOWER_CAMEL`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CaseFormat.html#LOWER_CAMEL) | `lowerCamel`       |
| [`LOWER_HYPHEN`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CaseFormat.html#LOWER_HYPHEN) | `lower-hyphen`     |
| [`LOWER_UNDERSCORE`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CaseFormat.html#LOWER_UNDERSCORE) | `lower_underscore` |
| [`UPPER_CAMEL`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CaseFormat.html#UPPER_CAMEL) | `UpperCamel`       |
| [`UPPER_UNDERSCORE`](https://guava.dev/releases/snapshot/api/docs/com/google/common/base/CaseFormat.html#UPPER_UNDERSCORE) | `UPPER_UNDERSCORE` |

使用起来也简单：

```java
CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "CONSTANT_NAME")); // returns "constantName"
```

该工具在生成其它程序代码的很有用。

## Strings

少量通用 `String` 工具在 `Strings` 类中。

## 参考

- https://github.com/google/guava/wiki/StringsExplained