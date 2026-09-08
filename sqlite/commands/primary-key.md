# Primary Key



## 主键简介

**主键**（primary key）是用于标识表中每一行唯一性的一列或一组列。每个表有且仅有一个主键。

SQLite 允许你通过以下两种方式来定义主键：

1. 如果主键只包含一列，则可以使用 `PRIMARY KEY` 列约束来定义主键，如下所示：

```sql
CREATE TABLE table_name(
   column_1 INTEGER NOT NULL PRIMARY KEY,
   ...
);
```

2. 如果主键由两列或多列组成，则需要使用 `PRIMARY KEY` 表约束来定义主键，如下面的语句所示：

```sql
CREATE TABLE table_name(
   column_1 INTEGER NOT NULL,
   column_2 INTEGER NOT NULL,
   ...
   PRIMARY KEY(column_1, column_2, ...)
);
```

在 SQL 标准中，主键列不能包含 `NULL` 值。这意味着主键列隐式地带有 `NOT NULL` 约束。

然而，为了使当前版本的 SQLite 与早期版本保持兼容，SQLite 允许主键列包含 `NULL` 值。

## SQLite 主键与 rowid 表

创建表时如果没有指定 `WITHOUT ROWID` 选项，SQLite 会添加一个名为 `rowid` 的隐式列，该列存储一个 64 位有符号整数。`rowid` 列是唯一标识表中行的键。具有 `rowid` 列的表被称为 `rowid` 表。

如果一个表的主键只包含一列，并且该列被定义为 `INTEGER` 类型，那么这个主键列就会成为 `rowid` 列的别名。

需要注意的是，如果你为主键列分配了其他整数类型（如 `BIGINT` 和 `UNSIGNED INT`），该列将不会成为 `rowid` 列的别名。

由于 `rowid` 表使用 B 树来组织数据，因此对 `rowid` 表进行数据查询和排序的速度非常快。这比使用非 `rowid` 别名的主键要快。

另一个重要提示是，如果你声明一个列为 `INTEGER` 类型，但同时使用了 `PRIMARY KEY DESC` 子句，该列也不会成为 `rowid` 列的别名：

```sql
CREATE TABLE table(
   pk INTEGER PRIMARY KEY DESC,
   ...
);
```

## 创建 SQLite 主键示例

以下语句创建了一个名为 `countries`（国家）的表，并将 `country_id` 列作为主键。

```sql
CREATE TABLE countries (
   country_id INTEGER PRIMARY KEY,
   name TEXT NOT NULL
);
```

因为 `countries` 表的主键只有一个列，所以我们使用了 `PRIMARY KEY` 列约束来定义主键。

当然，你也可以使用 `PRIMARY KEY` 表约束来定义只包含一个列的主键，如下面的语句所示：

```sql
CREATE TABLE languages (
   language_id INTEGER,
   name TEXT NOT NULL,
   PRIMARY KEY (language_id)
);
```

然而，对于主键由多个列组成的表，你必须使用 `PRIMARY KEY` 表约束来定义主键。

以下语句创建了 `country_languages`（国家语言）表，其主键由两列组成。

```sql
CREATE TABLE country_languages (
   country_id INTEGER NOT NULL,
   language_id INTEGER NOT NULL,
   PRIMARY KEY (country_id, language_id),
   FOREIGN KEY (country_id) REFERENCES countries (country_id) 
            ON DELETE CASCADE ON UPDATE NO ACTION,
   FOREIGN KEY (language_id) REFERENCES languages (language_id) 
            ON DELETE CASCADE ON UPDATE NO ACTION
);
```

## 添加 SQLite 主键示例

与其他数据库系统（如 MySQL 和 PostgreSQL）不同，你**不能**使用 `ALTER TABLE` 语句为现有表添加主键。

你需要遵循以下步骤来绕过这一限制：

1. 关闭外键约束检查。
2. 将该表重命名为另一个名称（如 `old_table`）。
3. 创建一个新表（`table`），其结构与你刚刚重命名的旧表完全一致。
4. 将数据从 `old_table` 复制到新表 `table` 中。
5. 重新开启外键约束检查。

请参考以下语句：

```sql
PRAGMA foreign_keys=off;

BEGIN TRANSACTION;

ALTER TABLE table RENAME TO old_table;

-- 在这里定义主键约束
CREATE TABLE table ( ... );

INSERT INTO table SELECT * FROM old_table;

COMMIT;

PRAGMA foreign_keys=on;
```

`BEGIN TRANSACTION` 会开启一个新事务。它确保后续的所有语句要么全部成功执行，要么全都不执行。
`COMMIT` 语句用于提交所有操作。

让我们创建一个名为 `cities`（城市）的表，且不带主键：

```sql
CREATE TABLE cities (
   id INTEGER NOT NULL,
   name TEXT NOT NULL
);

INSERT INTO cities (id, name)
VALUES(1, 'San Jose');
```

*(Try It / 试一试)*

为了给 `cities` 表添加主键，你需要执行以下步骤：

```sql
PRAGMA foreign_keys=off;

BEGIN TRANSACTION;

ALTER TABLE cities RENAME TO old_cities;

CREATE TABLE cities (
   id INTEGER NOT NULL PRIMARY KEY,
   name TEXT NOT NULL
);

INSERT INTO cities 
SELECT * FROM old_cities;

DROP TABLE old_cities;

COMMIT;

PRAGMA foreign_keys=on;
```

*(Try It / 试一试)*

如果你使用的是 SQLite 的图形界面（GUI）工具，可以使用以下语句来查看表的信息：

```sql
PRAGMA table_info([cities]);
```

*(Try It / 试一试)*

在本教程中，你已经学习了如何使用 SQLite 的 `PRIMARY KEY` 约束来为表定义主键。



## 参考

- https://www.sqlitetutorial.net/sqlite-primary-key/