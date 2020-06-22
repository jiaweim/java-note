# Transaction

- [Transaction](#transaction)
  - [简介](#简介)
  - [database transaction](#database-transaction)
  - [transaction 实例](#transaction-实例)

2020-06-20, 15:25
***

## 简介

在介绍前，先创建两个表格：

1. `materials` 表格包含 materials master
2. `inventory` 包含 `warehouses` 和 `materials` 关系

```sql
CREATE TABLE IF NOT EXISTS materials (
    id integer PRIMARY KEY,
    description text NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory (
    warehouse_id integer,
    material_id integer,
    qty real,
    PRIMARY KEY (warehouse_id, material_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses (id),
    FOREIGN KEY (material_id) REFERENCES materials (id)
);
```

## database transaction

事务（transaction）表示作为单个工作单元执行的一组操作。操作集合中任意操作失败，SQLite 中止其它操作，并将数据回滚到初始状态。

事务有四个主要属性，称为 ACID：

- 原子性（**A**tomicity）。每个事务要么成功，要么失败，如果失败，数据库状态不变。
- 一致性（**C**onsistency）。保证任何事务都将数据库从一个有效状态转换到另一个有效状态。
- 分离（**I**solation）。用于并发控制，确保事务的并发执行结果和顺序执行一样。
- 耐用性（**D**urability），事务提交后保持完整，遇到任何错误（如断电）都保持完整。

## transaction 实例

连接到 SQLite 数据库后，默认模式为 `auto-commit`。即提交到 SQLite 数据库的每个查询都自动提交。

设置 `setAutoCommit()` 可以取消自定提交：

```java
conn.setAutoCommit(false);
```

使用 `Connection` 的 `commit()` 方法提交：

```java
conn.commit();
```

在事务中出现错误，可以使用 `rollback()` 回滚事务：

```java
conn.rollback();
```
