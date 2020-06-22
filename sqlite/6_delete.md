# Deleting Data

## 简介

从数据库表格中删除行的步骤：

1. 连接数据库
2. 准备 `DELETE` 语句，如果需要参数，可以使用占位符 `?`
3. 使用 `prepareStatement()` 创建 `PreparedStatement`
4. 使用 `PreparedStatement` 的 `setInt()`, `setString()` 等方法设置占位符对应参数
5. 调用 `executeUpdate()` 方法指定 `DELETE` 语句

例如，从 `warehouses` 表格中删除 id 为 3 的行：

```java
/**
    * Delete a warehouse specified by the id
    */
void delete(int id)
{
    String sql = "DELETE FROM warehouses WHERE id = ?";

    try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // set the corresponding param
        pstmt.setInt(1, id);
        // execute the delete statement
        pstmt.executeUpdate();

    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

@Test
void testDelete()
{
    delete(3);
}
```
