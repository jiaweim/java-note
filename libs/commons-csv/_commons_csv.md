# Apache Commons CSV

- [Apache Commons CSV](#apache-commons-csv)
  - [CSV 格式](#csv-格式)
    - [DEFAULT](#default)
    - [EXCEL](#excel)
    - [INFORMIX\_UNLOAD](#informix_unload)
    - [INFORMIX\_UNLOAD\_CSV](#informix_unload_csv)
    - [TDF](#tdf)
    - [使用 CSVFormat](#使用-csvformat)
    - [定义 CSVFormat](#定义-csvformat)
  - [读 CSV](#读-csv)
    - [解析 Excel CSV 文件](#解析-excel-csv-文件)
  - [写 CSV](#写-csv)
  - [标题](#标题)
    - [索引](#索引)
    - [自定义标题](#自定义标题)
    - [使用 enum 定义标题](#使用-enum-定义标题)
    - [自动检测标题](#自动检测标题)
    - [打印标题](#打印标题)

@author Jiawei Mao
****

## CSV 格式

`CSVFormat` 类提供了常用的 CSV 格式：

|FORMAT|说明|
|---|---|
|DEFAULT|标准 CSV 格式 (RFC4180)，但允许空行|
|EXCEL|Excel CSV 格式|
|INFORMIX_UNLOAD|Informix UNLOAD format used by the `UNLOAD TO file_name` operation|
|INFORMIX_UNLOAD_CSV|Informix CSV UNLOAD format used by the UNLOAD TO file_name operation (escaping is disabled.)|
|MONGO_CSV|mongoexport 操作使用的 MongoDB CSV 格式|
|MONGO_TSV|mongoexport 操作使用的 MongoDB TSV 格式|
|MYSQL|MySQL CSV 格式|
|ORACLE|Default Oracle format used by the SQL*Loader utility|
|POSTGRESSQL_CSV|PostgreSQL COPY 操作对应的默认 CSV 格式|
|POSTGRESSQL_TEXT|PostgreSQL COPY 操作对应的默认 text 格式|
|RFC-4180|RFC-4180 定义格式|
|TDF|tab 分隔格式|

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

!!! note
    EXCEL 在 RFC4180 基础上添加了 `Builder#setAllowMissingColumnNames(true)` 和 `Builder#setIgnoreEmptyLines(false)`

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

### 使用 CSVFormat

例如：

```java
CSVParser parser = CSVFormat.EXCEL.parse(reader);
```

`CSVParser` 提供解析的 `static` 方法，例如：

```java
CSVParser parser = CSVParser.parse(file, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
```

### 定义 CSVFormat

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

换行符，即不同 record 之间的分隔符。通常为 `\n`, `\r` 或 `\r\n`。

```java
public Builder setRecordSeparator(final char recordSeparator)
public Builder setRecordSeparator(final String recordSeparator)
```

!!! note
    换行符仅用于输出；解析则自动识别 `\n`, `\r` 或 `\r\n`，目前无法自定义。

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



## 读 CSV



### 解析 Excel CSV 文件

```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
for (CSVRecord record : records) {
    String lastName = record.get("Last Name");
    String firstName = record.get("First Name");
}
```

## 写 CSV



## 标题

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