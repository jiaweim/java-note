# SQLite 数据类型

- [SQLite 数据类型](#sqlite-数据类型)
  - [简介](#简介)
  - [SQLite 存储数据类型](#sqlite-存储数据类型)
    - [Boolean 类型](#boolean-类型)
    - [Date and Time 类型](#date-and-time-类型)
  - [SQLite 关联类型](#sqlite-关联类型)

2020-07-07, 18:02
***

## 简介

大部分 SQL 数据库引擎使用静态类型。对静态类型，某个值的数据类型取决于其所处的容器（保存该值的 column 的类型）。

SQLite 使用更通用的动态类型。在 SQLite 中，数值的类型与其值关联，而不是容器。SQLite 的动态类型系统和其它数据库中常见的静态类型系统兼容，在静态类型数据库中运行的 SQL 语句在 SQLite 中也能一样功能。而且，SQLite 中可以执行其它静态类型数据库中无法完成的操作。

## SQLite 存储数据类型

SQLite 数据库中存储的数值类型：

| 类型    | 说明                           |
| ------- | ------------------------------ |
| NULL    | NULL 值                        |
| INTEGER | 有符号整数                     |
| REAL    | 浮点数                         |
| TEXT    | 文本                           |
| BLOB    | 不做任何转换，输入什么存储什么 |

SQLite 存储类型比数据类型更通用。例如，`INTEGER` 存储类型包含 6 种不同长度的整数数据类型。在硬盘上存储不同，但是用 SQLite 读入内存后，都转换为通用类型（8-byte signed integer）。

大多时候，存储类型和数据类型无法区分，可以互换使用。

除了 `INTEGER PRIMARY KEY` 列，SQLite3 数据库的column可以存储任何存储类型的数值。

SQL 语句，无论是嵌入在 SQL 语句中的文本或预编译 SQL 语句的参数，都隐式具有存储类。

### Boolean 类型

SQLite 没有单独的 Boolean 类型，Boolean 值存储为整数 0 (false) 和 1(true)。

### Date and Time 类型

SQLite 没有单独的日期和时间类。不过 SQLite 内置的日期和时间函数可以将日期时间存储为 `TEXT`, `REAL` 或 `INTEGER` 值：

- TEXT, ISO8601 字符串 ("YYYY-MM-DD HH:MM:SS.SSS")
- REAL, Julian day numbers
- INTEGER, Unix Time, 从 1970-01-01 00:00:00 UTC 开始的 seconds 数

## SQLite 关联类型

静态类型的 SQL 数据库引擎通常会自动将数值转换为合适的数据类型。例如：

```sql
CREATE TABLE t1(a INT, b VARCHAR(10));
INSERT INTO t1(a,b) VALUES('123',456);
```

静态类型数据库会将字符串 '123' 转换为 integer 123，将 integer 456 转换为字符串 '456'。

为了提高 SQLite 和其它数据库引擎的兼容性，使用 SQLite 也执行上面相同的操作，SQLite 添加了关联类型（type affinity）的概念。column 的关联类型是存储该列中数值的推荐类型。这里的核心思想是，该类型是推荐类型，而不是强制类型。任何列依然可以存储任意类型的数据。只是具有关联类型的 columns 优先使用特定类型存储数值。column 首选的存储类型称为关联类型（affinity）。

SQLite3 数据库支持的关联类型有：

- TEXT
- NUMBERIC
- INTEGER
- REAL
- BLOB

亲和类型为 TEXT 的 column 以存储类型 NULL, TEXT 或 BLOB 存储数值。如果将一个 numberic 数据插入 TEXT column，则在存储前会转换为 text。

NUMERIC column 可能使用所有 5 种存储类型存储数值。

- 当将 text 数据插入 NUMERIC column：
  - 如果 text 格式满足要求，会转换为 INTEGER 或 REAL 保存类型，
  - 如果 TEXT 满足 INTEGER 格式，但是太大 64-bit signed integer 保存不下，则转换为 REAL
  - TEXT 和 REAL 存储类之间转换，只保留 15 个有效数字
  - 如果 TEXT 无法被转换为数字，则保存为 TEXT
  - 16 进制整数以 TEXT 保存
- NULL 和 BLOB 值不进行转换
- 有些字符串的格式看起来想浮点数，但是可以用 integer 表示，NUMERIC column 会将其转换为 integer。例如 '3.0E+5' 在 NUMERIC column 中保存为 integer 300000，而不是浮点数 300000.0。

INTGER column 和 NUMBERIC column 行为 一样。

SQLite 支持关联类型（type affinity）。列可以存储任何类型数据，但是有个推荐