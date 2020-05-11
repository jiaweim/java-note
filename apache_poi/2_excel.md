# poi-excel

- [poi-excel](#poi-excel)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [SXSSF](#sxssf)
    - [限制方法](#%e9%99%90%e5%88%b6%e6%96%b9%e6%b3%95)
  - [使用](#%e4%bd%bf%e7%94%a8)
    - [New Workbook](#new-workbook)
    - [New Sheet](#new-sheet)
    - [Creating Cells](#creating-cells)
    - [Creating Date Cells](#creating-date-cells)
    - [不同 cell 类型](#%e4%b8%8d%e5%90%8c-cell-%e7%b1%bb%e5%9e%8b)
    - [Files vs InputStreams](#files-vs-inputstreams)
  - [数据格式化](#%e6%95%b0%e6%8d%ae%e6%a0%bc%e5%bc%8f%e5%8c%96)

2020-05-11, 14:01
***

## 简介

poi 提供了 HSSF 和 XSSF，分别用于 xls 和 xlsx 文件的读写。HSSF 和 XSSF 提供了创建、修改和读写 EXCEL 电子表格的方法。包括：

- 提供了底层结构（low level structure）
- 为只读提供高效事件模型的API
- 为创建、读取和修改XLS文件的完整用户模型API

使用说明：

- 如果仅仅需要读取表格数据，可以使用 `org.apache.poi.hssf.eventusermodel` 或 `org.apache.poi.xssf.eventusermodel` 包中的事件模型API，可减少内存占用。
- .xlsx 基于XML 格式，相对 .xls 基于二进制的格式，占用更多内存。
- 额外的 sxssf 是基于流的API，在XSSF基础上限制了内存中Row的数目，从而减少内存消耗。

## SXSSF

POI 在 3.8 版之后，提供了 SXSSF API，该API 在 XSSF 基础上基于流构建，内存占用低。

使用限制：

- 在一个时间点，只能访问有限的行
- 不支持 Sheet.clone() 操作
- 不支持函数

![compare table](images/2020-05-11-14-04-58.png)

SXSSF 作为XSSF 的流版本，通过限制可访问 row 的数目，减少内存占用。

### 限制方法

构造时限制

```java
new SXSSFWorkbook(int windowSize)
```

调用方法限制:

```java
SXSSF.setRandomAccessWindowSize(int windowSize)
```

默认数值为100. 设置为 -1 表示无限制。
SXSSF 生成的临时文件，必须通过调用 dispose 方法清除。

SXSSFWorkbook 默认使用内联字符串，而不是共享字符串表（shared string table），这样效率高很多，因为内存中不需要预先保存内容，但是生成的文档，可能和某些客户端不兼容。

使用 shared string 保存文档中的字符串都是 unique 的，不过根据文档内容，使用 shared string 可能占据更多资源。

例：

```java
SXSSFWorkbook wb = new SXSSFWorkbook(100);
SXSSFSheet sh = wb.createSheet();
for (int rownum = 0; rownum < 1000; rownum++) {
    SXSSFRow row = sh.createRow(rownum);
    for (int cellnum = 0; cellnum < 10; cellnum++) {
        SXSSFCell cell = row.createCell(cellnum);
        String address = new CellReference(cell).formatAsString();
        cell.setCellValue(address);
    }
}

// Rows with rownum < 900 are flushed and not accessible
for (int rownum = 0; rownum < 900; rownum++) {
    Assert.assertNull(sh.getRow(rownum));
}

// ther last 100 rows are still in memory
for (int rownum = 900; rownum < 1000; rownum++) {
    Assert.assertNotNull(sh.getRow(rownum));
}

FileOutputStream out = new FileOutputStream("/temp/sxssf.xlsx");
wb.write(out);
out.close();

// dispose of temporary files backing this workbook on disk
wb.dispose();
```

注意最后 `dispose()` 调用删除临时文件。

## 使用

这部分包含基本的使用方法。

### New Workbook

```java
Workbook wb = new HSSFWorkbook();
try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
    wb.write(fileOut);
}

Workbook wb2 = new XSSFWorkbook();
try (OutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
    wb2.write(fileOut);
}
```

### New Sheet

```java
Workbook wb = new HSSFWorkbook(); // or new XSSFWorkbook();
Sheet sheet1 = wb.createSheet("new sheet");
Sheet sheet2 = wb.createSheet("second sheet");

// sheet name 最多 31 个字符，并且不能包含如下字符：
// 0x0000
// 0x0003
// colon (:)
// backslash (\)
// asterisk (*)
// question mark (?)
// forward slash (/)
// opening square bracket ([)
// closing square bracket (])

try(OutputStream fileOut = new FileOutputStream("workbook.xls")){
    wb.write(fileOut);
}
```

### Creating Cells

```java
Workbook wb = new HSSFWorkbook();
// Workbook wb = new XSSFWorkbook();
CreationHelper creationHelper = wb.getCreationHelper();
Sheet sheet = wb.createSheet("new sheet");

Row row = sheet.createRow(0);
// Create a cell and put a value in it
Cell cell = row.createCell(0);
cell.setCellValue(1);

// or do it on one line
row.createCell(2).setCellValue(creationHelper.createRichTextString("This is a string"));

// write the output to a file
try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
    wb.write(fileOut);
}
```

### Creating Date Cells

```java
Workbook wb = new HSSFWorkbook();
CreationHelper creationHelper = wb.getCreationHelper();
Sheet sheet = wb.createSheet("new sheet");

// Create a row and put some cells in it. Rows are 0 based.
Row row = sheet.createRow(0);

// Create a cell and put a date value in it.  The first cell is not styled as a date.
Cell cell = row.createCell(0);
cell.setCellValue(new Date());

// we style the second cell as a date (and time).  It is important to
// create a new cell style from the workbook otherwise you can end up
// modifying the built in style and effecting not only this cell but other cells.
CellStyle cellStyle = wb.createCellStyle();
cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("m/d/yy h:mm"));
cell = row.createCell(1);
cell.setCellValue(new Date());
cell.setCellStyle(cellStyle);

//you can also set date as java.util.Calendar
cell = row.createCell(2);
cell.setCellValue(Calendar.getInstance());
cell.setCellStyle(cellStyle);

// Write the output to a file
try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
    wb.write(fileOut);
}
```

### 不同 cell 类型

```java
Workbook wb = new HSSFWorkbook();
Sheet sheet = wb.createSheet("new sheet");
Row row = sheet.createRow(2);
row.createCell(0).setCellValue(1.1);
row.createCell(1).setCellValue(new Date());
row.createCell(2).setCellValue(Calendar.getInstance());
row.createCell(3).setCellValue("a string");
row.createCell(4).setCellValue(true);
row.createCell(5).setCellType(CellType.ERROR);

// Write the output to a file
try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
    wb.write(fileOut);
}
```

### Files vs InputStreams

当打开一个 Excel 文件，可以使用 File 或 InputStream 创建 Workbook。使用 File 对象可以使内存占用更低，使用 InputStream 则需要缓冲整个文件，因此更为占用内存。

使用 `WorkbookFactory` 很容易区分两者：

```java
// Use a file
Workbook wb = WorkbookFactory.create(new File("MyExcel.xls"));

// Use an InputStream, needs more memory
Workbook wb = WorkbookFactory.create(new FileInputStream("MyExcel.xlsx"));
```

## 数据格式化

例如：

```java
Workbook wb = new HSSFWorkbook();
Sheet sheet = wb.createSheet("format sheet");
CellStyle style;
DataFormat format = wb.createDataFormat();
Row row;
Cell cell;
int rowNum = 0;
int colNum = 0;
row = sheet.createRow(rowNum++);
cell = row.createCell(colNum);
cell.setCellValue(11111.25);

style = wb.createCellStyle();
style.setDataFormat(format.getFormat("0.0"));
cell.setCellStyle(style);
row = sheet.createRow(rowNum++);
cell = row.createCell(colNum);
cell.setCellValue(11111.25);
style = wb.createCellStyle();
style.setDataFormat(format.getFormat("#,##0.0000"));
cell.setCellStyle(style);
try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
    wb.write(fileOut);
}
wb.close();
```
