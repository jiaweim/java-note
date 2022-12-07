# Apache Commons IO

- [Apache Commons IO](#apache-commons-io)
  - [Utility](#utility)
  - [Endian](#endian)
  - [Line Iterator](#line-iterator)
  - [File Filters](#file-filters)
  - [File Comparators](#file-comparators)
  - [Stream](#stream)
  - [参考](#参考)

Last updated: 2022-11-30, 10:31
***

## Utility

- **IOUtils**

`IOUtils` 包含读、写和复制功能，适用于 `InputStream`, `OutputStream`, `Reader` 和 `Writer`。

例如，从 URL 读取字节并 print 的任务：

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

这个方法灵活快速，但是缺点也很明显，如果读取 1GB 文件，会导致创建一个 1GB 的 `String`，占内存啊。

- **FileUtils**

`FileUtils` 包含处理 `File` 的方法。包括读、写、复制和比较。

例如，逐行读取文件：

```java
File file = new File("/commons/io/project.properties");
List lines = FileUtils.readLines(file, "UTF-8");
```

## Endian

## Line Iterator

## File Filters

## File Comparators

## Stream

## 参考

- https://commons.apache.org/proper/commons-io/
