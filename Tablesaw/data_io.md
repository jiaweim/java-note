# 导入和导出数据

2024-07-26
@author Jiawei Mao
***

## 支持的格式

| Format                         | Import | Export |
| :----------------------------- | :----- | :----- |
| CSV (and other delimited text) | Yes    | Yes    |
| JSON                           | Yes    | Yes    |
| RDBMS (via JDBC)               | Yes    |        |
| Fixed Width Text               | Yes    | Yes    |
| Excel                          | Yes    |        |
| HTML                           | Yes    | Yes    |

## 导入数据

> [!NOTE]
>
> 为了减少包的大小，tablesaw 将不同文件格式的读写功能放到单独的模块中。

大多数文本格式的处理方式类似。

读取 CSV 文件：

```java
Table t = Table.read().file("myFile.csv");
```

`Table.read()` 默认 columns 分隔符为都好，并且有一个标题行。

也可以使用 `CsvReadOptions` 自定义选择。例如：

```java
CsvReadOptions.Builder builder = 
	CsvReadOptions.builder("myFile.csv")
		.separator('\t')					// table is tab-delimited
		.header(false)						// no header
		.dateFormat("yyyy.MM.dd");  		// the date format to use. 

CsvReadOptions options = builder.build();

Table t1 = Table.read().usingOptions(options);
```

- `header` flag 表示第一行是否为标题；
- `separator` 用于指定分隔符，对 tsv 文件设置为 `\t`
- `dateFormat` 指定日期格式。文件中的所有日期必须采用相同的格式，由 `java.time.format.DateTimeFormatter` 类定义

创建 table 后，会根据文件名指定其名称。也可以使用 `table.setName(aString);` 随时修改。

## Column 类型

Tablesaw 查看文件中每列数据，并粗略的猜测类型。如果希望在猜测类型时考虑该 column 所有数据，可以设置 `sample(false)`。

如果无法匹配类型，则取默认 StringColumn。

### 指定 column 类型

将 `ColumnType` 数组传入 `read().csv()` 显式指定 column 类型。例如：

```java
ColumnType[] types = {LOCAL_DATE, INTEGER, FLOAT, FLOAT, CATEGORY};
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .columnTypes(types));
```

虽然增加了工作量，但是有气优势：

1. 不需要推断 column 类型，所以减少了文件加载时间；
2. 可以完全控制 column 类型；
3. 某些情况下，必须指定 column 类型。例如，如果一个文件将时间编码为 HHmm，中午时间就是 "1200"，显然 Tablesaw 无法给出正确类型。

### 查看猜测类型

当 table 包含许多 columns，手动指定 column 类型会很繁琐，如果自动挡好用，干嘛用手动。所以 `CsvReader` 提供了查看猜测的 column 类型的方法。可以检查猜测是否正确：

```java
String types = CsvReader.printColumnTypes("data/bush.csv", true, ','));
System.out.println(types);
```

```
ColumnType[] columnTypes = {
  LOCAL_DATE, // 0 date 
  SHORT_INT,  // 1 approval 
  CATEGORY,   // 2 who 
}
```

返回的 String 数组符合 Java 代码规范，可以直接粘贴进去。然后对猜测错误的类型进行修改。

### 跳过 column

指定 column 类型的另一个优点是可以跳过不需要的 column。将其类型指定为 `SKIP` 即可。例如：

```java
ColumnType[] types = {SKIP, INTEGER, FLOAT, FLOAT, SKIP};
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .columnTypes(types));
```

这样就跳过了第一个和最后一个 columns。

## 处理缺失值

tablesaw 有一组预定义的字符串，会自定将它们解释为缺失值。包括：“NaN”, “*”, “NA”, “null” 以及空字符串 “”。

当遇到其中一个字符串，tablesaw 将其替换为对应类型的缺失值：

- 对 String，缺失值为空字符串；
- 对 `Double` 为 `Double.NaN`；
- ...

也可以自定义缺失值字符串：

```java
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .missingValueIndicator("-"));
```

## 处理日期时间

由于不同地区日期和时间的格式不同，倒是其处理起来比较麻烦。和其他类型一样，tablesaw 会尝试正确处理日期，如果处理失败，有两种解救方法：

1. 指定 locale，除了处理日期，locale 还有助于处理数字格式
2. 指定日期 column 的准确格式，例如

```java
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .locale(Locale.FRENCH)
    .dateFormat("yyyy.MM.dd")
    .timeFormat("HH:mm:ss)
    .dateTimeFormat("yyyy.MM.dd::HH:mm:ss");
```

## Stream API

以 `java.io.InputStream` 为参数，读取 CSV 文件：

```java
Table.read().csv(InputStream stream, String tableName);
```

除了本地文件，还可以从网页读取：

```java
ColumnType[] types = {SHORT_INT, FLOAT, SHORT_INT};
String location = 
    "https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/bush.csv";
Table table = Table.read().usingOptions(CsvReadOptions.builder(new URL(location))
    .tableName("bush")
  	.columnTypes(types)));
```

从 S3 读 CSV：

```java
ColumnTypes[] types = {SHORT_INT, FLOAT, SHORT_INT};
S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));

InputStream stream = object.getObjectContent();
Table t = Table.csv(CsvReadOptions.builder(stream)
    .tableName("bush")
    .columnTypes(types)));
```

## Encoding

tablesaw 默认采用 UTF-8 编码。对其它编码，则需要通过 `FileInputStream` 指定编码：

```java
// file has a latin-1 encoding so, special sauce
InputStreamReader reader = new InputStreamReader(
			new FileInputStream("somefile.csv"), Charset.forName("ISO-8859-1"));

Table restaurants = Table.read()
		.usingOptions(CsvReadOptions.builder(reader, "restaurants"));
```

## 从 Database 导入

以数据库查询结果创建一个 table 也很容易。此时不需要指定 column 类型，因为数据库中包含类型信息：

```java
Table t = Table.read().db(ResultSet resultSet, String tableName);
```

包含 JDBC 设置的完成示例：

```java
String DB_URL = "jdbc:derby:CoffeeDB;create=true";
Connection conn = DriverManager.getConnection(DB_URL);

Table customer = null; 
try (Statement stmt = conn.createStatement()) {
  String sql = "SELECT * FROM Customer";
  try (ResultSet results = stmt.executeQuery(sql)) {
    customer = Table.read().db(results, "Customer");
  }
}
```

## 从 HTML，JSON，Excel 导入

读取这几种类型的功能在单独的模块中：

```xml
<dependency>
  <groupId>tech.tablesaw</groupId>
  <artifactId>tablesaw-html</artifactId>
</dependency>
<dependency>
  <groupId>tech.tablesaw</groupId>
  <artifactId>tablesaw-json</artifactId>
</dependency>
<dependency>
  <groupId>tech.tablesaw</groupId>
  <artifactId>tablesaw-excel</artifactId>
</dependency>
```

