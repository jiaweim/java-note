# Introduction

- [Introduction](#introduction)
  - [概述](#概述)
  - [JDBC 组成](#jdbc-组成)
  - [JDBC优缺点](#jdbc优缺点)

2020-06-20, 16:53
***

## 概述

JDBC提供执行 SQL 语句以操作关系数据库的手段。

| 缩写 | 英文全称                   | 解释           |
| ---- | -------------------------- | -------------- |
| JDBC | Java DataBase Connectivity | Java数据库连接 |
| SQL  | Structure Query Language   | 结构化查询语言 |
| DBMS | Database Manage System     | 数据库关系系统 |

JDBC 是用于连接和执行数据库操作的 Java API，提供了一种与平台无关的用于执行SQL语句的标准 Java API，可以方便的实现多种关系型数据库的统一操作，它由一组用Java语言编写的类和接口组成。

在实际开发中可以直接使用 JDBC 进行各个数据库的连接与操作，而且可以向数据库发送各种SQL命令。JDBC 提供了一套标准的接口，各个支持JAVA的数据库生产商只要按照此接口提供相应的实现，就可以使用JDBC进行操作。极大的体现了JAVA的可移植性的设计思想。

## JDBC 组成

JDBC API可以分为四部分：

- JDBC Drivers
- Connections
- Statements
- Result Sets

JDBC drivers 是用于连接指定数据库的 java 包，例如 MySQL 具有自定义的 JDBC 驱动。每个JDBC驱动都实现了大量的JDBC接口，在我们使用 JDBC 时，只需要和JDBC的接口打交道，而实现的功能类由JDBC驱动提供。

在JDBC驱动载入后，需要连接数据库，通过 JDBC API 和载入的驱动可以获得 `Connection`实例。对数据库的所有操作都通过Connection实现。

`Statement` 用于执行查询和更新数据库操作。`Statement` 有多种类型，每个statement对应一个查询或更新。

## JDBC优缺点

优点

- JDBC API与微软的ODBC(Open Data Base Connection)十分相似，利于用户理解；
- JDBC 使得编程人员从复杂的驱动器调用命令和函数中解脱出来，可以致力于应用程序中的关键地方；
- JDBC 支持不同的关系数据库，这使得程序的可移植性大大加强；
- JDBC API是面向对象的。

缺点

- 使用JDBC，访问数据记录的速度会受到一定程序的影响；
- JDBC结构中包含不同厂家的产品，这就给更改数据源带来了很大麻烦。
