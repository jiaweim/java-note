# Apache Commons CSV

2024-03-05
根据 commons-csv API 文档更新笔记。
PS：commons-csv GUIDE 写得不好。
2024-01-16⭐
@author Jiawei Mao

***
## 1. 简介

### Maven 依赖

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-csv</artifactId>
    <version>1.10.0</version>
</dependency>
```

### CSV 格式

CSV 格式每行一条记录（record），每条记录由多个值组成。以下是 Commons CSV 特点：

|特点|说明|
|---|---|
|Separators (行)|record 分隔符是硬编码的，不能修改，必须是 '\r', '\n' 或 '\r\n'|
|Delimiter (值)|值分隔符可以设置，默认为 ','|
|Comments|有些 CSV 支持简单的注释语言。注释是以指定字符开头的 record。这类 record 被视为注释，在解析 CSV 时跳过|
|Encapsulator（引号）|封装字符用于将复杂的值括起来|
|Simple values|简单值由 delimiter 以外的字符组成，知道下一个 delimiter 或 Separator 结束。简单值周围的空格可以被忽略（默认忽略）|
|Complex values|复杂值被封装在一对预定义的引号中。在复杂值内部使用引号，需要进行转义。复杂值内部保留各种格式，包括换行符|
|Empty line skipping|可以跳过 CSV 文件中的空行。否则，返回一个包含三个空值的 record|

解析 CSV 示例：

```java
Reader in = new StringReader("a,b,c");
for (CSVRecord record : CSVFormat.DEFAULT.parse(in)) {
    for (String field : record) {
        System.out.print("\"" + field + "\", ");
    }
    System.out.println();
}
```

## 2. CSVFormat

`CSVFormat` 类提供了常用的 CSV 格式，采用如下信息定义 CSV 格式：

| 字段 | 说明 |
| ---- | ---- |
| `delimiter` | 字段分隔符，通常为 ";", "," 或 "\t" |
| `quoteCharacter` | 引号字符，用于将特殊字符括起来 |
| `quoteMode` | 加引号策略 |
| `commentMarker` | 注释字符 |
| `escapeCharacter` | 转义字符 |
| `ignoreSurroundingSpaces` | 解析时是否忽略值周围的空格 |
| `allowMissingColumnNames` | 解析标题时，是否允许丢失 column 名 |
| `ignoreEmptyLines` | 解析时是否跳过空行 |
| `recordSeparator` | record 分隔符，一般为换行符 |
| nullString | 指定转换为 null 的字符串 |
| `headerComments` | 标题注释数组 |
| `headers` | 标题 |
| `skipHeaderRecord` | 是否跳过标题行 |
| `ignoreHeaderCase` | 解析时访问 header names 时忽略大小写 |
| trailingDelimiter | 是否添加 trailing 分隔符 |
| trim | 输出时字符移出前后空格 |
| autoFlush | 关闭时是否 flush |
| quotedNullString | quoteCharacter+nullString+quoteCharacter |
| duplicateHeaderMode | 是否允许标题中有重复值 |

`CSVFormat` 提供了如下几套常用配置：

| FORMAT | 说明 |
| ---- | ---- |
| `DEFAULT` | 标准 CSV 格式 (RFC4180)，允许空行 |
| `EXCEL` | Excel CSV 格式 |
| `INFORMIX_UNLOAD` | Informix UNLOAD format used by the `UNLOAD TO file_name` operation |
| `INFORMIX_UNLOAD_CSV` | Informix CSV UNLOAD format used by the UNLOAD TO file_name operation (escaping is disabled.) |
| `MONGO_CSV` | mongoexport 操作使用的 MongoDB CSV 格式 |
| `MONGO_TSV` | mongoexport 操作使用的 MongoDB TSV 格式 |
| `MYSQL` | MySQL CSV 格式 |
| `ORACLE` | Default Oracle format used by the SQL*Loader utility |
| `POSTGRESSQL_CSV` | PostgreSQL COPY 操作对应的默认 CSV 格式 |
| `POSTGRESSQL_TEXT` | PostgreSQL COPY 操作对应的默认 text 格式 |
| `RFC-4180` | RFC-4180 定义格式 |
| `TDF` | tab 分隔格式 |

### DEFAULT

标准 CSV 格式，同 RFC4180，但是自动跳过空行。对应 `CSVFormat.Builder` 配置：

```java
setDelimiter(',')
setQuote('"')
setRecordSeparator("\r\n")
setIgnoreEmptyLines(true)
setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)
```
### EXCEL

Excel 文件格式（用逗号作为分隔符）。不过 Excel 实际使用的分隔符与 locale 相关，可能需要根据 locale 调整该格式。

例如，在 French 系统上解析或生成 CSV 文件，使用如下格式：

```java
CSVFormat fmt = CSVFormat.EXCEL.withDelimiter(';');
```

EXCEL 的 `CSVFormat.Builder` 设置：

```java
setDelimiter(',')
setQuote('"')
setRecordSeparator("\r\n")
setIgnoreEmptyLines(false)
setAllowMissingColumnNames(true)
setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)
```

```ad-note
EXCEL 在 RFC4180 基础上添加了 `Builder#setAllowMissingColumnNames(true)` 和 `Builder#setIgnoreEmptyLines(false)`
```
### INFORMIX_UNLOAD

`UNLOAD TO file_name` 使用的 Informix CSV UNLOAD 格式。逗号分隔以 LF 换行，值不加引号，特殊字符用 `'\'` 转义。默认 NULL 字符串为 `"\\N"`。

