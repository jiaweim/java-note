# 正则表达式

2025-03-14 update ⭐
2021-03-13 create
@author Jiawei Mao

***

## 简介

正则表达式根据特定的规则描述字符串的特征，可用于检索、编辑和操作文本数据。

如果你觉得正则表达式不难，那么要么你是一个天才，要么你不是地球人。正则表达式的语言很令人头疼，即使对经常使用它的人来说也是如此。由于难于读写，容易出错，所以找一种工具对正则表达式进行测试是很必要的。推荐 [regex101](https://regex101.com/)，一个很便捷的在线 regex 测试工具。

在 Java 中正则表达式相关的功能在 `java.util.regex`
包中。`java.util.regex`主要包括三个类：`Pattern`, `Matcher`和
`PatternSyntaxException`。

| 对象                   | 功能                               |
|:-----------------------|:-----------------------------------|
| `Pattern`              | 编译后的正则表达式对象                |
| `Matcher`              | 解析正则表达式，对输入字符串执行匹配操作 |
| `PatternSyntaxException` | 表示正则表达式中存在语法错误           |

## 正则表达式语法

### 字符串常量

匹配字符串常量是正则表达式最基本的匹配模式。例如，正则表达式 "foo"，可以匹配字符串 "foo"：

![regex_literal](images/regex_literal.png)

| 正则表达式 | 匹配           |
| ---------- | -------------- |
| x          | 字符 x         |
| `\\`       | 反斜杠字符 `\` |
| `\0n`     | 八进制字符 `0n` (0 `<=` *n* `<=` 7) |
| `\0nn`     | 八进制字符 `0nn` (0 `<=` *n* `<=` 7) |
| `\0mnn`    | 八进制字符 `0mnn` (0 `<=` *m* `<=` 3, 0 `<=` *n* `<=` 7) |
| `\xhh`     | 十六进制字符 `0xhh`         |
| `\uhhhh`   | 十六进制字符 `0xhhhh`     |
| `\x{h...h}` | 十六进制字符 `0xh...h` ([`Character.MIN_CODE_POINT`](https://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#MIN_CODE_POINT) <= `0x`*h...h* <= [`Character.MAX_CODE_POINT`](https://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#MAX_CODE_POINT)) |
| `\t`          | 制表符 (`'\u0009'`)                            |
| `\n`          | 换行符 (`'\u000A'`)            |
| `\r`          | 回车符 (`'\u000D'`)            |
| `\f`          | 换页符 (`'\u000C'`)                     |
| `\a`          | 警报符 (`'\u0007'`)             |
| `\e`          | 转义符 (`'\u001B'`)                  |
| `\c`*x*       | 与 x 对应的控制符         |

> [!NOTE]
>
> `Character.MIN_CODE_POINT` 为 UNICODE 最小编码值，为 `U+0000`；
>
> `Character.MAX_CODE_POINT` 为 UNICDOE 最大编码值，为 `U+10FFFF`

### 字符类

| 正则表达式 | 匹配 |
| ---------- | ---- |
| `[abc]`         | `a`, `b`, 或 `c` (simple class)         |
| `[^abc]`        | `a`, `b`, `c` 以外的字符 (negation) |
| `[a-zA-Z]`      | `a` 到 `z` ，和 `A` 到 `Z` 任意字符 inclusive (range) |
| `[a-d[m-p]]`    | `a` 到 `d`, 或 `m` 到 `p`，等价于: `[a-dm-p]` (union) |
| `[a-z&&[def]]`  | `d`, `e`, 或 `f` (intersection)                          |
| `[a-z&&[^bc]]`  | `a` 到`z`, `b` 和 `c` 除外，等价于: `[ad-z]` (subtraction) |
| `[a-z&&[^m-p]]` | `a` 到 `z`,  `m` 到 `p` 除外，等价于: `[a-lq-z]`(subtraction) |

- 匹配单词 "bat", "cat", "rat"

```
[bcr]at
```

- 匹配元音字母

```
[aeuio]
```

- 匹配除 `bat`, `cat`, `rat` 以外的所有以 `at` 结尾的单词。

```
[^bcr]at
```

- 匹配 1 到 5 的数字

```
[1-5]
```

- 匹配 a 到 h 所有字母

```
[a-h]
```

- 不同的范围可以挨着，例如：匹配所有的大写字母和小写字母

```
[a-zA-Z]
```

- 多个字符类取并集，例如：匹配 0, 1, 2, 3, 4, 6, 7, 8 这些数字

```
[0-4[6-8]]
```

- 交集，例如： 匹配 3, 4, 5

```
[0-9&&[345]]
```

- 差集，例如：匹配0-9中除 3, 4, 5 以外的数字

```
[0-9[^345]]
```

### 预定义字符类

预定义字符类，便于使用：

| 正则表达式 | 匹配                                                         |
| ---------- | ------------------------------------------------------------ |
| `.`        | 任意字符（根据配置，可以选择是否匹配换行符）                 |
| `\d`       | 数字: `[0-9]`                                                |
| `\D`       | 非数字: `[^0-9]`                                             |
| `\h`       | 水平空白字符: `[ \t\xA0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]` |
| `\H`       | 非水平空白字符: `[^\h]`                                      |
| `\s`       | 空白字符: `[ \t\n\x0B\f\r]`                                  |
| `\S`       | 非空白字符: `[^\s]`                                          |
| `\v`       | 垂直空白字符: `[\n\x0B\f\r\x85\u2028\u2029]`                 |
| `\V`       | 非垂直空白字符: `[^\v]`                                      |
| `\w`       | 单词字符: `[a-zA-Z_0-9]`                                     |
| `\W`       | 非单词字符: `[^\w]`                                          |

### POSIX 字符类

> 只支持 US-ASCII

| 正则表达式   | 匹配                                                   |
| ------------ | ------------------------------------------------------ |
| `\p{Lower}`  | A lower-case alphabetic character: `[a-z]`             |
| `\p{Upper}`  | An upper-case alphabetic character:`[A-Z]`             |
| `\p{ASCII}`  | All ASCII:`[\x00-\x7F]`                                |
| `\p{Alpha}`  | An alphabetic character:`[\p{Lower}\p{Upper}]`         |
| `\p{Digit}`  | A decimal digit: `[0-9]`                               |
| `\p{Alnum}`  | An alphanumeric character:`[\p{Alpha}\p{Digit}]`       |
| `\p{Punct}`  | Punctuation: One of `!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~` |
| `\p{Graph}`  | A visible character: `[\p{Alnum}\p{Punct}]`            |
| `\p{Print}`  | A printable character: `[\p{Graph}\x20]`               |
| `\p{Blank}`  | A space or a tab: `[ \t]`                              |
| `\p{Cntrl}`  | A control character: `[\x00-\x1F\x7F]`                 |
| `\p{XDigit}` | A hexadecimal digit: `[0-9a-fA-F]`                     |
| `\p{Space}`  | A whitespace character: `[ \t\n\x0B\f\r]`              |

### java.lang.Character 类

| 正则表达式           | 匹配                                        |
| -------------------- | ------------------------------------------- |
| `\p{javaLowerCase}`  | 等价于 `java.lang.Character.isLowerCase()`  |
| `\p{javaUpperCase}`  | 等价于 `java.lang.Character.isUpperCase()`  |
| `\p{javaWhitespace}` | 等价于 `java.lang.Character.isWhitespace()` |
| `\p{javaMirrored}`   | 等价于 `java.lang.Character.isMirrored()`   |

### Unicode 相关类

| 正则表达式           | 匹配                                                         |
| -------------------- | ------------------------------------------------------------ |
| `\p{IsLatin}`        | A Latin script character ([script](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#usc)) |
| `\p{InGreek}`        | A character in the Greek block ([block](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#ubc)) |
| `\p{Lu}`             | An uppercase letter ([category](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#ucc)) |
| `\p{IsAlphabetic}`   | An alphabetic character ([binary property](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#ubpc)) |
| `\p{Sc}`             | A currency symbol                                            |
| `\P{InGreek}`        | Any character except one in the Greek block (negation)       |
| `[\p{L}&&[^\p{Lu}]]` | Any letter except an uppercase letter (subtraction)          |

### 边界匹配

用于指定在字符串中匹配的位置。

| 字符 | 描述                     |
| ---- | ------------------------ |
| ^    | 行开头                   |
| $    | 行结尾                   |
| \b   | 单词边界（开头和结尾）   |
| \B   | 非单词边界               |
| \A   | 输入开头                 |
| \G   | 上次匹配的结尾           |
| \Z   | 输入结尾，对应换行符前面 |
| \z   | 输入结尾                 |

### 数量词

数量词用于指定匹配出现的数目。如下表所示：

| Greedy  | Reluctant | Possessive | 说明           |
| ------- | --------- | ---------- | -------------- |
| X?      | X??       | X?+        | 匹配 0 或 1 次 |
| X*      | X*?       | X*+        | 匹配 0 或多次  |
| X+      | X+?       | X++        | 匹配至少 1 次  |
| X{n}    | X{n}?     | X{n}+      | 匹配 n 次      |
| X{n,}   | X{n, }?   | X{n, }+    | 匹配至少n次    |
| X{n, m} | X{n, m}?  | X{n, m}+   | 匹配 n 到 m 次 |

解释：

- Greedy, 匹配前读取整个字符串，如果匹配失败，减掉一个字符，再次匹配，重复进行直至匹配成功，或没有字符可减。
- Reluctant, 采用相反的路径，从字符串开头开始匹配，一次递增字符，最后尝试匹配整个字符串。
- Possessive, 直接匹配整个字符串，只尝试一次。

例：`Windows\d+`, 匹配 `Windows` 后面跟1个或多个数字。

例：`^\w+`, 匹配一行的第一个单词

例： `[abc]+`, 匹配 `a` 或 `b` 或 `c` 一次或多次

例：`(abc)+`, 匹配 `abc` 一次或多次

### 逻辑运算符

| 正则表达式 | 匹配         |
| ---------- | ------------ |
| *XY*       | X 后面跟着 Y |
| *X*`|`*Y*  | X 或 Y       |
| `(`*X*`)`  | X 作为捕获组 |

### 换行符

换行符为 1 到 2 个标识输入字符序列 line 结束的字符。包括：

- `\n`
- `\r\n`
- `\r`
- `\u0085`
- `\u2028`：行分隔符
- `\u2029`：段分隔符

如果启用 `UNIX_LINES` 模式，则只有 `\n` 为视为 line 结束。

正则表达式 `.` 匹配换行符以外的所有字符，如果启用 `DOTALL` 则匹配所有字符。

正则表达式 `^` 和 `$` 默认忽略换行符，匹配整个输入序列的开始和结尾。如果启用 `MULTILINE` 模式，`^` 匹配输入开头，以及每个换行符后面位置（序列结尾除外）；`$` 匹配每个换行符前面的位置，以及输入序列结尾。

https://symbl.cc/cn/unicode-table

## group

| 正则表达式               | 匹配                                                         |
| ------------------------ | ------------------------------------------------------------ |
| `(?<name>`*X*`)`         | *X*, 作为命名捕获组                                          |
| `(?:`*X*`)`              | *X*, 作为非捕获组                                            |
| `(?idmsuxU-idmsuxU) `    | 打开或关闭 match flags [i](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#CASE_INSENSITIVE) [d](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#UNIX_LINES) [m](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#MULTILINE) [s](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#DOTALL) [u](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#UNICODE_CASE) [x](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#COMMENTS) [U](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#UNICODE_CHARACTER_CLASS) |
| `(?idmsux-idmsux:`*X*`)` | *X*, 指定 flag 的非命名捕获组                                |
| `(?=`*X*`)`              | 0 宽前向匹配                                                 |
| `(?!`*X*`)`              | 0 宽前向不匹配                                               |
| `(?<=`*X*`)`             | 0 宽后向匹配                                                 |
| `(?<!`*X*`)`             | 0 宽后向不匹配                                               |
| `(?>`*X*`)`              | X 作为独立的非捕获组                                         |

捕获组，即将多个字符当作整体处理，用**括号**进行分组。

使用小括号指定一个捕获组后，匹配这个子表达式的文本可以在表达式或程序中进一步处理。

### group 编号

每个 group 默认有一个编号，从左到右，以左括号为标识，第一个出现的 group 编号为1，以此类推。

例，`((A)(B(C)))`包含 4 个 group，分别是：
1. `((A)(B(C)))`
2. `(A)`
3. `(B(C))`
4. `(C)`

`Matcher.groupCount` 方法可以查看 group 数。编号 0 表示整个正则表达式，不包含在 `groupCount` 中。

另外，以 `(?` 开头的非捕获组以及不捕获字符的 group 也不在计数内。

`Matcher` 中以 group 编号为参数的方法：

- [`public int start(int group)`](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html#start-int-): 返回上一个 match 指定 group 捕获的子序列的 start-index
- [`public int end (int group)`](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html#end-int-): 返回上一个 match 指定 group 捕获的子序列的 end-index
- [`public String group (int group)`](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html#group-int-): 返回上一个 match 指定 group 捕获的子序列

### 命名 group

可以为分组命名（group-name），然后通过 group-name 进行反向引用。group-name 命名规则如下，首个字符必须为字母：

- 大小字母 'A' 到 'Z'
- 小写字母 'a' 到 'z'
- 数字 '0' 到 '9' 

命名分组依然有编号。

group 捕获的为最近 match 的子序列。

格式，将 `\w+` 的 group-name 指定为 `<name>`

```
(?<name>\w+)
```

### 通过编号获取分组（Java）

通过 `Matcher` 的 `groupCount()` 方法可以获得分组个数（不包括 group 0）。

分组相关方法：

| 方法                 | 功能                                                    |
|:---------------------|:-------------------------------------------------------|
| int start()          | 返回上一个匹配的起始索引                                   |
| int start(int group) | 返回上一个匹配编号对应捕获组匹配到的子字符串在字符串中的起始索引 |
| int end()            | 返回上一个匹配的终止索引                                   |
| int end(int group)   | 返回编号对应捕获组匹配到的子字符串在字符串中的终止索引         |
| group(int group)     | 返回编号对应捕获组匹配到的字符串                            |

### 反向引用

| 正则表达式     | 匹配                        |
| -------------- | --------------------------- |
| `\n`           | 反向引用匹配的第 n 个 group |
| `\`*k*<*name*> | 引用命名捕获组 "name"       |

反向引用用于重复搜索前面某个 group 匹配到的文本。例如，`\1`表示分组 `1`匹配到的文本。格式：

```
\x
```

x 表示 group 索引。

例如：`(\d\d)`定义一个匹配两个数字的 group，在表达式后面可以用 `\1` 引用该 group。
因此，`(\d\d)\1`，匹配两个数字后，后面跟着继续匹配两个相同的数字，`1212` 能匹配上，`1234` 匹配不上。

例如：匹配重复单词

```
\b(\w+)\b\s+\1\b
```

- `\b(\w+)\b` 匹配一个单词，该 group 编号为 1
- `\s+` 匹配 1 或多个空白
- `\1`，匹配 group-1 匹配的内容

例如，匹配  "abba"：

```
(\w)(\w)\2\1
```

### 非捕获组

语法：`(?:)`

捕获分组，但是不分配编号。

### 零长度匹配（zero-length matches）

`a?` 和 `a*` 都可以匹配空字符串，匹配位置 start 和 end 都是 0。因为输入长度为零，这类匹配称为**零长度匹配**。零长度匹配的情形有多种：

- 输入字符串为空
- 输入字符串起始
- 输入字符串结尾
- 输入字符串中任意挨着的两个字符串之间

零长度匹配很好识别，因为其 start 和 end 索引相同。例如:

- 用不同量词的正则表达式匹配字符串 "a"

| 正则表达式 | 匹配项 | 起始  | 结尾  |
| ---------- | ------ | ----- | ----- |
| a?         | a      | 0     | 1     |
|            | ""     | **1** | **1** |
| a*         | a      | 0     | 1     |
|            | ""     | **1** | **1** |
| a+         | a      | 0     | 1     |

说明：上面三个正则表达式都可以匹配 "a"，但前面两个在 index 1 额外匹配空字符串。所以根据数量词的不同，字符最后的 "" 空白可能会，也可能不会触发匹配

- 匹配字符串 "aaaaa"

| 正则表达式 | 匹配项 | 起始  | 结尾  |
| ---------- | ------ | ----- | ----- |
| a?         | a      | 0     | 1     |
|            | a      | 1     | 2     |
|            | a      | 2     | 3     |
|            | a      | 3     | 4     |
|            | a      | 4     | 5     |
|            | ""     | **5** | **5** |
| a*         | aaaaa  | 0     | 5     |
|            | ""     | **5** | **5** |
| a+         | aaaaa  | 0     | 5     |

- 匹配字符串 "ababaaaab"

| 正则表达式 | 匹配项 | 起始  | 结尾  |
| ---------- | ------ | ----- | ----- |
| a?         | a      | 0     | 1     |
|            | a      | 1     | 2     |
|            | a      | 2     | 3     |
|            | a      | 3     | 4     |
|            | a      | 4     | 5     |
|            | ""     | **5** | **5** |
| a*         | aaaaa  | 0     | 5     |
|            | ""     | **5** | **5** |
| a+         | aaaaa  | 0     | 5     |

## 锚点和零宽断言

锚点（Anchors）和零宽断言（zero-width assertions）用于匹配文本中的**位置**，而非文本。

零宽断言：要求文本满足指定条件才继续匹配。

| 格式       | 说明                | 要点    |
| -------- | ----------------- | ----- |
| (?=X)    | 断言匹配位置的右侧满足表达式 X  | 右侧匹配  |
| (?!X)    | 断言匹配位置的右侧不满足表达式 X | 右侧不匹配 |
| (?<=X)   | 断言匹配位置的左侧满足表达式 X  | 左侧匹配  |
| `(?<!X)` | 断言匹配位置的左侧不满足表达式 X | 左侧不匹配 |

说明：
- 正预测，指要求满足指定条件；负预测表示要求不满足指定条件；
- 先行，表示向前搜索；后发，表示向后搜索；
- 零宽度，表示匹配的内容宽度为零，即不匹配任何内容，只是对匹配结果的一个筛选规则。

**(?=X)**

匹配位置右侧满足表达式 `X`。

例如 `foo(?=bar)` 能匹配 `foobar`，不能匹配 `foobaz`。

**(?!X)**

匹配位置右侧不满足表达式 `X`。

例如 `foo(?!bar)` 不匹配 `foobar`，但匹配 `foobaz`

**(?<=X)**

匹配位置左侧满足表达式 `X`。

例如 `(?<=foo)bar` 匹配 `foobar`，不匹配 `fuubar`。

**`(?<!X)`**

同上，但是取否。

## API

### Pattern 

`Pattern` 表示编译好的正则表达式。

对 `String` 类型的正则表达式，首先必须编译为 `Pattern`，然后使用 `Pattern` 创建 `Matcher` 对象使用正则表达式匹配任何字符序列。匹配过程中涉及的所有状态都保存在 `Matcher` 中。

典型用法：

```java
Pattern p = Pattern.compile("a*b");
Matcher m = p.matcher("aaaaab");
boolean b = m.matches();
```

`Pattern` 也定义了一个 `matches` 方法，将上面编译正则表达式、匹配输入序列简化为一次调用：

```java
boolean b = Pattern.matches("a*b", "aaaaab");
```

不够该方法不能重复使用编译好的 `Pattern`，所以性能较低。

`Pattern` 属于 immutable，线程安全，不过 `Matcher` 不是线程安全的。

#### flags

| Flag               | 等价表达式 | 功能                                                         |
| :----------------- | ---------- | :----------------------------------------------------------- |
| `CANON_EQ`         | None       | 启用规范等价。以 unicode 字符 é 为例，其 code 为 U00E9。unicode 同时包含字符 e U0065 和上标 U0301，启用规范等价后，U00E9 与 U0065U0301 匹配。启用该 flag 会影响性能，默认 false |
| `CASE_INSENSITIVE` | `(?i)`     | 不区分大小写。默认只支持 US-ASCII 字符。同时指定 `UNICODE_CASE` flag 使 Unicode 也不区分大小写。启用该 flag 会稍微降低性能 |
| `COMMENTS`         | `(?x)`     | 允许 pattern 包含空格和注释。在该模式，忽略空格，忽略以 # 开始到行末尾的注释 |
| `DOTALL`           | `(?s)`     | `.` 匹配所有字符，包括换行符                                 |
| `LITERAL`          | None       | 将 pattern 中的所有字符当作字面量处理。元字符和转义字符也没有特殊含义。`CASE_INSENSITIVE` 和 `UNICODE_CASE` 与该 flag 结合使用还用其它，其它 flag 全部失效 |
| `MULTILINE`        | `(?m)`     | 多行模式，`^` 和 `$` 也匹配每行的开头和结尾                  |
| `static int`       | `(?u)`     | `UNICODE_CASE`Enables Unicode-aware case folding.            |
| `static int`       | None       | `UNICODE_CHARACTER_CLASS`Enables the Unicode version of *Predefined character classes* and *POSIX character classes*. |
| `UNIX_LINES`       | `(?d)`     | 启用 Unix 换行模式。在该模式下，`.`, `^` 和 `$` 仅将 `\n` 视为换行符 |

- `CANON_EQ` 示例

```java
Pattern p1 = Pattern.compile("\u00E9");
Matcher m1 = p1.matcher("\u0065\u0301");
assertFalse(m1.matches());

Pattern p2 = Pattern.compile("\u00E9", Pattern.CANON_EQ);
Matcher m2 = p2.matcher("\u0065\u0301");
assertTrue(m2.matches());
```

- `CASE_INSENSITIVE`

```java
Pattern p1 = Pattern.compile("dog");
Matcher m1 = p1.matcher("This is a Dog");
assertFalse(m1.find());

Pattern p2 = Pattern.compile("dog", Pattern.CASE_INSENSITIVE);
Matcher m2 = p2.matcher("This is a Dog");
assertTrue(m2.find());

Pattern p3 = Pattern.compile("(?i)dog");
Matcher m3 = p3.matcher("This is a Dog");
assertTrue(m3.find());
```

- `COMMENTS`

该功能主要用于注释复杂的正则表达式。

```java
Pattern p1 = Pattern.compile("dog$  #check for word dog at end of text");
Matcher m1 = p1.matcher("This is a dog");
assertFalse(m1.find());

Pattern p2 = Pattern.compile("dog$  #check end of text", Pattern.COMMENTS);
Matcher m2 = p2.matcher("This is a dog");
assertTrue(m2.find());

Pattern p3 = Pattern.compile("(?x)dog$  #check end of text");
Matcher m3 = p3.matcher("This is a dog");
assertTrue(m3.find());
```

- `DOTALL`

```java
Pattern p1 = Pattern.compile("(.*)");
Matcher m1 = p1.matcher(
        "this is a text" + System.lineSeparator()
                + " continued on another line");
m1.find();
assertEquals("this is a text", m1.group(1));

Pattern p2 = Pattern.compile("(.*)", Pattern.DOTALL);
Matcher m2 = p2.matcher(
        "this is a text" + System.lineSeparator()
                + " continued on another line");
m2.find();
assertEquals(
        "this is a text" + System.lineSeparator()
                + " continued on another line", m2.group(1));

Pattern p3 = Pattern.compile("(?s)(.*)");
Matcher m3 = p3.matcher(
        "this is a text" + System.lineSeparator()
                + " continued on another line");
m3.find();
assertEquals(
        "this is a text" + System.lineSeparator()
                + " continued on another line", m3.group(1));
```

- `LITERAL`

```java
Pattern p1 = Pattern.compile("(.*)");
Matcher m1 = p1.matcher("text");
assertTrue(m1.find());

Pattern p2 = Pattern.compile("(.*)", Pattern.LITERAL);
Matcher m2 = p2.matcher("text");
assertFalse(m2.find());

Matcher m3 = p2.matcher("text(.*)");
assertTrue(m3.find());
assertEquals(4, m3.start());
assertEquals(8, m3.end());
assertEquals("(.*)", m3.group());
```

- `MULTILINE`

```java
Pattern p1 = Pattern.compile("dog$");
Matcher m1 = p1.matcher("This is a dog" + System.lineSeparator()
        + "this is a fox");
assertFalse(m1.find());

Pattern p2 = Pattern.compile("dog$", Pattern.MULTILINE);
Matcher m2 = p2.matcher("This is a dog" + System.lineSeparator()
        + "this is a fox");
assertTrue(m2.find());

Pattern p3 = Pattern.compile("(?m)dog$");
Matcher m3 = p3.matcher("This is a dog" + System.lineSeparator()
        + "this is a fox");
assertTrue(m3.find());
```

### Matcher

`Matcher` 根据 `Pattern` 匹配字符串。通过 `Pattern.matcher` 方法创建 `Matcher`，创建后，可以使用 matcher 执行三类操作：

- `matches` 方法将整个输入序列与 pattern 匹配
- `lookingAt` 从字符串开头与 pattern 匹配
- `find` 扫描字符串，找到与 pattern 匹配的子序列

这三个方法都返回 `boolean`，匹配成功返回 `true`，否则返回 `false`。匹配成功时，可以通过 `Matcher` 查询匹配结果。

region 边界与 pattern 交互的方式可以通过 `useAnchoringBounds ` 和 `useTransparentBounds` 修改。

`Matcher` 还提供了用新字符串替换匹配的子序列的方法。可以使用 `appendReplacement ` 和 `appendTail`  将将结果收集到已有 string-buffer，或者直接使用 `replaceAll` 替换所有匹配的子序列。

matcher 的状态包括：

- 最近成功匹配的 start-index 和 end-index
- 每个捕获组的 start-index 和 end-index
- 总的子序列数目

matcher 在成功匹配之前查询会抛出 `IllegalStateException`。每次成功匹配都会更新其状态。

matcher 还有隐式状态，包括输入的字符序列和 `append` 位置，`append` 位置初始为 0，由 `appendReplacement` 方法更新。

调用 `reset()` 方法，或输入新的序列会重置 matcher 状态。重置 matcher 会重置其状态，并将 `append` 位置归0。

#### matches

```java
public boolean matches()
```

将整个 region 与 pattern 匹配，即要求整个序列完全匹配。

```java
Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
Matcher m1 = pattern.matcher("goodbye 2019 and welcome 2020");
assertFalse(m1.matches());

Matcher m2 = pattern.matcher("2019");
assertTrue(m2.matches());
assertEquals(0, m2.start());
assertEquals("2019", m2.group());
assertEquals(4, m2.end());
assertTrue(m2.matches());
```

`matches()` 多次调用返回结果相同。

#### lookingAt

```java
public boolean lookingAt()
```

匹配字符串开头的片段。

```java
Pattern pattern = Pattern.compile("dog");
Matcher m1 = pattern.matcher("dogs are friendly");

assertTrue(m1.lookingAt());
assertFalse(m1.matches());

Matcher m2 = pattern.matcher("dog");
assertTrue(m2.matches());
```

#### find

```java
boolean find();
```

`find()` 方法查找下一个与 pattern 匹配的**子序列**。

该方法从 region 的开头开始；或者从上一次 `find()` 成功后且没有重置，第一个与 pattern 不匹配的字符开始。

匹配成功后，可以使用 `start`, `end`, `group` 方法查看匹配信息。例如：

```java
Pattern pattern = Pattern.compile("foo");
Matcher matcher = pattern.matcher("foofoo");

assertTrue(matcher.find()); // 匹配第一个 foo
assertEquals(0, matcher.start());
assertEquals(3, matcher.end());
assertEquals("foo", matcher.group());

assertTrue(matcher.find()); // 匹配第二个 foo
assertEquals(3, matcher.start());
assertEquals(6, matcher.end());
assertEquals("foo", matcher.group());

assertFalse(matcher.find()); // 没有匹配了
```

- 下面是 `find` 的另一个版本

```java
boolean find(int start);
```

`find(int start)` 重置 matcher，并从指定 `start` 开始查找匹配的子序列。



#### region

region 用于限制在输入字符串中匹配的区域。相关方法：

- `region(int start, int end)` 用于修改 region
- `regionStart()` 和 `regionEnd()` 用于查询 region
- 

```java
Matcher region(int start, int end);
int regionStart();
int regionEnd();

Matcher useAnchoringBounds(boolean b)；
Matcher useTransparentBounds(boolean b)；
```

示例：

```java
Pattern pattern = Pattern.compile("(Bio)");
Matcher matcher = pattern.matcher("BioForBio Bio for Bio Biology");

System.out.println("Before changing region, Groups found are:");
while (matcher.find()) {
    System.out.println(matcher.start() + "\t" + matcher.group());
}

System.out.println();
System.out.println("After changing region, Groups found are: ");
matcher.region(0, 10);
while (matcher.find()) {
    System.out.println(matcher.start() + "\t" + matcher.group());
}
```

```
Before changing region, Groups found are:
0	Bio
6	Bio
10	Bio
18	Bio
22	Bio

After changing region, Groups found are: 
0	Bio
6	Bio
```

#### replaceFirst & replaceAll

```java
public String replaceAll(String replacement);
public String replaceFirst(String replacement);
```

- replaceFirst 示例

```java
Pattern p1 = Pattern.compile("dog");
Matcher m1 = p1.matcher(
        "dogs are domestic animals, dogs are friendly");
String newStr = m1.replaceFirst("cat");

assertEquals("cats are domestic animals, dogs are friendly", newStr);
```

- `replaceAll` 示例

```java
Pattern p2 = Pattern.compile("dog");
Matcher m2 = p2.matcher(
        "dogs are domestic animals, dogs are friendly");
String newStr2 = m2.replaceAll("cat");

assertEquals("cats are domestic animals, cats are friendly", newStr2);
```



#### appendReplacement

```java
Matcher appendReplacement(StringBuffer sb, String replacement);
```

实现一个 non-terminal 的 append-and-replace 操作。

该方法执行如下操作：

1. 从输入序列 `append` 位置开始读取字符，然后将其添加到指定 `StringBuffer`，在上一个 match 的前一个字符位置停止 （`start()-1` ）
2. 将给定的替换字符串添加到 `StringBuffer`
3. 将 matcher 的 `append` 位置设置为上一个 match 最后一个字符的位置+1，即 `end()`

`appendReplacement` 一般在循环中与 `appendTail` 和 `find` 结合使用:

- `find` 负责查找 match 子序列
- `appendTail` 负责将最后一次 match 后余下序列添加到 `StringBuffer`

例如：

```java
Pattern p = Pattern.compile("cat");
Matcher m = p.matcher("one cat two cats in the yard");
StringBuffer sb = new StringBuffer();
while (m.find()) {
	m.appendReplacement(sb, "dog");
}
m.appendTail(sb);
System.out.println(sb.toString());
```

```
one dog two dogs in the yard
```

替换字符串可能包含对上一个 match 捕获的子序列的引用：每个 `${name}` 或 `$g` 被 `group(name)` 或 `group(g)` 依次替换：

- 对 `$g`，`$` 后的第一个数字总是视为 group 引用的一部分，随后的数字如果能够与 g 组成正确的 group 引用，就加进去。group 引用只支持数字 '0' 到 '9'。
- 例如，如果 group-2 匹配的字符串 "foo"，那么替换字符串 "$2bar" 将导致 "foobar" 添加到 `StringBuffer`
- 通过转义 `\$` 可以将 `$` 作为字面量添加到替换字符串中

替换字符串中的 `\` 和 `$` 作为字面量可能导致结果不同。`$`  可能被视为对上一个 match 的捕获子序列的引用，`\` 可能被视为转义。

#### 查询

- 上一次匹配最后一个字符的 index

```java
public int end()
```

- 

```java
public int end(int group)
```



group-0 表示整个正则表达式，因此 `m.end(0)` 等价于 `m.end()`。

- 上一次 match 的 start-index

```java
public int start()
```

- 返回上一次 match 与 pattern 匹配的子序列

```java
public String group()
```

对 `Matcher` `m` 和输入序列 `s`，表达式 `m.group()` 与 `s.substring(m.start, m.end())` 等价。

对有些 pattern，如 `a*`，匹配空字符串。当 pattern 成功与空字符串匹配，`group()` 返回空字符串。

## 参考

- <https://docs.oracle.com/javase/tutorial/essential/regex/index.html>
- https://deerchao.cn/tutorials/regex/regex.htm
- 正则表达式测试网页工具 [regex101](https://regex101.com/)，**强烈推荐**

