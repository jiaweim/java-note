# Select

- [Select](#select)
  - [简介](#简介)
  - [带参数查询](#带参数查询)
  - [简单查询](#简单查询)

2020-06-20, 13:16
***

## 简介

`SELECT` 语句是 SQL 中最常用的语句。SQLite 的 `SELECT` 语句提供了标准 SQL 的 `SELECT` 语句的所有功能。语法：

```sql
SELECT column1, column2, columnN FROM table_name;
```

这里 `column1`, `column2` 等是表格的字段，对应你要查询的值。如果要获得表格的所有值，可以使用：

```sql
SELECT * FROM table_name;
```

使用 SQLite-jdbc 执行 `SELECT` 的步骤：

1. 连接数据库，获得 `Connection` 对象
2. 创建 `Statement`
3. 使用 `Statement` 的 `executeQuery` 方法创建 `ResultSet`
4. 使用 `ResultSet` 的 `next()` 方法查看结果
5. 使用 `ResultSet` 对象的 `getInt()`, `getString()`, `getDouble()` 等方法查看数据。

例如：

```java
String sql = "SELECT id, name, capacity FROM warehouses";

try (Connection conn = this.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

    // loop through the result set
    while (rs.next()) {
        System.out.println(rs.getInt("id") + "\t" +
                rs.getString("name") + "\t" +
                rs.getDouble("capacity"));
    }
} catch (SQLException e) {
    System.out.println(e.getMessage());
}
```

## 带参数查询

如果要使用参数查询，可以使用 `PreparedStatement`。

例如，选择 `warehouse` 中 `capacity` 大于指定值的结果：

```java
@Test
void testSelectGreater()
{
    getCapacityGreaterThan(3600);
}

/**
    * Get the warehouse whose capacity greater than a specified capacity
    */

void getCapacityGreaterThan(double capacity)
{
    String sql = "SELECT id, name, capacity "
            + "FROM warehouses WHERE capacity > ?";

    try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // set the value
        pstmt.setDouble(1, capacity);
        ResultSet rs = pstmt.executeQuery();

        // loop through the result set
        while (rs.next()) {
            System.out.println(rs.getInt("id") + "\t" +
                    rs.getString("name") + "\t" +
                    rs.getDouble("capacity"));
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

## 简单查询

```java

```
