# SQLite Create Table

## 简介

要在 SQLite 中创建一个新表，需要使用 `CREATE TABLE` 语句，其语法如下：

```sql
CREATE TABLE [IF NOT EXISTS] [schema_name].table_name (
	column_1 data_type PRIMARY KEY,
	column_2 data_type NOT NULL,
	column_3 data_type DEFAULT 0,
	table_constraints
) [WITHOUT ROWID];
```

在这个语法中：

1. 在 `CREATE TABLE` 关键字后指定要创建的表的名称。表名不能以 `sqlite_` 开头，因为这是 SQLite 内部的保留字。
2. 使用 `IF NOT EXISTS` 选项，可以在表不存在时创建新表。如果不使用 `IF NOT EXISTS` 选项而尝试创建一个已存在的表，将会导致错误。
3. 可选地指定新表所属的 `schema_name`（模式名）。该模式可以是主数据库、`temp` 数据库或任何已附加的数据库。
4. 指定表的 column 列表。每一列都有一个名称、数据类型和列约束。SQLite 支持 `PRIMARY KEY`（主键）、`UNIQUE`（唯一）、`NOT NULL`（非空）和 `CHECK`（检查）列约束。

- **第五**，指定表约束，例如 `PRIMARY KEY`、`FOREIGN KEY`（外键）、`UNIQUE` 和 `CHECK` 约束。
- **最后**，可选地使用 `WITHOUT ROWID` 选项。默认情况下，表中的每一行都有一个隐式列，称为 `rowid`、`oid` 或 `_rowid_` 列。`rowid` 列存储一个 64 位有符号整数键，用于在表内唯一标识该行。如果你不希望 SQLite 创建 `rowid` 列，可以指定 `WITHOUT ROWID` 选项。包含 `rowid` 列的表被称为 `rowid` 表。请注意，`WITHOUT ROWID` 选项仅在 SQLite 3.8.2 或更高版本中可用。

> **注意**：表的主键是一个或一组列，用于唯一标识表中的每一行。

### SQLite CREATE TABLE 示例

假设你需要使用 SQLite 来管理联系人。

每个联系人包含以下信息：

- 名 (First name)
- 姓 (Last name)
- 电子邮件 (Email)
- 电话 (Phone)

要求是电子邮件和电话必须是唯一的。此外，每个联系人可以属于一个或多个组，每个组可以包含零个或多个联系人。

基于这些要求，我们设计了三个表：

- `contacts` 表：存储联系人信息。
- `groups` 表：存储组信息。
- `contact_groups` 表：存储联系人和组之间的关系。

下面的数据库图展示了 `contacts`、`groups` 和 `contact_groups` 这三个表。

*(此处原文有一张名为“SQLite Create Table”的配图)*

以下语句创建 `contacts` 表：

```sql
CREATE TABLE contacts (
	contact_id INTEGER PRIMARY KEY,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
	email TEXT NOT NULL UNIQUE,
	phone TEXT NOT NULL UNIQUE
);
```

`contact_id` 是 `contacts` 表的主键。

因为主键只包含一列，所以你可以使用列约束来定义它。

`first_name` 和 `last_name` 列具有 `TEXT` 存储类型，并且这些列是 `NOT NULL` 的。这意味着在向 `contacts` 表中插入或更新行时，你必须提供这些值。

电子邮件和电话是唯一的，因此我们为每一列都使用了 `UNIQUE` 约束。

以下语句创建 `groups` 表：

```sql
CREATE TABLE groups (
	group_id INTEGER PRIMARY KEY,
	name TEXT NOT NULL
);
```

`groups` 表非常简单，只有两列：`group_id` 和 `name`。`group_id` 列是主键列。

以下语句创建 `contact_groups` 表：

```sql
CREATE TABLE contact_groups(
	contact_id INTEGER,
	group_id INTEGER,
	PRIMARY KEY (contact_id, group_id),
	FOREIGN KEY (contact_id) 
		REFERENCES contacts (contact_id) 
			ON DELETE CASCADE 
			ON UPDATE NO ACTION,
	FOREIGN KEY (group_id) 
		REFERENCES groups (group_id) 
			ON DELETE CASCADE 
			ON UPDATE NO ACTION
);
```

`contact_groups` 表有一个由两列组成的主键：`contact_id` 和 `group_id`。

要添加表主键约束，你可以使用以下语法：

```sql
PRIMARY KEY (contact_id, group_id)
```

此外，`contact_id` 和 `group_id` 是外键。因此，你使用 `FOREIGN KEY` 约束为每一列定义一个外键。

```sql
FOREIGN KEY (contact_id) 
	REFERENCES contacts (contact_id) 
		ON DELETE CASCADE 
		ON UPDATE NO ACTION

FOREIGN KEY (group_id) 
	REFERENCES groups (group_id) 
		ON DELETE CASCADE 
		ON UPDATE NO ACTION
```

> **注意**：我们将在后续教程中详细讨论 `FOREIGN KEY` 约束。

在本教程中，你已经学习了如何使用 SQLite 的 `CREATE TABLE` 语句及各种选项来创建一个新表。



## 参考

- https://www.sqlitetutorial.net/sqlite-create-table/