`CSVFormat.Builder` 设置：

```java
setDelimiter(',')
setEscape('\\')
setQuote("\"")
setRecordSeparator('\n')
```
### INFORMIX_UNLOAD_CSV

`UNLOAD TO file_name` 使用的 Informix CSV UNLOAD 格式（禁用转义）。逗号分隔以 LF 换行，值不加引号。默认 NULL 字符串为 `"\\N"`。

```java
setDelimiter(',')
setQuote("\"")
setRecordSeparator('\n')
```
### TDF

对应 `CSVFormat.Builder` 设置：

```java
setDelimiter('\t')
setQuote('"')
setRecordSeparator("\r\n")
setIgnoreSurroundingSpaces(true)
```
## 3. 使用 CSVFormat

例如：

```java
CSVParser parser = CSVFormat.EXCEL.parse(reader);
```

`CSVParser` 提供解析**其它输入类型**的 `static` 方法，例如：

```java
CSVParser parser = CSVParser.parse(file, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
```

## 4. 定义 CSVFormat

使用 `CSVFormat.Builder` 类定义 `CSVFormat`。例如，以 `CSVFormat.EXCEL` 为起点开始定义 CSV 格式：

```java
Builder builder = CSVFormat.EXCEL.builder();
```

然后调用 `Builder` 的方法配置 CSV 格式。

- 设置分隔符

分隔符通常为 `,`, `;` 或 `\t`。

```java
public Builder setDelimiter(final char delimiter)
public Builder setDelimiter(final String delimiter)
```

- 设置引号

设置为 `null` 表示禁用。

```java
public Builder setQuote(final char quoteCharacter)
public Builder setQuote(final Character quoteCharacter)
```

- 设置换行符

换行符，即不同 record 之间的分隔符，通常为 `\n`, `\r` 或 `\r\n`。

```java
public Builder setRecordSeparator(final char recordSeparator)
public Builder setRecordSeparator(final String recordSeparator)
```

!!! note
    这里设置的换行符仅用于输出；解析则自动识别 `\n`, `\r` 或 `\r\n`，目前无法自定义。

- 忽略空行

```java
public Builder setIgnoreEmptyLines(final boolean ignoreEmptyLines)
```

对空行有两种处理方式：忽略或生成空 record。

- 重复标题处理方式

```java
public Builder setDuplicateHeaderMode(final DuplicateHeaderMode duplicateHeaderMode)
```

可以通过调用 set 方法扩展已有 format。例如：

```java
CSVFormat.EXCEL.withNullString("N/A").withIgnoreSurroundingSpaces(true);
```

### skipHeaderRecord

在解析 CSV 时，仅 `CSVFormat` 的 header 不为 `null`，且长度大于 0 时，才考虑 `skipHeaderRecord` 字段时，如果为 true，使用第一个 record 的注释更新 `headerComment` 字段值。

在输出 CSV 时，如果 `CSVFormat` 的 header 不为 `null`，且 `skipHeaderRecord` 为 true，则将 `CSVFormat` 的 header 输出。

总结，如果 CSV 文件自带标题，这个字段的用处不大。

## 5. 读 CSV（CSVParser）

```java
public final class CSVParser
	implements Iterable<CSVRecord>, Closeable
```

解析 CSV 返回 `CSVParser` 对象，该类型实现了 `Iterable<CSVRecord>` 接口。

### 创建 CSVParser

`CSVParser` 提供了多个工厂方法，以从不同输入类型读取数据：

```java
parse(java.io.File, Charset, CSVFormat)
parse(String, CSVFormat)
parse(java.net.URL, java.nio.charset.Charset, CSVFormat)
```

对喜欢 fluent API 样式的，可以使用 `CSVFormat.parse(java.io.Reader)` 创建：

```java
for(CSVRecord record : CSVFormat.EXCEL.parse(in)) {
    ...
}
```

### 逐步解析 record

从文件中解析 CSV 输入：

```java
File csvData = new File("/path/to/csv");
CSVParser parser = CSVParser.parse(csvData, CSVFormat.RFC4180);
for (CSVRecord csvRecord : parser) {
    ...
}
```

