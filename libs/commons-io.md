# Apache Commons IO

- [Apache Commons IO](#apache-commons-io)
  - [1. 最佳实践](#1-最佳实践)
    - [1.1. java.io.File](#11-javaiofile)
    - [1.2. Buffer Streams](#12-buffer-streams)
  - [2. Utility](#2-utility)
    - [2.1. IOUtils](#21-ioutils)
    - [2.2. FileUtils](#22-fileutils)
    - [2.3. FilenameUtils](#23-filenameutils)
    - [2.4. FileSystemUtils](#24-filesystemutils)
  - [3. Endian](#3-endian)
  - [4. Line Iterator](#4-line-iterator)
  - [5. File Filters](#5-file-filters)
  - [6. File Comparators](#6-file-comparators)
  - [7. Stream](#7-stream)
  - [8. 参考](#8-参考)

2023-08-08, 15:49
add: 完善内容
2022-11-30, 10:31
****
## 1. 最佳实践

### 1.1. java.io.File

我们经常需要处理文件和文件名，可能出错的地方有很多：

- 在 Unix 中可以工作，在 Windows 中不能（反之亦然）
- 文件名因为双分隔符或缺少路径分隔符而无效
- 等等

`java.io.File` 可以很好地处理上述许多情况，因此建议使用 `java.io.File` 作为文件名，而不是 String，以避免平台依赖问题。

commons-io 提供了 `FilenameUtils`，用于处理许多文件名问题，不过仍然建议尽可能使用 `java.io.File` 对象。

例如：

```java
 public static String getExtension(String filename) {
   int index = filename.lastIndexOf('.');
   if (index == -1) {
     return "";
   } else {
     return filename.substring(index + 1);
   }
 }
```

看上去没问题，对不对？如果有人传递了完整路径，例如，"C:\Temp\documentation.new\README" 是一个完全合法的路径，上面定义的方法返回 "new\README"，显然不对。

使用 java.io.File 作为文件名，而不是 String。java.io.File 的功能经过了很好的测试。在 FileUtils 类中可以找到其它有关 java.io.File 的实用函数。

不要用：

```java
String tmpdir = "/var/tmp";
String tmpfile = tmpdir + System.getProperty("file.separator") + "test.tmp";
InputStream in = new java.io.FileInputStream(tmpfile);
```

推荐用：

```java
File tmpdir = new File("/var/tmp");
File tmpfile = new File(tmpdir, "test.tmp");
InputStream in = new java.io.FileInputStream(tmpfile);
```

### 1.2. Buffer Streams

IO 性能很大程度上取决于缓冲策略。通常，读取 512 或 1024 bytes 的数据包非常快，因为这些大小与文件系统或文件系统缓存的大小非常匹配。

在读写流时确保正确地缓冲了流，特别是处理文件时。使用 `BufferedInputStream` 封装 `FileInputStream` 即可：

```java
 InputStream in = new java.io.FileInputStream(myfile);
 try {
   in = new java.io.BufferedInputStream(in);
   
   in.read(.....
 } finally {
   IOUtils.closeQuietly(in);
 }
```

```ad-attention
不要缓冲已经缓冲的流，一些组件（如 XML 解析器）可能已经缓冲，再次缓冲只会降低代码速度。
```

如果使用 commons-io 的 `CopyUtils` 或 `IOUtils`，不需要额外缓冲，方法内已经内嵌的缓冲。

另外，使用 `ByteArrayOutputStream` 写入内存不需要缓冲。

## 2. Utility

### 2.1. IOUtils

`IOUtils` 包含读、写和复制功能，适用于 `InputStream`, `OutputStream`, `Reader` 和 `Writer`。

**示例：** 从 URL 读取字节并 print 的任务

```java
 InputStream in = new URL( "https://commons.apache.org" ).openStream();
 try {
   InputStreamReader inR = new InputStreamReader( in );
   BufferedReader buf = new BufferedReader( inR );
   String line;
   while ( ( line = buf.readLine() ) != null ) {
     System.out.println( line );
   }
 } finally {
   in.close();
 }
```

使用 `IOUtils` 执行相同功能：

```java
 InputStream in = new URL( "https://commons.apache.org" ).openStream();
 try {
   System.out.println( IOUtils.toString( in ) );
 } finally {
   IOUtils.closeQuietly(in);
 }
```

在某些应用领域中，这样的 IO 操作很常见，使用该类可以节省大量时间。

这个方法灵活快速，但是缺点也很明显，如果读取 1GB 文件，会导致创建一个 1GB 的 `String`，占内存。

### 2.2. FileUtils

`FileUtils` 包含处理 `File` 的方法。包括读、写、复制和比较。

**示例：** 逐行读取文件

```java
File file = new File("/commons/io/project.properties");
List lines = FileUtils.readLines(file, "UTF-8");
```

### 2.3. FilenameUtils

FilenameUtils 类提供不使用 File 对象的情况下处理文件名。该类在 Unix 和 Windows 中保持一致。

**示例：** 规范文件名，删除多余的 `..` 号

```java
String filename = "C:/commons/io/../lang/project.xml";
String normalized = FilenameUtils.normalize(filename);
// result is "C:/commons/lang/project.xml"
```

### 2.4. FileSystemUtils

FileSystemUtils 提供 JDK 不支持的文件系统相关功能。目前只有一个获取驱动空闲空间的方法。

```java
long freeSpace = FileSystemUtils.freeSpace("C:/");
```

```ad-warning
java.nio.file.FileStore 提供相同功能，该类处于 Deprecated 状态。
```

## 3. Endian

不同计算机的架构采用不同的字节排序约定。在所谓的 "Little Endian" 架构（如 intel），lower-order 字节在内存中存储在较低地址，后续字节存储在较高内存地址。对 "Big Endian" 架构（如 Motorola），则相反。

在 commons-io 包中有两个相关类：

- EndianUtils 类包含一些静态方法，用于转换 Java 基本类型和 Stream 的 Endian
- SwappedDataInputStream 类实现 DataInput 接口，可以从 non-native Endian 文件读取数据

http://www.cs.umass.edu/~verts/cs32/endian.html

## 4. Line Iterator

`org.apache.commons.io.LineIterator` 提供了逐行读取文件的功能。可以通过 `FileUtils` 或 `IOUtils` 的工厂方法创建该类实例。

推荐的使用模式：

```java
LineIterator it = FileUtils.lineIterator(file, StandardCharsets.UTF_8.name());
try {
    while (it.hasNext()) {
      String line = it.nextLine();
      // do something with line
    }
} finally {
    it.close();
}
```


## 5. File Filters

`org.apache.commons.io.filefilter`  包定义了接口 IOFileFilter，它结合 java.io.FileFilter 和 java.io.FilenameFilter。此外，该包还提供了一系列 `IOFileFilter` 实现，还可以组合 `IOFileFilter`。

## 6. File Comparators

org.apache.commons.io.comparator 包为 java.io.File 提供了许多 java.util.Comparator 实现。这些 comparators 可用于文件排序。

## 7. Stream

org.apache.commons.io.input 和 org.apache.commons.io.output 包含多种 stream 实现。包括：

- Null output stream"
- Tee output stream: 将输出数据发送到两个流，而不是一个流
- Byte array output stream: 比 JDK 版本更快
- Counting stream: 计算传递的字节数
- Proxy stream: 将任务委托给代理方法
- Lockable writer: 使用锁文件提供同步写操作

## 8. 参考

- https://commons.apache.org/proper/commons-io/
