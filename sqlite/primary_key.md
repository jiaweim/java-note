# Primary Key

## 简介

主键（primary key）是用于表示行的一列或多列值。每个表只有一个主键。

SQLite 提供了两种定义主键的方式：

1. 如果主键只有一列，则可以使用 `PRIMARY KEY` 修饰列来定义主键：

```sql
CREATE TABLE table_name(
   column_1 INTEGER NOT NULL PRIMARY KEY,
   ...
);
```

2. 如果主键由多列组成，则可以使用 `PRIMARY KEY` 修饰表格来定义主键：

```sql
CREATE TABLE table_name(
   column_1 INTEGER NOT NULL,
   column_2 INTEGER NOT NULL,
   ...
   PRIMARY KEY(column_1,column_2,...)
);
```

在 SQL 标准中，主键列不能包含 NULL 值，即隐含 `NOT NULL` 约束。

不过为了和早期的 SQLite 版本兼容，SQLite 主键允许有 NULL 值。

## primary key 和 rowid

在创建表格时如果不使用 `WITHOUT ROWID` 选项，SQLite 会自动创建一个名为 `rowid` 的隐式列，存储 64-bit signed integer。`rowid` 列为每一行添加唯一编号。

如果已有一列被指定为主键，且该类类型为 `INTEGER`，则该主键列被设置为 `rowid` 类。

