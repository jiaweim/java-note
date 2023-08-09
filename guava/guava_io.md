# Guava I/O 工具

## ByteStream 和 CharStream

Guava 使用 "stream" 指在底层资源中具有位置状态的 I/O 数据的 `Closeabe` stream：

- "byte stream"  指 `InputStream` 或 `OutputStream`
- "char stream" 指 `Reader` 或 `Writer`

相应的工具分为 `ByteStreams` 和 `CharStreams`。

Guava 大多数与 stream 相关的工具要么一次处理整个 stream，要么自定义缓冲以提高效率。

```ad-note
Guava 方法不会关闭 stream，关闭 stream 的是打开 stream 的代码的责任。
```

这两个类提供的方法：

|ByteStreams|CharStreams|
|---|---|
|`byte[] toByteArray(InputStream)`|`String toString(Readable)`|
|N/A|`List<String> readLines(Readable)`|
|`long copy(InputStream, OutputStream)`|`long copy(Readable, Appendable)`|
|`void readFully(InputStream, byte[])`|N/A|
|`void skipFully(InputStream, long)`|`void skipFully(Reader, long)`|
|`OutputStream nullOutputStream()`|`Writer nullWriter()`|

## Source 和 Sink

对一些基本操作，往往会创建一些工具方法避免处理 stream。如 Guava 的 Files.toByteArray(File) 和 Files.write(File, byte[])。这样会导致类似的方法在各处使用，用于处理不同类型的数据源或可写入的目标（sink）。例如，Guava 的 `Resources.toByteArray(URL)` 功能与 `Files.toByteArray(File)` 一样，只是使用 URL 作为数据源，而非文件。

为了解决该问题，Guava 对不同类型的数据源和 sink 进行了抽象。source 或 sink 表示资源，你知道如何打开一个新的 stream，如 File 或 URL。

source 是可读的，sink 是可写入的。

此外，source 和 sink 会根据处理的是字节数据还是字符数据进行分类。

|操作|Byte|Char|
|---|---|---|
|读|ByteSource|CharSource|
|写|ByteSink|CharSink|

这些 API 的优点是提供了一组通用的操作。例如，将数据源封装为 ByteSource 后，不管数据源是什么，都可以获得相同的方法集合。

### 创建 Source 和 Sink

Guava 提供了许多 Source 和 Sink 实现

|**Bytes**|**Chars**|
|:--|:--|
|`Files.asByteSource(File)`|`Files.asCharSource(File, Charset)`|
|[`Files.asByteSink(File, FileWriteMode...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/Files.html#asByteSink-java.io.File-com.google.common.io.FileWriteMode...-)|[`Files.asCharSink(File, Charset, FileWriteMode...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/Files.html#asCharSink-java.io.File-java.nio.charset.Charset-com.google.common.io.FileWriteMode...-)|
|[`MoreFiles.asByteSource(Path, OpenOption...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/MoreFiles.html#asByteSource-java.nio.file.Path-java.nio.file.OpenOption...-)|[`MoreFiles.asCharSource(Path, Charset, OpenOption...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/MoreFiles.html#asCharSource-java.nio.file.Path-java.nio.charset.Charset-java.nio.file.OpenOption...-)|
|[`MoreFiles.asByteSink(Path, OpenOption...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/MoreFiles.html#asByteSink-java.nio.file.Path-java.nio.file.OpenOption...-)|[`MoreFiles.asCharSink(Path, Charset, OpenOption...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/MoreFiles.html#asCharSink-java.nio.file.Path-java.nio.charset.Charset-java.nio.file.OpenOption...-)|
|[`Resources.asByteSource(URL)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/Resources.html#asByteSource-java.net.URL-)|[`Resources.asCharSource(URL, Charset)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/Resources.html#asCharSource-java.net.URL-java.nio.charset.Charset-)|
|[`ByteSource.wrap(byte[])`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/ByteSource.html#wrap-byte:A-)|[`CharSource.wrap(CharSequence)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/CharSource.html#wrap-java.lang.CharSequence-)|
|[`ByteSource.concat(ByteSource...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/ByteSource.html#concat-com.google.common.io.ByteSource...-)|[`CharSource.concat(CharSource...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/CharSource.html#concat-com.google.common.io.CharSource...-)|
|[`ByteSource.slice(long, long)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/ByteSource.html#slice-long-long-)|N/A|
|[`CharSource.asByteSource(Charset)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/CharSource.html#asByteSource-java.nio.charset.Charset-)|[`ByteSource.asCharSource(Charset)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/ByteSource.html#asCharSource-java.nio.charset.Charset-)|
|N/A|[`ByteSink.asCharSink(Charset)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/io/ByteSink.html#asCharSink-java.nio.charset.Charset-)|

## 使用 Source 和 Sink

## Files

