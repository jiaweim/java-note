# kryo

@author Jiawei Mao
***
## 简介

Kryo 是一个快速、高效的 Java 二进制 graph 序列化框架。其目标的实现快速、尺寸小、易于使用的 API。

Kryo 支持自动深度或浅复制。

## 安装

Kryo 发布了两种 artifacts:

- 默认 jar（带有依赖库），直接在应用程序而不是库中使用
- 不带依赖的 jar 版本，提供其它库使用。

### Maven

application 中使用：

```xml
<dependency>
   <groupId>com.esotericsoftware</groupId>
   <artifactId>kryo</artifactId>
   <version>5.6.0</version>
</dependency>
```

library 中使用：

```xml
<dependency>
   <groupId>com.esotericsoftware.kryo</groupId>
   <artifactId>kryo5</artifactId>
   <version>5.6.0</version>
</dependency>
```

使用最新 snapshot 版本：

```xml
<repository>
   <id>sonatype-snapshots</id>
   <name>sonatype snapshots repo</name>
   <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>

<!-- for usage in an application: -->
<dependency>
   <groupId>com.esotericsoftware</groupId>
   <artifactId>kryo</artifactId>
   <version>5.6.1-SNAPSHOT</version>
</dependency>
<!-- for usage in a library that should be published: -->
<dependency>
   <groupId>com.esotericsoftware.kryo</groupId>
   <artifactId>kryo5</artifactId>
   <version>5.6.1-SNAPSHOT</version>
</dependency>
```
## 快速入门

直接展示如何使用该库：

```java
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class HelloKryo {
    public static void main(String[] args) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.register(SomeClass.class);

        SomeClass object = new SomeClass();
        object.value = "Hello Kryo!";

        Output output = new Output(new FileOutputStream("file.bin"));
        kryo.writeObject(output, object);
        output.close();

        Input input = new Input(new FileInputStream("file.bin"));
        SomeClass object2 = kryo.readObject(input, SomeClass.class);
        input.close();
    }

    public static class SomeClass {
        String value;
    }
}
```

`Kryo` 类自动执行序列化。Output 和 Input 类处理缓冲字节和 flush 操作。

本文余下部分详细介绍 Kryo 的工作原理和高级用法。

## IO

使用 `Input` 和 `Output` 类在 Kryo 中获取数据。这些类都不是线程安全的。

### Output

`Output` 类是一个 `OutputStream`，它将数据写入 byte-array buffer。可以获取并直接使用该 byte-array。如果给 `Output` 提供一个 `OutputStream`，则装满 buffer 后，`Output` 会将 buffer 中的字节 flush 到流中，否则 `Output` 会自动增加 buffer。`Output` 包含许多将基本类型和字符串写成字节的方法，提供类似 `DataOutputStream`, `BufferedOutputStream`, `FilterOutputStream` 和 `ByteArrayOutputStream` 的功能。

```ad-tip
`Output` 和 `Input` 提供 `ByteArrayOutputStream` 的所有功能，所以基本没有理由让 `Output` flush 到 `ByteArrayOutputStream`。
```

在写入 `OutputStream` 时，`Output` 会对字节进行缓冲，因此在写完后必须调用 `flush` 或 `close`，以便将缓冲的字节完整写入 `OutputStream`。当然，如果没有给 `Output` 提供 `OutputStream`，就不需要调用 `flush` 或 `close`。

与许多流不同，`Output` 实例可以通过设置位置、设置新的字节数组或流来重用。

```ad-tip
因为 `Output` 已经缓冲了，所以没必要让 `Output` flush 到 `BufferedOutputStream`。
```

### Input

`Input` 类是一个 `InputStream`，它从 byte-array buffer 读取数据：

- 如果需要从 byte-array 读取数据，则可以直接设置该 buffer；
- 如果从 `InputStream` 读取数据，则读完 buffer 中的数据后，使用 `InputStream` 填充 buffer

`Input` 提供许多用于高效读取基本类型和字符串字节的方法。提供类似 `DataInputStream`, `BufferedInputStream`, `FilterInputStream` 和 `ByteArrayInputStream` 的方法。

```ad-tip
`Input` 提供了 `ByteArrayInputStream` 的所有功能，所以没有理由使用 `ByteArrayInputStream` 为 `Input` 提供数据。
```

如果调用 `Input.close`，`Input` 内的 `InputStream` 随之关闭（如果有）。如果不是从 `InputStream` 读取数据，则没有必要调用 `close`。

与许多流不同，`Input` 实例可以重用，只需设置 position 和 limit，或设置新的 byte-array 或 `InputStream`。

`Input` 的无参构造函数创建未初始化的 `Input`。在使用前必须调用 `setBuffer`。

### ByteBuffers

`ByteBufferOutput` 和 `ByteBufferInput` 的工作方式与 `Output` 和 `Input` 完全相同，只是它们使用 `ByteBuffer` 而不是 byte-array。

### Unsafe buffers

`UnsafeOutput`, `UnsafeInput`, `UnsafeByteBufferOutput` 和 `UnsafeByteBufferInput` 类的工作方式与对应的 non-unsafe 类完全相同，只是它们在许多情况下使用 `sun.misc.Unsafe` 以提高性能。要使用这些类，`Util.unsafe` 必须为 `true`。

使用 unsafe buffers 的主要缺点是，执行序列化的系统的字节序（endianness）和数字类型的表示会影响序列化数据。例如，在 x86 熵写入数据，在 SPARC 上读取数据，反序列化会失败。此外，使用 unsafe-buffer 写入数据，则必须同时使用 unsafe-buffer 读取数据。

unsafe-buffer 的最大性能差异是在不使用变长编码的大型基本类型数组。unsafe-buffer 可以禁用变长编码，或者仅为特定字段禁用（使用 `FieldSerializer`）。

### Variable length encoding

IO 类提供读写变长 int (varint) 和 long (varlong) 值的方法。该功能通过使用每个字节的第 8 位来指示后面是否有更多字节来实现，所以 varint 使用 1-5 bytes，varlong 使用 1-9 bytes。使用变成编码成本更高，但会使序列化的数据更小。



## 参考

- https://github.com/EsotericSoftware/kryo