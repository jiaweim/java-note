# Update Data

- [Update Data](#update-data)
  - [简介](#简介)

2020-06-20, 14:26
***

## 简介

使用 JDBC 更新表格数据的步骤如下：

1. 连接数据库
2. 准备 `UPDATE` 语句。如果需要参数，可以使用 `?` 占位符
3. 通过 `Connection` 的 `prepareStatement()` 方法创建 `PreparedStatement`
4. 使用 `PreparedStatement` 的 `setString()`, `setInt()` 等方法设置占位符对应的参数
5. 指定 `executeUpdate()` 方法

下面使用该方法更新 `warehouses` 表格的数据：

```java
void update(int id, String name, double capacity)
{
    String sql = "UPDATE warehouses SET name = ? , "
            + "capacity = ? "
            + "WHERE id = ?";
    try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, name);
        pstmt.setDouble(2, capacity);
        pstmt.setInt(3, id);
        pstmt.executeUpdate();
    } catch (SQLException throwables) {
        System.out.println(throwables.getMessage());
    }
}

@Test
void testUpdate()
{
    update(3, "Finished Products", 5500);
}
```
