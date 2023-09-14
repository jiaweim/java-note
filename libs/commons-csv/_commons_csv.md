# Apache Commons CSV

## CSV 格式

`CSVFormat` 类提供了常用的 CSV 格式：

|FORMAT|说明|
|---|---|
|DEFAULT|标准 CSV 格式 (RFC4180)，但允许空行|
|EXCEL|The Microsoft Excel CSV format|
|INFORMIX_UNLOAD|Informix UNLOAD format used by the `UNLOAD TO file_name` operation|
|INFORMIX_UNLOAD_CSV|Informix CSV UNLOAD format used by the UNLOAD TO file_name operation (escaping is disabled.)|
|MONGO_CSV|MongoDB CSV format used by the mongoexport operation|
|MONGO_TSV|MongoDB TSV format used by the mongoexport operation|
|MYSQL|The MySQL CSV format|
|ORACLE|Default Oracle format used by the SQL*Loader utility|
|POSTGRESSQL_CSV|Default PostgreSQL CSV format used by the COPY operation|
|POSTGRESSQL_TEXT|Default PostgreSQL text format used by the COPY operation.
|RFC-4180|The RFC-4180 format defined by RFC-4180|
|TDF|A tab delimited format|

**DEFAULT**

对应 `CSVFormat.Builder` 设置：

- setDelimiter(',')
- setQuote('"')
- setRecordSeparator("\r\n")
- setIgnoreEmptyLines(true)
- setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)

**EXCEL**

Excel 文件格式（用逗号作为分隔符）。不过 Excel 实际使用的分隔符与 locale 相关，可能需要根据 locale 自定义。例如，在 French 系统上解析或生成 CSV 文件，使用如下格式：

```java
CSVFormat fmt = CSVFormat.EXCEL.withDelimiter(';');
```  
  
EXCEL 默认 `CSVFormat.Builder` 设置：

- setDelimiter(',')
- setQuote('"')
- setRecordSeparator("\r\n")
- setIgnoreEmptyLines(false)
- setAllowMissingColumnNames(true)
- setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)

!!! note
    EXCEL 在 RFC4180 基础上添加了 `Builder#setAllowMissingColumnNames(true)` 和 `Builder#setIgnoreEmptyLines(false)`

### 使用 CSVFormat

例如：

```java
CSVParser parser = CSVFormat.EXCEL.parse(reader);
```

CSVParser 提供了解析其它输入类型的 static 方法，例如：

```java
CSVParser parser = CSVParser.parse(file, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
```

### 定义 CSVFormat

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

### by index

```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
for (CSVRecord record : records) {
    String columnOne = record.get(0);
    String columnTwo = record.get(1);
}
```

### 自定义 header

```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("ID", "CustomerNo", "Name").parse(in);
for (CSVRecord record : records) {
    String id = record.get("ID");
    String customerNo = record.get("CustomerNo");
    String name = record.get("Name");
}
```

