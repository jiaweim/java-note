# JDBC 基础



## 入门

搭建基本的数据库开发环境，并演示如何编译和运行 JDBC 教程的示例代码。

下面演示创建一个数据库，该数据库由一家名为 The Coffee Break（咖啡小憩）的小型咖啡馆老板使用。在这家店里，咖啡豆按磅出售，现煮咖啡按杯出售。

以下步骤将为你配置一个 JDBC 开发环境，你可以使用它来编译和运行教程中的示例代码：

1. 在计算机上安装最新版本的 Java SE SDK
2. 根据需要安装你的数据库管理系统（DBMS）
3. 从数据库供应商处安装对应的 JDBC 驱动程序
4. 安装 Apache Ant
5. 安装 Apache Xalan
6. 下载示例代码
7. 修改 `build.xml` 文件
8. 修改教程的属性文件（properties file）
9. 编译并打包示例代码
10. 创建数据库和表，并向表中填充数据
11. 运行示例代码

## 使用 JDBC 处理 SQL 语句

概述处理任何 SQL 语句所需的步骤。接下来的页面将更详细地描述这些步骤：

通常，要使用 JDBC 处理任何 SQL 语句，你需要遵循以下步骤：

1. 建立连接
2. 创建语句
3. 执行查询
4. 处理 `ResultSet` 对象
5. 关闭连接

本页面使用教程示例中的以下方法 `CoffeesTable.viewTable` 来演示上述步骤。该方法会输出 `COFFEES` 表的内容。在本教程的后续部分，我们将对该方法进行更详细的讨论：

```java
  public static void viewTable(Connection con) throws SQLException {
    String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String coffeeName = rs.getString("COF_NAME");
        int supplierID = rs.getInt("SUP_ID");
        float price = rs.getFloat("PRICE");
        int sales = rs.getInt("SALES");
        int total = rs.getInt("TOTAL");
        System.out.println(coffeeName + ", " + supplierID + ", " + price +
                           ", " + sales + ", " + total);
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
```

**建立连接**

首先，需要与你想要使用的数据源建立连接。数据源可以是数据库管理系统（DBMS）、传统的文件系统，或者任何具有相应 JDBC 驱动程序的其他数据源。这个连接由一个 `Connection` 对象来表示。

**创建语句**

`Statement` 是一个表示 SQL 语句的接口。通过执行 `Statement` 对象，可以生成 `ResultSet` 对象，它代表一个数据库结果集，本质上是一个数据表。要创建 `Statement` 对象，你需要一个 `Connection`（连接）对象。

例如，`CoffeesTable.viewTable` 方法通过以下代码创建了一个 `Statement` 对象：

```java
stmt = con.createStatement();
```

实际上有三种不同类型的语句：

- `Statement`：用于执行不带参数的简单 SQL 语句。
- `PreparedStatement`：（继承自 `Statement`）用于执行预编译的 SQL 语句，这些语句可能包含输入参数。
- `CallableStatement`：（继承自 `PreparedStatement`）用于执行可能同时包含输入和输出参数的存储过程。

这段文本继续深入讲解了 JDBC 的核心操作，包括执行查询、处理结果集以及关闭连接，为您翻译如下：

**执行查询**

要执行查询，可以调用 `Statement` 提供的执行方法，例如以下方法：

- `execute`：如果查询返回的第一个对象是 `ResultSet` 对象，则返回 `true`。当查询可能返回一个或多个 `ResultSet` 对象时，请使用此方法。通过反复调用 `Statement.getResultSet` 方法，可以获取查询返回的 `ResultSet` 对象。
- `executeQuery`：返回一个 `ResultSet` 对象。
- `executeUpdate`：返回一个整数，表示受该 SQL 语句影响的行数。如果你使用的是 `INSERT`、`DELETE` 或 `UPDATE` SQL 语句，请使用此方法。

例如，`CoffeesTable.viewTable` 方法通过以下代码执行了一个 `Statement` 对象：

```java
ResultSet rs = stmt.executeQuery(query);
```

**处理 ResultSet 对象**

