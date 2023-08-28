# Apache Commons CSV

## 解析文件

CSVFormat 类提供了常用的 CSV 变体：

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

### 解析 Excel CSV 文件

```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
for (CSVRecord record : records) {
    String lastName = record.get("Last Name");
    String firstName = record.get("First Name");
}
```

### 

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

