# SQLite JDBC Usage

2026-09-07⭐
@author Jiawei Mao
***
## 如何指定数据库文件

以下是建立到数据库文件 `C:\work\mydatabase.db`（在 Windows 系统中）连接的示例：

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/work/mydatabase.db")) { /*...*/ }
```

打开 UNIX 系统（如 Linux、macOS 等）下的文件 `/home/leo/work/mydatabase.db`：

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:/home/leo/work/mydatabase.db")) { /*...*/ }
```

## 如何使用内存数据库或临时数据库

SQLite 支持内存数据库，这种数据库不会创建任何数据库文件。要在 Java 代码中使用内存数据库，可以通过以下方式获取数据库连接：

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) { /*...*/ }
```

你可以通过以下方式创建一个临时数据库：

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:")) { /*...*/ }
```

## 如何使用在线备份与恢复功能

将整个数据库备份到 `backup.db` 文件中：

```java
try (
    // 创建一个内存数据库
    Connection conn = DriverManager.getConnection("jdbc:sqlite:");
    Statement stmt = conn.createStatement();
) {
    // 执行一些更新操作
    stmt.executeUpdate("create table sample(id, name)");
    stmt.executeUpdate("insert into sample values(1, \"leo\")");
    stmt.executeUpdate("insert into sample values(2, \"yui\")");
    
    // 将数据库内容导出（备份）到文件中
    stmt.executeUpdate("backup to backup.db");
}
```

从备份文件中恢复数据库：

```java
try (
    // 创建一个内存数据库
    Connection conn = DriverManager.getConnection("jdbc:sqlite:");
    // 从备份文件中恢复数据库
    Statement stat = conn.createStatement();
) {
    stat.executeUpdate("restore from backup.db");
}
```

## 创建 BLOB 数据

1. 创建一个包含 BLOB 类型列的表：`create table T (id integer, data blob)`
2. 创建一个带有 `?` 占位符的预处理语句（`PreparedStatement`）：`insert into T values(1, ?)`
3. 准备一个字节数组形式的 BLOB 数据（例如：`byte[] data = ...`）
4. 设置参数：`preparedStatement.setBytes(1, data)`
5. 执行语句：`preparedStatement.execute()...`

这段文本主要讲解了如何从类路径（Classpath）或网络位置读取只读的 SQLite 数据库文件，为您翻译如下：

## 从类路径或网络读取数据库文件（只读）

要加载可以通过类加载器（ClassLoader）找到的数据库文件（例如，位于类路径下的 JAR 包内的数据库文件），请使用 `jdbc:sqlite::resource:` 前缀。

例如，以下是访问位于 Java 包 `org.yourdomain` 中的 SQLite 数据库文件 `sample.db` 的示例：

```java
try (Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:org/yourdomain/sample.db")) { /*...*/ }
```

此外，也可以使用外部数据库资源，如下所示：

```java
try (Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:http://www.xerial.org/svn/project/XerialJ/trunk/sqlite-jdbc/src/test/java/org/sqlite/sample.db")) { /*...*/ }
```

要访问特定 JAR 文件（本地或远程）内部的数据库文件，请使用 JAR URL：

```java
try (Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:jar:http://www.xerial.org/svn/project/XerialJ/trunk/sqlite-jdbc/src/test/resources/testdb.jar!/sample.db")) { /*...*/ }
```

数据库文件将被提取到由 `System.getProperty("java.io.tmpdir")` 指定的临时文件夹中。

这段文本主要讲解了在 Java 中使用 `sqlite-jdbc` 时，如何自定义原生库（Native Library）的提取路径、指定特定版本以及覆盖系统架构检测。为您翻译如下：

## 配置原生库的提取目录

`sqlite-jdbc` 默认会将适用于您操作系统的原生库提取到由 JVM 属性 `java.io.tmpdir` 指定的目录中。如果需要使用其他目录，请将 JVM 属性 `org.sqlite.tmpdir` 设置为您期望的路径。

## 如何使用特定的原生库

您可以通过设置以下 JVM 属性来使用特定版本的原生库：
`-Dorg.sqlite.lib.path=/path/to/folder`
`-Dorg.sqlite.lib.name=your-custom.dll`

## 覆盖检测到的系统架构

如果系统检测到的架构不正确，导致加载了错误的原生库，您可以通过设置以下 JVM 属性来覆盖该检测值：
`-Dorg.sqlite.osinfo.architecture=arm`

这段文本主要讲解了如何配置 SQLite 数据库连接，以及如何使用第三方驱动来操作加密数据库。为您翻译如下：

## 配置数据库连接

```java
SQLiteConfig config = new SQLiteConfig();
// config.setReadOnly(true);   // 设置为只读
config.setSharedCache(true);  // 开启共享缓存
config.recursiveTriggers(true); // 开启递归触发器
// ... 还可以通过 SQLiteConfig 对象进行其他配置
try (Connection conn = DriverManager.getConnection("jdbc:sqlite:sample.db", config.toProperties())) { /*...*/ }
```

## 如何使用加密数据库

**重要提示**：`xerial/sqlite-jdbc` 原生并不自带加密支持，你需要引入特殊的 `.dll` 或 `.so` 动态链接库文件。

SQLite 支持通过特殊的驱动程序和密钥来加密数据库。要使用加密数据库，你需要使用支持通过 `pragma key` 或 `pragma hexkey` 指令进行加密的驱动，例如 SQLite SSE 或 SQLCipher。你需要通过直接引用 `.dll/.so` 文件来指定这些驱动：

`-Dorg.sqlite.lib.path=.`
`-Dorg.sqlite.lib.name=sqlite_cryption_support.dll`

配置好驱动后，你只需要在建立连接时指定密码即可：

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite", "", "password")) { /*...*/ }
```

这段文本主要讲解了在连接加密的 SQLite 数据库时，如何以二进制形式传递密码。为您翻译如下：

### 二进制密码

如果你需要以二进制形式提供密码，则必须指定底层的 `.dll/.so` 库期望的格式。目前有两种不同的模式可供选择：

- **SSE**
  二进制密码通过 `pragma hexkey='AE...'` 语句提供。
- **SQLCipher**
  二进制密码通过 `pragma key="x'AE...'"` 语句提供。

你可以在数据库连接字符串级别设置该模式：

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite?hexkey_mode=sse", "", "AE...")) { /*...*/ }
```

这段文本主要讲解了在 SQLite 中获取自动生成键（Generated Keys）的机制及如何关闭该功能以优化性能。为您翻译如下：

## 获取生成的键（Generated Keys）

SQLite 对获取生成的键的支持是有限的，它依赖于 `last_insert_rowid`，并存在以下限制：

- 即使添加或更新了多行数据，也只能检索到一个 ID。
- 必须在执行 SQL 语句后**立即**调用。

默认情况下，驱动程序会在每条语句执行后急切地（立即）检索生成的键，这可能会对性能产生影响。

你可以通过以下 3 种方式禁用生成键的检索功能：

1. 通过 `SQLiteDataSource#setGetGeneratedKeys(false)`
2. 通过 `SQLiteConnectionConfig#setGetGeneratedKeys(false)`
3. 使用 `jdbc.get_generated_keys` pragma 参数：

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:?jdbc.get_generated_keys=false")) { /*...*/ }
```

## 参考

- https://github.com/xerial/sqlite-jdbc/blob/master/USAGE.md