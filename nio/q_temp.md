# 创建临时文件

- [创建临时文件](#创建临时文件)
  - [IO 方法](#io-方法)
  - [NIO 方法](#nio-方法)

2021-02-05, 16:28
***

## IO 方法

`java.io.File.createTempFile(String prefix, String suffix, File directory)` 在指定目录创建临时文件，调用 `deleteOnExit()` 可以删除由该方法创建的文件。

```java
public static File createTempFile(String prefix, String suffix, File directory)
```

参数说明：

- `prefix` 定义文件名，要求至少3个字符，如果太长会自动截断；
- `suffix` 定义文件后缀，如果为 `null`，则以 `.tmp` 替代；
- `directory` 是临时文件所在目录，如果指定的目录不存在，抛出 `IOException`。

文件名由 `prefix` + “内部随机生成字符（至少5个）”+`suffix` 组成。

如果 `directory` 为 null，则使用默认目录，其位置由 `java.io.tmpdir` 系统属性确定。例如在 Windows 10 系统上：

```java
System.out.println(System.getProperty("java.io.tmpdir"));
// C:\Users\<username>\AppData\Local\Temp\
```

> 这个方法一般和 `deleteOnExit` 配套使用，在使用完临时文件后删除之。

使用示例：

```java
File f = File.createTempFile("tmp", ".txt", new File("D:\\data"));
System.out.println("File path: " + f.getAbsolutePath()); 
// File path: D:\data\tmp9662249449050851548.txt

// deletes file when the virtual machine terminate
f.deleteOnExit();

// creates temporary file, if suffix is null, "tmp" is used.
f = File.createTempFile("tmp", null, new File("D:/"));
System.out.println("File path: " + f.getAbsolutePath());
// File path: D:\tmp16530246932214752759.tmp

// deletes file when the virtual machine terminate
f.deleteOnExit();

// if the directory is null, default temp folder is used (AppData\Local\Temp for Windows)
f = File.createTempFile("tmp", null, null);
System.out.println(f.getAbsolutePath());
// C:\Users\happy\AppData\Local\Temp\tmp1426089657789007888.tmp
f.deleteOnExit();
```

## NIO 方法

`java.nio.file.Files` 类同样提供了创建临时文件的方法:

```java
public static Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs)
```
方法签名和 `File.createTempFile()` 类似，使用 `prefix` 和 `suffix` 生成临时文件名的规则基本也一致。

在使用创建的临时文件时，使用 `StandardOpenOption.DELETE_ON_CLOSE` 选项打开，在使用后调用合适的 close 方法可以保证该文本被删除。

例1：创建指定后缀的临时文件

```java
Path tempFile = Files.createTempFile("happy", ".lucky");
System.out.println(tempFile);
// C:\Users\happy\AppData\Local\Temp\happy5418717743945248114.lucky
```

在默认临时文件夹创建临时文件。

例2：不指定前缀

```java
Path tempFile2 = Files.createTempFile(null, ".lucky");
System.out.println(tempFile2);
// C:\Users\happy\AppData\Local\Temp\5335753237959736973.lucky
```

不指定前缀，对应的临时文件名全部为随机生成字符。

例3：不指定后缀

```java
Path tempFile3 = Files.createTempFile("happy", null);
System.out.println(tempFile3);
// C:\Users\happy\AppData\Local\Temp\happy851462428616511214.tmp
```

不指定后缀，使用默认后缀 `.tmp`。

例4：指定空后缀

```java
Path tempFile4 = Files.createTempFile("happy", "");
System.out.println(tempFile4);
// C:\Users\happy\AppData\Local\Temp\happy1219941913418435852
```

指定空后缀，则没有后缀。