你可以通过游标（Cursor）来访问 `ResultSet` 对象中的数据。请注意，这里的游标不是数据库游标，而是一个指向 `ResultSet` 对象中某一行数据的指针。最初，游标定位在第一行之前。你需要调用 `ResultSet` 对象中定义的各种方法来移动游标。

例如，`CoffeesTable.viewTable` 方法会反复调用 `ResultSet.next` 方法，将游标向前移动一行。每次调用 `next` 时，该方法都会输出游标当前所在行的数据：

```java
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String coffeeName = rs.getString("COF_NAME");
        int supplierID = rs.getInt("SUP_ID");
        float price = rs.getFloat("PRICE");
        int sales = rs.getInt("SALES");
        int total = rs.getInt("TOTAL");
        System.out.println(coffeeName + ", " + supplierID + ", " + price +
                           ", " + sales + ", " + total);
      }
      // ...
```

**关闭连接（Closing Connections）**

当你使用完 `Connection`、`Statement` 或 `ResultSet` 对象后，应调用其 `close` 方法以立即释放它所占用的资源。

或者，你可以使用 `try-with-resources` 语句来自动关闭 `Connection`、`Statement` 和 `ResultSet` 对象，无论是否抛出了 `SQLException`。自动资源声明语句由一个 `try` 语句和一个或多个声明的资源组成。例如，`CoffeesTable.viewTable` 方法会自动关闭其 `Statement` 对象，代码如下：

```java
  public static void viewTable(Connection con) throws SQLException {
    String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String coffeeName = rs.getString("COF_NAME");
        int supplierID = rs.getInt("SUP_ID");
        float price = rs.getFloat("PRICE");
        int sales = rs.getInt("SALES");
        int total = rs.getInt("TOTAL");
        System.out.println(coffeeName + ", " + supplierID + ", " + price +
                           ", " + sales + ", " + total);
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
```

以下语句是一个 `try-with-resources` 语句，它声明了一个资源 `stmt`，该资源将在 `try` 代码块结束时被自动关闭：

```java
    try (Statement stmt = con.createStatement()) {
      // ...
    }
```

### 建立连接

首先，你需要与想要使用的数据源建立连接。数据源可以是数据库管理系统（DBMS）、传统的文件系统，或任何具有相应 JDBC 驱动程序的其他数据源。通常，JDBC 应用程序会使用以下两个类之一来连接到目标数据源：

- **DriverManager**：通过指定的数据库 URL 将应用程序连接到数据源。当该类首次尝试建立连接时，它会自动加载在类路径（class path）中找到的任何 JDBC 4.0 驱动程序。请注意，对于 4.0 之前的版本，你的应用程序必须手动加载 JDBC 驱动程序。
- **DataSource**：相比于 `DriverManager`，更推荐使用这个接口，因为它能让底层数据源的细节对应用程序保持透明。通过设置 `DataSource` 对象的属性，可以使其代表一个特定的数据源。

**注意**：本教程中的示例使用的是 `DriverManager` 类而不是 `DataSource` 类，因为 `DriverManager` 更易于使用，且示例不需要 `DataSource` 类的特有功能。



### 使用 DataSource 进行连接

演示如何使用 `DataSource` 对象连接到数据库，这是获取数据源连接的首选方式。

### 处理 SQLException

演示如何处理由数据库错误引起的异常。

## 设置表

介绍 JDBC 教程示例中使用的所有数据库表，以及如何使用 JDBC API 和 SQL 脚本创建和填充这些表。

### 从 ResultSet 中检索和修改值

完善配置数据库、发送查询以及从数据库中检索数据的流程。

### 使用预编译语句

介绍一种创建数据库查询的更灵活的方法。

### 使用事务

演示如何控制数据库查询的实际执行时机。

## RowSet

与 `ResultSet` 相比，`RowSet` 对象更灵活性、更易于使用的方式保存表格数据。接下来的页面将介绍可用的不同类型 `RowSet` 对象。

### JdbcRowSet

### CachedRowSet

### JoinRowSet

### FilteredRowSet

### WebRowSet

## 高级数据类型

### 大对象

### SQLXML

### Array

### DISTINCT 数据类型

### 结构化对象

### 自定义类型映射

### Datalink

### RowId

## 使用存储过程

## 结合 GUI API 使用 JDBC