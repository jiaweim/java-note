# Transaction

## 简介

事务（transaction）表示一组操作作为单个工作单元执行。这意味着这一组操作中任意操作失败，SQLite 将终止其他操作，并将数据回滚到初始状态。

事务具有四个属性，称为 ACID：

- A，表示原子性（atomicity）。即每个事务执行要么全部执行，要么全不执行，事务中的任何操作失败，数据库都保持不变。
- C，表示一致性（consistency）。任何事务都将数据库中的数据从一种有效状态转移到另一种有效状态。
- I，表示分离（isolation）。用于并发控制，确保事务的所有并发执行产生的结果与顺序执行一致。
- D，表示稳定（durability）。事务提交后，它将保持完整。

## Java SQLite transaction

连接到 SQLite 数据库，默认模式为 `auto-commit`，即对数据库的每个操作都自动提交。

使用 `setAutoCommit()` 禁用自动提交：

```java
conn.setAutoCommit(false);
```

使用 `Connection` 的 `commit()` 提交工作：

```java
conn.commit();
```

在事务中出现失败，可以使用 `rollback()` 回滚事务：

```java
conn.rollback();
```

