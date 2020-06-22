# Operations

- [Operations](#operations)
  - [Create Table](#create-table)
  - [插入数据](#插入数据)

2020-06-20, 10:17
***

## Create Table

创建表格步骤：

1. 准备好 `CREATE TABLE` 语句
2. 连接数据库
3. 使用 `Connection` 对象创建 `Statement`
4. 使用 `Statement` 的 `executeUpdate()` 方法执行 `CREATE TABLE` 语句

例如：

```java
String url = "jdbc:sqlite:D:\\code\\test\\tests.db";
String sql = "CREATE TABLE IF NOT EXISTS warehouses (" +
        "id integer PRIMARY KEY, " +
        "name text NOT NULL, " +
        "capacity real);";
try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement()) {
    stmt.execute(sql);
} catch (SQLException e) {
    e.printStackTrace();
}
```

## 插入数据

使用 `INSERT` 语句插入数据到表格，步骤如下：

1. 连接数据库
2. 准备 `INSERT` 语句
3. 使用 `Connection` 对象创建 `PreparedStatement`
4. 使用 `PreparedStatement` 的 `setInt()`, `setString()` 等方法设置对应值
5. 执行 `executeUpdate()` 方法

例如，下面在 `warehouses` 中插入三行：

```java
 @Test
void testInsert()
{
    insert("Raw Materials", 3000);
    insert("Semifinished Goods", 4000);
    insert("Finished Goods", 5000);

}

private Connection connect()
{
    String url = "jdbc:sqlite:D:\\code\\test\\tests.db";
    Connection con = null;
    try {
        con = DriverManager.getConnection(url);
    } catch (SQLException throwables) {
        System.out.println(throwables.getMessage());
    }
    return con;
}

void insert(String name, double value)
{
    String sql = "INSERT INTO warehouses(name,capacity) VALUES(?,?)";

    try (Connection connection = connect();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, name);
        pstmt.setDouble(2, value);
        pstmt.executeUpdate();
    } catch (SQLException throwables) {
        System.out.println(throwables.getMessage());
    }
}
```