这将使用 RFC4180 格式读取并解析文件内容。

解析 Excel 样式的 CSV 输入：

```java
CSVParser parser = CSVParser.parse(csvData, CSVFormat.EXCEL);
for (CSVRecord csvRecord : parser) {
    ...
}
```

如果预定义格式与手头的格式不匹配，则可以[自定义格式](#定义-csvformat)。

### 一步解析到内存

如果不想逐步解析，而是一步将输入完全读入内存，可以采用：

```java
Reader in = new StringReader("a;b\nc;d");
CSVParser parser = new CSVParser(in, CSVFormat.EXCEL);
List<CSVRecord> list = parser.getRecords();
```

该方法有两个限制：

1. 解析到内存从解析器的当前位置开始。如果已经解析部分记录，那么解析过的记录不会保存到 list。
2. 更消耗内存。

## 6. 写 CSV（CSVPrinter）

使用 `CSVPrinter` 输出 CSV 格式：

- 调用 `CSVPrinter.print(Object)` 输出值；
- 输出值根据 `String.valueOf(Object)` 转换为字符；
- 调用 `println()` 表示一条 record 结束；
- `printComment(String)` 输出一条注释：只有 `CSVFormat` 支持注释时，该注释才会输出到文件；
- `printRecord(Object...)` 和 `printRecord(Iterable)` 一次输出一条 record；
- `printRecords(Object...)`, `printRecords(Iterable)` 和 `printRecords(ResultSet)` 一次输出多条 records。

例如：

```java
try (CSVPrinter printer = new CSVPrinter(new FileWriter("csv.txt"), CSVFormat.EXCEL)) {
    printer.printRecord("id", "userName", "firstName", "lastName", "birthday");
    printer.printRecord(1, "john73", "John", "Doe", LocalDate.of(1973, 9, 15));
    printer.println();
    printer.printRecord(2, "mary", "Mary", "Meyer", LocalDate.of(1985, 3, 29));
} catch (IOException ex) {
    ex.printStackTrace();
}
```

该代码输出如下 csv.txt：

```csv
 id,userName,firstName,lastName,birthday
 1,john73,John,Doe,1973-09-15

 2,mary,Mary,Meyer,1985-03-29
```

## 7. 标题

Apache Commons CSV 支持通过索引和标题访问数据。

### 索引

通过索引访问数据，不需要对 `CSVFormat` 进行额外配置：

```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
for (CSVRecord record : records) {
    String columnOne = record.get(0);
    String columnTwo = record.get(1);
}
```

### 自定义标题

通过索引访问不够直观，为每列分配标题名称，通过标题访问更便捷：

```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("ID", "CustomerNo", "Name").parse(in);
for (CSVRecord record : records) {
    String id = record.get("ID");
    String customerNo = record.get("CustomerNo");
    String name = record.get("Name");
}
```

### 使用 enum 定义标题

使用 `String` 定义和访问 column 容易出错，使用 enum 定义标题则没该问题。

不过使用 enum 常量定义 column 名，不可避免会出现 enum 常量名不符合 Java 标准规范：

```java
public enum Headers {
    ID, CustomerNo, Name
}
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(Headers.class).parse(in);
for (CSVRecord record : records) {
    String id = record.get(Headers.ID);
    String customerNo = record.get(Headers.CustomerNo);
    String name = record.get(Headers.Name);
}
```

### 自动检测标题

有些 CSV 文件第一行定义标题。此时可以配置 `CSVFormat` 将第一行作为标题处理：

- `setHeader()` 无参调用，表示自动从输入文件解析标题：将第一个 record 作为 header

```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.Builder.create(CSVFormat.RFC4180)
        .setHeader().setSkipHeaderRecord(true)
        .build().parse(in);
for (CSVRecord record : records) {
    String id = record.get("ID");
    String customerNo = record.get("CustomerNo");
    String name = record.get("Name");
}
```

此时将第一行作为标题处理，迭代时自动跳过第一行。

### 打印标题

输出包含标题的 CSV，需要在 CSVFormat 中指定标题：

```java
final Appendable out = ...;
CSVFormat format = CSVFormat.Builder.create().setHeader("H1", "H2").build();
CSVPrinter printer = format.print(out);
```

输出 JDBC 标题：

```java
final ResultSet resultSet = ...;
final CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).print(out)
```

### 重复标题值

对重复的标题值，commons-csv 提供了三种处理方式：

```java
public enum DuplicateHeaderMode {

    /**
     * Allows all duplicate headers.
     */
    ALLOW_ALL,

    /**
     * Allows duplicate headers only if they're empty, blank, or null strings.
     */
    ALLOW_EMPTY,

    /**
     * Disallows duplicate headers entirely.
     */
    DISALLOW
}
```

