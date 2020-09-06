# Alter

## 简介

`ALTER TABLE` 语句，可以用于：

- 重命名表格
- 添加新的 column
- 重命名 column

## 重命名表格

```sql
ALTER TABLE database_name.table_name RENAME TO new_table_name;
```

## 添加 column

语法：

```sql
ALTER TABLE table_name ADD COLUMN column_definition;
```

使用限制：

- 新添加的column不能有 `UNIQUE` 或 `PRIMARY KEY` 限制
- 如果新 column 有 `NOT NULL` 限制，则必须为其提供默认值，而不能使用NULL
- 新 column 不能有默认的 `CURRENT_TIMESTAMP`, `CURRENT_DATE`, `CURRENT_TIME`。

