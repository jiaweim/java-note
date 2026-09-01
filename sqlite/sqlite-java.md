# SQLite Java

## 简介

下面介绍如何使用 Java JDBC API 与 SQLite 进行交互。

在 Java 中，有多种接口可用于与 SQLite 交互。其中一些是对原生 C API 的封装，而另一些则实现了标准的 Java 数据库连接（JDBC）API。

下面介绍现代的 SQLiteJDBC 包，这是一款专为 SQLite 打造的 JDBC 驱动程序。该 SQLiteJDBC 包不仅包含了相关的 Java 类，还内置了适用于 Windows、macOS 和 Linux 系统的原生 SQLite 库。

## 1. 连接 SQLite


### 下载 SQLite JDBC 驱动

2026-09-01：**https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc**

当前最新 SQLite JDBC

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.53.4.0</version>
    <scope>compile</scope>
</dependency>
```

sqlite-jdbc 包含 SQLite 和 Java 接口类。

### 连接格式

可以使用如下格式在 SQLite JDBC 中连接 SQLite 数据库文件：

```java
jdbc:sqlite:sqlite_database_file_path
```

其中 `sqlite_database_file_path` 是 SQLite 数据库文件的路径，可以是相对路径：

```txt
jdbc:sqlite:sample.db
```

也可以是绝对路径：

```txt
jdbc:sqlite:C:/sqlite/db/chinook.db
```

也可以连接内存中数据库：

```txt
jdbc:sqlite::memory:
```

### 使用 JDBC 连接数据库

连接 `chinook.db` 文件指向的数据库：

```java
public static void connect() {
    // connection string
    String url = "jdbc:sqlite:C:/tool/sqlite/chinook.db";
    try (var conn = DriverManager.getConnection(url)) {
        System.out.println("Connection to SQLite has been established.");
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

样例数据库 `chinook.db` 包含 13 个表格。

![chinook.db](images/2020-06-20-16-07-42.png)

### 创建新的数据库

在 Java 中创建 SQLite 数据库分两步：

1. 指定数据库文件名称和路径
2. 通过 SQLite JDBC driver 连接数据库

当连接的数据库不存在，SQLite会自动创建一个新的数据库。所以，创建新的数据库，只需要指定数据库名称，然后连接即可，和连接数据库语法一样。

例如，创建一个 `test.db` 数据库：

```java
String url = "jdbc:sqlite:D:\\code\\test\\test.db";
try (Connection conn = DriverManager.getConnection(url)) {
    if (conn != null) {
        DatabaseMetaData metaData = conn.getMetaData();
        assertEquals(metaData.getDriverName(), "SQLite JDBC");
    }
} catch (SQLException e) {
    System.out.println(e.getMessage());
}
```


## 2. Create Table

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

## 3. 插入数据

使用 `INSERT` 语句插入数据到表格，步骤如下：

1. 连接 SQLite 数据库
2. 准备 `INSERT` 语句。如果语句中包含参数，请使用 `?` 占位符
3. 使用 `Connection` 对象创建 `PreparedStatement`
4. 使用 `PreparedStatement` 的 `setInt()`, `setString()` 等方法为每个占位符设置对应值
5. 执行 `executeUpdate()` 方法

例如，下面在 `warehouses` 中插入三行：

```java
 @Test
void testInsert(){
    insert("Raw Materials", 3000);
    insert("Semifinished Goods", 4000);
    insert("Finished Goods", 5000);
}

private Connection connect(){
    String url = "jdbc:sqlite:D:\\code\\test\\tests.db";
    Connection con = null;
    try {
        con = DriverManager.getConnection(url);
    } catch (SQLException throwables) {
        System.out.println(throwables.getMessage());
    }
    return con;
}

void insert(String name, double value){
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
