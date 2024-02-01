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

IO 类提供读写变长 int (varint) 和 long (varlong) 值的方法。该功能通过使用每个字节的第 8 位来指示后面是否有更多字节来实现，所以 varint 使用 1-5 bytes，varlong 使用 1-9 bytes。变长编码成本更高，但会使序列化的数据更小。

在写入变长值时，可以只针对正数优化，也可以同时针对正数和负数。例如，当针对正数进行优化，0 到 127 只写入一个字节，128-16383 写入两个字节，等等。当同时对正数和负数进行优化，值的范围减半。例如，-64 到 63 写入一个字节，64 到 8192 以及 -65 到 -8192 写入两字节，等等。

`Input` 和 `Output` buffers 提供读写定长和变长值。

| 方法 | 说明 |
| ---- | ---- |
| writeInt(int) | Writes a 4 byte int. |
| writeVarInt(int, boolean) | Writes a 1-5 byte int. |
| writeInt(int, boolean) | Writes either a 4 or 1-5 byte int (the buffer decides). |
| writeLong(long) | Writes an 8 byte long. |
| writeVarLong(long, boolean) | Writes an 1-9 byte long. |
| writeLong(long, boolean) | Writes either an 8 or 1-9 byte long (the buffer decides). |

要禁用所有值的变长编码，需要重写 `writeVarInt`, `writeVarLong`, `readVarInt` 和 `readVarLong` 方法。
### Chunked encoding

先写入数据长度，再写入数据，有利于后续读取。当数据长度未知，需要对所有数据进行缓冲，确定其长度，然后写入长度、写入数据。使用单个大的 buffer 较大内存，不是理想选择。

chunk 编码通过使用小的 buffer 解决该问题。当 buffer 满了，写入长度，再写入数据，对应一个 chunk（数据块）。然后清空 buffer，用底层数据（如 `InputStream`）填充，重复该过程，直到没有更多数据。长度为 0 的 chunk 表示结束。

Kryo 提供了使 chunk 编码易于使用的类。`OutputChunked` 用于写入 chunk 数据，它继承 `Output`，所以包含所有写入数据的便捷方法。当 `OutputChunked` 满了，它会将数据块 flush 到另一个 `OutputStream`。`endChunk` 方法用于标记 chunk 的结束。

```java
OutputStream outputStream = new FileOutputStream("file.bin");
OutputChunked output = new OutputChunked(outputStream, 1024);
// Write data to output...
output.endChunk();
// Write more data to output...
output.endChunk();
// Write even more data to output...
output.endChunk();
output.close();
```

用 `InputChunked` 读取 chunk 数据，它扩展自 `Input`，因此具有读取数据的所有便捷方法。在读取数据时，`InputChunked` 到达一组 chunks 末尾会有相应指示。`nextChunks` 方法推进到下一组 chunks，即使当前 chunks 的数据没有读完。

```java
InputStream outputStream = new FileInputStream("file.bin");
InputChunked input = new InputChunked(inputStream, 1024);
// Read data from first set of chunks...
input.nextChunks();
// Read data from second set of chunks...
input.nextChunks();
// Read data from third set of chunks...
input.close();
```

### Buffer performance

Output 和 Input 的性能通常良好。Unsafe buffers 的性能可能更好，特别是对基本类型数组，但是跨平台不兼容。`ByteBufferOutput` 和 `ByteBufferInput` 的性能稍差，但如果必须是 ByteBuffer 接收数据，其性能也可以接受。

![[images/Pasted image 20240201095658.png]]

![[images/Pasted image 20240201095841.png]]

变长编码比定长慢，数据越多越明显。

![[images/Pasted image 20240201100133.png]]

chunk 编码使用了中间缓冲区，因此对所有字节都添加了额外副本。单独使用没问题，但是在可重入序列化器重，序列化器必须为每个对象创建 OutputChunked 或 InputChunked。在序列化期间分配缓冲区和垃圾回收会对性能产生负面影响。

![[images/Pasted image 20240201100425.png]]

## 读写对象

Kryo 有三套读写对象的方法。

- 不知道对象的具体类，且可能为 `null`

```java
kryo.writeClassAndObject(output, object);

Object object = kryo.readClassAndObject(input);
if (object instanceof SomeClass) {
   // ...
}
```

- 已知类，可能为 `null`

```java
kryo.writeObjectOrNull(output, object);

SomeClass object = kryo.readObjectOrNull(input, SomeClass.class);
```

- 已知类，对象不会为 `null`

```java
kryo.writeObject(output, object);

SomeClass object = kryo.readObject(input, SomeClass.class);
```

所有这些方法首先找到要使用的序列化器，然后用序列化器序列化或反序列化对象。序列化器可以调用这些方法进行递归序列化。Kryo 会自动处理对同一对象的多次引用和循环引用。

除了读写对象的方法外，Kryo 类还支持注册序列化器，有效读写类的识别符，为不接受 null 的序列化器处理 null 对象，并处理读写对象引用。使得序列化器能够专注于它们的序列化任务。
### Round trip

在测试 Kryo api 时，将对象写入字节，然后将这些字节读回对象可能很有用。

```java
Kryo kryo = new Kryo();

// Register all classes to be serialized.
kryo.register(SomeClass.class);

SomeClass object1 = new SomeClass();

Output output = new Output(1024, -1);
kryo.writeObject(output, object1);

Input input = new Input(output.getBuffer(), 0, output.position());
SomeClass object2 = kryo.readObject(input, SomeClass.class);
```

在本例中，`Output` 从一个容量为 1024 字节的 buffer 开始。如果向 `Output` 输出更多字节，buffer 的大小将随之增长。不需要关闭 `Output`，因为没有连接到 `OutputStream`。`Input` 直接从 `Output` 的 `byte[]` 缓冲区读取。

### Deep and shallow copies

Kryo 支持对象深拷贝和浅拷贝。这比序列化字节再序列化回对象更有效。

```java
Kryo kryo = new Kryo();
SomeClass object = ...
SomeClass copy1 = kryo.copy(object);
SomeClass copy2 = kryo.copyShallow(object);
```

使用的所有serializers 需要支持复制。Kryo 提供的所有 serializer 都支持复制。

和序列化一样，在复制时，如果启用了引用，Kryo 会自动处理对同一对象的多个引用和循环引用问题。

如果 Kryo 只是用来复制，则可以放心禁用注册。

Kryo 的 `getOriginalToCopyMap` 可以在复制对象 graph 后使用，获得原对象到复制对象的映射。Kryo 的 `reset` 会自动清理该 map，所以只有当 `setAutoReset` 为 false 时才有用。

### References

引用默认没有启用。因此，如果一个对象在 object-graph 中出现多次，它会被写入多次，并被反序列化多次，序列化为不同对象。禁用引用时，循环引用会导致序列化失败。使用 `setReferences` 启用或禁用引用序列化，使用 `setCopyReferences` 启用或禁用引用复制。

启用引用后，在每个对象加入 object-graph 之前，先写入一个 varint。当同一 object-graph 中再次出现该类，则只写入一个 varint。反序列化时，恢复对象引用，包括循环引用。使用的 serializers 必须在 `read` 中调用 Kryo `reference` 来支持引用。

启用引用会影响性能，因为需要跟踪读取或写入的每个对象。

![[images/Pasted image 20240201104000.png]]

#### ReferenceResolver

`ReferenceResolver` 负责跟踪读写的对象，并提供一个 int ID。提供了多个实现：

1. 如果未指定 `ReferenceResolver`，则默认为 `MapReferenceResolver`。它使用 Kryo 的 `IdentityObjectIntMap`（[一种 cuckoo hashmap](https://en.wikipedia.org/wiki/Cuckoo%5Fhashing)）跟踪写入的对象，对 put 不分配内存。这种映射的 get 非常快，不过对大量对象，put 可能有点慢。
2. HashMapReferenceResolver 使用 HashMap 跟踪对象，给 put 分配内存。当对象很多，性能更好。
3. ListReferenceResolver 使用 ArrayList 跟踪写入对象。对象较少的 object-graph，可能比 map 快（部分测试中快 15%）。不应在包含较多对象的 object-graph 中使用，因为它的查找时间和对象数目成线性关系。

可以重写 `ReferenceResolver` 的 useReferences(Class)。它返回一个 boolean 值来判断一个类是否支持引用。如果一个类不支持引用，则不将变量引用 varint ID 写入 object-graph。如果一个类不需要引用，而改类的对象在 object-graph 中出现多次，则禁用该类的引用可以减小序列化大小。所有基础类型的 wrappers 和 enums 默认禁用引用。对 String 和其他类，通常也返回 false，取决于被序列化的 object-graph。

```java
public boolean useReferences (Class type) {
   return !Util.isWrapperClass(type) && !Util.isEnum(type) && type != String.class;
}
```

#### Reference limits

`ReferenceResolver` 确定单个 object-graph 中的最大引用数。Java 数组索引仅限于 `Integer.MAX_VALUE`，因此，当序列化超过 20 亿个对象时，使用基于数组的数据结构的 `ReferenceResolver` 会导致 `java.lang.NegativeArraySizeException`。Kryo 使用 int 类的 IDs，因此单个 object-graph 中的最大引用数被限制为 int 正数和负数的全部范围（~ 40 亿）。
### Context

Kryo `getContext` 返回一个用于存储用户数据的 map。Kryo 实例对所有 serializers 都可用，因此所有 serializers 都可以轻松访问该数据。

Kryo `getGraphContext` 类似，不过在每个 object-graph 序列化或反序列化后被清除。这使得管理与当前 object-graph 相关的状态变得容易。例如，可以对第一次遇到的类可以写入 schema 数据。有关示例可参考 `CompatibleFieldSerializer`。
### Reset

Kryo 默认在每个 object-graph 完整序列化后调用 reset。该 reset 操作会在 `ClassResolver` 中注销类名，在 `ReferenceResolver` 中注销之前序列化或反序列化对象的引用，并清除 graph-context。Kryo `setAutoReset(false)` 可用来禁用自动调用 reset，使得该状态可以跨越多个 object-graph。
## 序列化框架

Kryo 是一个用于序列化的框架。框架本身不强制 schema，也不关心读写数据的内容或方式。Serializers 是可插入式的，决定读写的内容。Kryo 提供的许多 serializers 都是开箱即用，提供各种读写数据的方式。虽然 Kryo 提供的 serializers 可以读写大多数对象，但依然可以自定义 serializer 部分或完全替代 Kryo 内置的 serializers。
### Registration

Kryo 在写入对象实例时，首先需要写入对象类型信息。默认情况下，Kryo 要读写的所有类都必须实现注册。注册提供一个 int ID，serializer 根据该 ID 序列化和实例化对象。

```java
Kryo kryo = new Kryo();
kryo.register(SomeClass.class);
Output output = ...
SomeClass object = ...
kryo.writeObject(output, object);
```

在反序列化时，注册的类必须与序列化时具有完全相同的 ID。在注册时，为类分配下一个最小整数 ID，这意味**注册类的顺序很重要**。也可以在注册时显式指定 ID，就不需要在意顺序了：

```java
Kryo kryo = new Kryo();
kryo.register(SomeClass.class, 9);
kryo.register(AnotherClass.class, 10);
kryo.register(YetAnotherClass.class, 11);
```

Class IDs 1 和 2 保留。Class IDs 0-8 用于基本类型和 String。ID 被写入为优化的 varints，因此它们为小的正整数时效率最高，负数 IDs 无法有效序列化。

#### ClassResolver

ClassResolver 读写表示类的字节。默认实现大多情况下是够用的，但也可以自定义，如注册类时的额外行为，序列化时遇到未注册类的行为，以及读写表示类的内容。

#### Optional registration

Kryo 通过设置，可以无效注册就能序列化：

```java
Kryo kryo = new Kryo();
kryo.setRegistrationRequired(false);
Output output = ...
SomeClass object = ...
kryo.writeObject(output, object);
```

可以混合使用注册和未注册的类。未注册类主要有两个缺点：

1. 存在安全隐患，因为它允许通过反序列化来创建任何类的实例。在构造或销毁时具有副作用的类可能被用于恶意目的。
2. 对未注册类，初次出现在 object-graph 时，不是使用 varint 类（通常 1-2 字节），而是完全限定类名。该类在同一 object-graph 下次出现时才使用 varint。简短的包名可以减少包含未注册类的序列化大小。

如果 Kryo 仅用于复制，则可以安全地禁用注册。

当不需要注册时，可以启用 `setWarnUnregisteredClasses`，在遇到未注册类时 log 消息。可以重写 `unregisteredClassMessage` 以自定义日志消息或采用去他操作。
### Default serializers

注册类时可以选择指定 serializer 实例。在反序列化时，必须使用与序列化完全相同的 serializer 和配置。

```java
Kryo kryo = new Kryo();
kryo.register(SomeClass.class, new SomeSerializer());
kryo.register(AnotherClass.class, new AnotherSerializer());
```

如果未指定 serializer，或遇到未注册的类，则从默认 serializers 中自动选择一个。拥有许多默认serializers 不会影响序列化性能，因此 Kryo 默认为各种 JRE 类提供了 50+ 默认 serializers。另外，可以继续添加默认 serializers：

```java
Kryo kryo = new Kryo();
kryo.setRegistrationRequired(false);
kryo.addDefaultSerializer(SomeClass.class, SomeSerializer.class);

Output output = ...
SomeClass object = ...
kryo.writeObject(output, object);
```

这将导致在注册 `SomeClass` 或其子类时创建 `SomeSerializer` 实例。

默认 serializers 是排序的，以首先匹配更具体的类。

如果某个类没有匹配的默认 serializer，则使用全局默认 serializer。全局默认 serializer 默认为 `FieldSerializer`，可以修改。全局 serializer 一般可以处理多种不同类型：

```java
Kryo kryo = new Kryo();
kryo.setDefaultSerializer(TaggedFieldSerializer.class);
kryo.register(SomeClass.class);
```

对该代码，如果没有默认 serializer 匹配 `SomeClass`，则使用 `TaggedFieldSerializer`。

也可以使用 DefaultSerializer 注释类，以代替选择默认 serializers：

```java
@DefaultSerializer(SomeClassSerializer.class)
public class SomeClass {
   // ...
}
```

为了获得最大的灵活性，可以重写 Kryo `getDefaultSerializer` 以自定义选择和实例化 serializer。
#### Serializer factories

`addDefaultSerializer(Class, Class)` 方法不允许配置 serializer。但可以设置 serializer-factory，而不是 serializer，在 factory 中可以创建和配置每个 serializer 实例。常用 serializers 都提供了factories，通常使用 getConfig 方法来配置所创建的  serializer：

```java
Kryo kryo = new Kryo();
 
TaggedFieldSerializerFactory defaultFactory = new TaggedFieldSerializerFactory();
defaultFactory.getConfig().setReadUnknownTagData(true);
kryo.setDefaultSerializer(defaultFactory);

FieldSerializerFactory someClassFactory = new FieldSerializerFactory();
someClassFactory.getConfig().setFieldsCanBeNull(false);
kryo.register(SomeClass.class, someClassFactory);
```

serializer-factory 有一个 `isSupported(Class)` 方法，允许它拒绝一个类，即使这个类与它匹配。使得 factory 可以检查多个接口，或实现其它逻辑。

### Object creation

虽然有的 serializers 是针对特定类的的，但是其它 serializers 可以序列化许多不同的类。Serializers 可以使用 Kryo 的 newInstance(Class) 方法创建任意类的实例。这是通过查找类的注册信息，然后使用注册的 ObjectInstantiator。在注册时可以指定 instantiator。

```java
Registration registration = kryo.register(SomeClass.class);
registration.setInstantiator(new ObjectInstantiator<SomeClass>() {
   public SomeClass newInstance () {
      return new SomeClass("some constructor arguments", 1234);
   }
});
```

如果注册没有 instantiator，则由 Kryo `newInstantiator` 提供。通过重写 Kryo `newInstantiator` 或使用 InstantiatorStrategy 可以自定义对象创建方式。
#### InstantiatorStrategy

Kryo 提供的 `DefaultInstantiatorStrategy`，它使用 ReflectASM 调用零参构造函数来创建对象。如果失败，则通过反射调用无参构造函数。如果这也失败了，则抛出异常或尝试用 `InstantiatorStrategy`。反射使用 `setAccessible`，因此提供 `private` 无参构造函数是一个很好的方法，允许 Kryo 创建类，还不影响 `public` API。

`DefaultInstantiatorStrategy` 是使用 Kryo 创建对象的推荐方法。它像运行 Java 代码一样运行构造函数。另外，也可以使用语言外的机制创建对象。[Objenesis](http://objenesis.org/) `StdInstantiatorStrategy` 使用 JVM 特有的 api 创建类的实例，而不调用任何构造函数。使用这种方法有一定危险，绕过构造函数创建对象，可能使对象处于未初始化状态或无效。使用这种方式，必须将类进行对应设计。

可以将 Kryo 配置为先尝试 DefaultInstantiatorStrategy，然后在必要时使用 StdInstantiatorStrategy：

```java
kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
```

另一个选项是 SerializingInstantiatorStrategy，它使用 Java 内置序列化机制创建实例。使用此方法，该类必须实现  java.io.Serializable，并且调用超类的第一个无参构造函数。这也绕过了构造函数，因此与 StdInstantiatorStrategy 一样，也有一定危险：

```java
kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new SerializingInstantiatorStrategy()));
```
#### Overriding create

一些通用 serializer 提供了可以被重写的方法，为特定类型提供自定义对象创建方法，而不是调用 Kryo 的 newInstance：

```java
kryo.register(SomeClass.class, new FieldSerializer(kryo, SomeClass.class) {
   protected T create (Kryo kryo, Input input, Class<? extends T> type) {
      return new SomeClass("some constructor arguments", 1234);
   }
});
```

有些 serializers 提供了 `writeHeader` 方法，可以重写该方法，以便在合适的时候写入创建对象所需的数据：

```java
static public class TreeMapSerializer extends MapSerializer<TreeMap> {
   protected void writeHeader (Kryo kryo, Output output, TreeMap map) {
      kryo.writeClassAndObject(output, map.comparator());
   }

   protected TreeMap create (Kryo kryo, Input input, Class<? extends TreeMap> type, int size) {
      return new TreeMap((Comparator)kryo.readClassAndObject(input));
   }
}
```

如果 serializer 不提供 `writeHeader`，则为 `create` 写入数据在 `write` 中完成：

```java
static public class SomeClassSerializer extends FieldSerializer<SomeClass> {
   public SomeClassSerializer (Kryo kryo) {
      super(kryo, SomeClass.class);
   }
   public void write (Kryo kryo, Output output, SomeClass object) {
      output.writeInt(object.value);
   }
   protected SomeClass create (Kryo kryo, Input input, Class<? extends SomeClass> type) {
      return new SomeClass(input.readInt());
   }
}
```

### Final classes

即使 serializer 知道值的期望类（如字段的类），如果值的具体类不是 final，serializer 需要先写入类 ID，然后写入值。final 类是非多态的，因此可以更有效地序列化。

Kryo 的 `isFinal` 用来确定一个类是否为 final。可以重写该方法，对非 final 类，也可以返回 true。例如，如若一个应用广泛使用 ArrayList，但不使用 ArrayList 子类，那么将 ArrayList 作为 final 处理，对每个 ArrayList 字段 FieldSerializer 可以节省 1-2 个字节。
### Closures

Kryo 可以序列化实现 java.io.Serializable 的 Java 8+ closures，有一些注意事项。在一个 JVM 上序列化的 Closures 可能无法在另一个 JVM 上反序列化。

Kryo `isClosure` 用于确定一个类是否为 closure。如果是，则使用 `ClosureSerializer.Closure` 查找注册类，而不是 closure 的类。序列化 closures，必须注册以下类：`ClosureSerializer.Closure`, `Object[]` 和 `Class`。此外，必须注册 closure 的捕获类：

```java
kryo.register(Object[].class);
kryo.register(Class.class);
kryo.register(ClosureSerializer.Closure.class, new ClosureSerializer());
kryo.register(CapturingClass.class);

Callable<Integer> closure1 = (Callable<Integer> & java.io.Serializable)( () -> 72363 );

Output output = new Output(1024, -1);
kryo.writeObject(output, closure1);

Input input = new Input(output.getBuffer(), 0, output.position());
Callable<Integer> closure2 = (Callable<Integer>)kryo.readObject(input, ClosureSerializer.Closure.class);
```

序列化没有实现 Serialiable 的 closures 需要额外工作。
### Compression and encryption

Kryo 支持 streams，因此对序列化字节使用压缩或加密很容易：

```java
OutputStream outputStream = new DeflaterOutputStream(new FileOutputStream("file.bin"));
Output output = new Output(outputStream);
Kryo kryo = new Kryo();
kryo.writeObject(output, object);
output.close();
```

如果需要，可以使用 serializer 对 object-graph 的部分字节进行压缩或加密。例如，参考 `DeflateSerializer` 或 `BlowfishSerializer`。这些 serializers 包装另一个 serializer 来编码或解码字节。

## Implementing a serializer

`Serializer` 抽象类定义从对象到字节和从字节到对象的方法。

```java
public class ColorSerializer extends Serializer<Color> {
   public void write (Kryo kryo, Output output, Color color) {
      output.writeInt(color.getRGB());
   }

   public Color read (Kryo kryo, Input input, Class<? extends Color> type) {
      return new Color(input.readInt());
   }
}
```

`Serializer` 只有两个必须实现的方法：

- `write` 将对象以字节的形式写入 `Output`
- `read` 创建对象新的实例，并从 `Input` 读取数据填充实例
### Serializer references

当使用 Kryo 在 Serializer `read`  中读取嵌套对象，如果嵌套对象可能引用父对象，则需要首先与父对象一起调用 Kryo `reference`。如果嵌套对象不可能引用父对象，或者没有使用引用，则没必要调用 `reference`。如果嵌套对象可以使用相同的 serializer，则该 serializer 必须是可重入的：

```java
public SomeClass read (Kryo kryo, Input input, Class<? extends SomeClass> type) {
   SomeClass object = new SomeClass();
   kryo.reference(object);
   // Read objects that may reference the SomeClass instance.
   object.someField = kryo.readClassAndObject(input);
   return object;
}
```

#### Nested serializers

Serializers 通常不应该直接使用其它 serializers，而应该使用 Kryo 的读写方法。从而允许 Kryo 使用编排序列化、引用和 null 对象等特性。有时，serializer 知道对嵌套对象使用哪个 serializer。此时，它应该使用接受 serializer 的 Kryo 的 read 和 write 方法。

如果对象可能为 null：

```java
Serializer serializer = ...
kryo.writeObjectOrNull(output, object, serializer);

SomeClass object = kryo.readObjectOrNull(input, SomeClass.class, serializer);
```

如果对象不可能为 null：

```java
Serializer serializer = ...
kryo.writeObject(output, object, serializer);

SomeClass object = kryo.readObject(input, SomeClass.class, serializer);
```

在序列化过程中，`getDepth` 返回 object-graph 的当前深度。

#### KryoException

当序列化失败时，可以抛出 KryoException，其中包含 object-graph 发生异常的位置。当使用嵌套 serializers，可以在 KryoException 添加 trace 信息。

```java
Object object = ...
Field[] fields = ...
for (Field field : fields) {
   try {
      // Use other serializers to serialize each field.
   } catch (KryoException ex) {
      ex.addTrace(field.getName() + " (" + object.getClass().getName() + ")");
      throw ex;
   } catch (Throwable t) {
      KryoException ex = new KryoException(t);
      ex.addTrace(field.getName() + " (" + object.getClass().getName() + ")");
      throw ex;
   }
}
```

#### Stack size

Kryo 提供的 serializers 在序列化嵌套对象时使用了调用堆栈。Kryo 最大限度地减少了堆栈调用，但是对于非常深的 object-graph 可能会发生堆栈溢出。这是大多数序列化库（包含 Java 内置的序列化）的常见问题。可以使用 `-Xss` 增加堆栈大小，但需要注意，该设置影响所有线程。在包含许多线程的 JVM 中，较大的堆栈大小会使用更多内存。

Kryo 的 `setMaxDepth` 可以用来限制 object-graph 的最大深度，可以防止恶意数据导致堆栈溢出。

### Accepting null

serializers 默认不接收 null，kryo 会根据需要写入一个字节表示 null 或非 null。如果 serializer 可以通过自己处理 null 来提高效率，它可以调用 serializer setAcceptsNull(true)。这也可以用来避免在知道序列化程序处理的所有实例不会有 null 的情况。

### Generics

Kryo `getGenerics` 提供泛型类型信息，使 serializers 更高效。常用于避免在类型参数类为 final 时写入类。

默认启用泛型类型推断，使用 setOptimizedGenerics(false) 可以禁用。禁用泛型优化可以提高性能，但会增加序列化大小。

如果类只有一个类型参数，`nextGenericClass` 返回类型参数，如果没有则返回 null。读写嵌套对象后，必须调用 `popGenericType`。相关示例，可以参考 CollectionSerializer。

```java
public class SomeClass<T> {
   public T value;
}
public class SomeClassSerializer extends Serializer<SomeClass> {
   public void write (Kryo kryo, Output output, SomeClass object) {
      Class valueClass = kryo.getGenerics().nextGenericClass();

      if (valueClass != null && kryo.isFinal(valueClass)) {
         Serializer serializer = kryo.getSerializer(valueClass);
         kryo.writeObjectOrNull(output, object.value, serializer);
      } else
         kryo.writeClassAndObject(output, object.value);

      kryo.getGenerics().popGenericType();
   }

   public SomeClass read (Kryo kryo, Input input, Class<? extends SomeClass> type) {
      Class valueClass = kryo.getGenerics().nextGenericClass();

      SomeClass object = new SomeClass();
      kryo.reference(object);

      if (valueClass != null && kryo.isFinal(valueClass)) {
         Serializer serializer = kryo.getSerializer(valueClass);
         object.value = kryo.readObjectOrNull(input, valueClass, serializer);
      } else
         object.value = kryo.readClassAndObject(input);

      kryo.getGenerics().popGenericType();
      return object;
   }
}
```

对具有多个类型参数的类，`nextGenericTypes` 返回 GenericType 实例数组，并使用 `resolve` 获得每个 GenericType 的类。在读写任何嵌套对象后，必须调用 `popGenericType`。相关示例可参考 MapSerializer。

```java
public class SomeClass<K, V> {
   public K key;
   public V value;
}
public class SomeClassSerializer extends Serializer<SomeClass> {
   public void write (Kryo kryo, Output output, SomeClass object) {
      Class keyClass = null, valueClass = null;
      GenericType[] genericTypes = kryo.getGenerics().nextGenericTypes();
      if (genericTypes != null) {
         keyClass = genericTypes[0].resolve(kryo.getGenerics());
         valueClass = genericTypes[1].resolve(kryo.getGenerics());
      }

      if (keyClass != null && kryo.isFinal(keyClass)) {
         Serializer serializer = kryo.getSerializer(keyClass);
         kryo.writeObjectOrNull(output, object.key, serializer);
      } else
         kryo.writeClassAndObject(output, object.key);

      if (valueClass != null && kryo.isFinal(valueClass)) {
         Serializer serializer = kryo.getSerializer(valueClass);
         kryo.writeObjectOrNull(output, object.value, serializer);
      } else
         kryo.writeClassAndObject(output, object.value);

      kryo.getGenerics().popGenericType();
   }

   public SomeClass read (Kryo kryo, Input input, Class<? extends SomeClass> type) {
      Class keyClass = null, valueClass = null;
      GenericType[] genericTypes = kryo.getGenerics().nextGenericTypes();
      if (genericTypes != null) {
         keyClass = genericTypes[0].resolve(kryo.getGenerics());
         valueClass = genericTypes[1].resolve(kryo.getGenerics());
      }

      SomeClass object = new SomeClass();
      kryo.reference(object);

      if (keyClass != null && kryo.isFinal(keyClass)) {
         Serializer serializer = kryo.getSerializer(keyClass);
         object.key = kryo.readObjectOrNull(input, keyClass, serializer);
      } else
         object.key = kryo.readClassAndObject(input);

      if (valueClass != null && kryo.isFinal(valueClass)) {
         Serializer serializer = kryo.getSerializer(valueClass);
         object.value = kryo.readObjectOrNull(input, valueClass, serializer);
      } else
         object.value = kryo.readClassAndObject(input);

      kryo.getGenerics().popGenericType();
      return object;
   }
}
```

对传递嵌套对象类型信息的 serializer，首先使用 GenericsHierarchy 保存类的类型参数。序列化时，解析泛型类型前调用泛型 `pushTypeVariables`。如果返回值 >0，则必须继续调用泛型 popTypeVariables。具体示例可参考 FieldSerializer。

```java
public class SomeClass<T> {
   T value;
   List<T> list;
}
public class SomeClassSerializer extends Serializer<SomeClass> {
   private final GenericsHierarchy genericsHierarchy;

   public SomeClassSerializer () {
      genericsHierarchy = new GenericsHierarchy(SomeClass.class);
   }

   public void write (Kryo kryo, Output output, SomeClass object) {
      Class valueClass = null;
      Generics generics = kryo.getGenerics();
      int pop = 0;
      GenericType[] genericTypes = generics.nextGenericTypes();
      if (genericTypes != null) {
         pop = generics.pushTypeVariables(genericsHierarchy, genericTypes);
         valueClass = genericTypes[0].resolve(generics);
      }

      if (valueClass != null && kryo.isFinal(valueClass)) {
         Serializer serializer = kryo.getSerializer(valueClass);
         kryo.writeObjectOrNull(output, object.value, serializer);
      } else
         kryo.writeClassAndObject(output, object.value);

      kryo.writeClassAndObject(output, object.list);

      if (pop > 0) generics.popTypeVariables(pop);
      generics.popGenericType();
   }

   public SomeClass read (Kryo kryo, Input input, Class<? extends SomeClass> type) {
      Class valueClass = null;
      Generics generics = kryo.getGenerics();
      int pop = 0;
      GenericType[] genericTypes = generics.nextGenericTypes();
      if (genericTypes != null) {
         pop = generics.pushTypeVariables(genericsHierarchy, genericTypes);
         valueClass = genericTypes[0].resolve(generics);
      }

      SomeClass object = new SomeClass();
      kryo.reference(object);

      if (valueClass != null && kryo.isFinal(valueClass)) {
         Serializer serializer = kryo.getSerializer(valueClass);
         object.value = kryo.readObjectOrNull(input, valueClass, serializer);
      } else
         object.value = kryo.readClassAndObject(input);

      object.list = (List)kryo.readClassAndObject(input);

      if (pop > 0) generics.popTypeVariables(pop);
      generics.popGenericType();
      return object;
   }
}
```

### KryoSerializable

不使用 serializer，通过实现 KryoSerializable （类似 java.io.Externalizable）类可以自定义序列化。

```java
public class SomeClass implements KryoSerializable {
   private int value;
   public void write (Kryo kryo, Output output) {
      output.writeInt(value, false);
   }
   public void read (Kryo kryo, Input input) {
      value = input.readInt(false);
   }
}
```

显然，在调用 `read` 之前必须已经创建实例，因此该类无法控制自己的创建。KryoSerializable 类使用默认的 serializer KryoSerializableSerializer，而它使用 Kryo `newInstance` 创建新的实例。编写自定义 serializer 来定制该过程很简单。

### Serializer copying

Serializers 仅在 `copy` 方法被覆盖时才知道复制。类似 `Serializer` 的 `read` 方法，该方法包含创建和配置 copy 的逻辑。与 `read` 一样，对任何可能引用父对象的子对象，在复制 child 对象前必须调用 `reference`。

```java
class SomeClassSerializer extends Serializer<SomeClass> {
   public SomeClass copy (Kryo kryo, SomeClass original) {
      SomeClass copy = new SomeClass();
      kryo.reference(copy);
      copy.intValue = original.intValue;
      copy.object = kryo.copy(original.object);
      return copy;
   }
}
```

#### KryoCopyable

不使用 serializer，可以实现 KryoCopyable 来复制：

```java
public class SomeClass implements KryoCopyable<SomeClass> {
   public SomeClass copy (Kryo kryo) {
      SomeClass copy = new SomeClass();
      kryo.reference(copy);
      copy.intValue = intValue;
      copy.object = kryo.copy(object);
      return copy;
   }
}
```

#### Immutable serializers

对 immutable 类型，可以调用 Serializer `setImmutable(true)`。此时不需要实现 Serializer `copy` 方法，默认的 copy `实现会返回原对象`。

## Kryo versioning and upgrading

以下经验规则适用于 Kryo 的版本编号：

1. 如果序列化兼容性被破坏，major-version 将增加。这意味着用旧版本序列化的数据用新版本可能无法反序列化。
2. 如果 public API 的 binary 或 source 兼容性被破坏，minor-version 增加。为了避免在很少用户受影响的情况下增加版本，对很少使用或不打算常使用的 public 类，允许一些小的破坏。

升级任何依赖项都是一件重要事情，但是序列化库更容易损坏。升级 Kryo 时，请仔细检查版本差异，并在应用中彻底测试新版本。

- 在开发时，针对不同二进制格式和默认 serializer 测试序列化兼容性。
- 在开发时，使用 [clir](https://www.mojohaus.org/clirr-maven-plugin/) 跟踪二进制文件和源代码的兼容性。
- 对每个版本，都提供一个 changelog，其中包含序列化、二进制和源码的兼容性。
- 使用 japi-compliance-checker 包含 binary 和 source 的兼容性。

## Interoperability

提供的 Kryo serializers 默认假定使用 Java 进行反序列化，因此它们不会显式定义所编写的格式。serializer 可以使用标准格式编写，以便其它语言更容易阅读，但默认不提供这种格式。

## Compatibility

对序列长期存储的序列化字节，如何处理类的更改很重要。这被称为向前兼容（读取新类序列化的字节）和向后兼容（读取旧类序列化的字节）。Kryo 提供了几个通用 serializers，它们采用不同方法来处理兼容性。可以很容易开发其它序列化器实现向前和向后兼容，例如，使用手写 schema 的 serializer。
## Serializers

Kryo 提供了许多具有不同配置和兼容性的 serializers。在[ kryo-serializers](https://github.com/magro/kryo-serializers) 项目中还有其他 serializers，它们要么访问 private API，或者不是在所有 JVMs 都安全。
### FieldSerializer

`FieldSerializer` 序列化每个非 `transient` 字段。无需任何配置就可以序列化 POJOs 和许多其它类。默认情况下，它会读写所有 non-public 字段，因此评估每个被序列化的类很重要。如果字段是 public，序列化可能更快。

`FieldSerializer` 使用 Java 类作为 schema，只写入字段数据而不写入任何 schema 信息，从而提高了效率。在不使之前序列化字节失效的前提下，它不支持添加、删除或更改字段类型；要重命名字段，也不能改变字段的字典顺序。

`FieldSerializer` 的兼容性问题在大多情况下都可以接受，但对长期数据存储来说不是一个好的选择。

#### FieldSerializer settings

|Setting|Description|Default value|
|---|---|---|
|`fieldsCanBeNull`|When false it is assumed that no field values are null, which can save 0-1 byte per field.|true|
|`setFieldsAsAccessible`|When true, all non-transient fields (including private fields) will be serialized and `setAccessible` if necessary. If false, only fields in the public API will be serialized.|true|
|`ignoreSyntheticFields`|If true, synthetic fields (generated by the compiler for scoping) are serialized.|false|
|`fixedFieldTypes`|If true, it is assumed every field value's concrete type matches the field's type. This removes the need to write the class ID for field values.|false|
|`copyTransient`|If true, all transient fields will be copied.|true|
|`serializeTransient`|If true, transient fields will be serialized.|false|
|`variableLengthEncoding`|If true, variable length values are used for int and long fields.|true|
|`extendedFieldNames`|If true, field names are prefixed by their declaring class. This can avoid conflicts when a subclass has a field with the same name as a super class.|false|
#### CachedField settings

FieldSerializer 提供将被序列化的字段。可以删除不想序列化的字段。也可以通过合理配置提高字段序列化效率。

```java
FieldSerializer fieldSerializer = ...

fieldSerializer.removeField("id"); // Won't be serialized.

CachedField nameField = fieldSerializer.getField("name");
nameField.setCanBeNull(false);

CachedField someClassField = fieldSerializer.getField("someClass");
someClassField.setClass(SomeClass.class, new SomeClassSerializer());
```

|Setting|Description|Default value|
|---|---|---|
|`canBeNull`|When false it is assumed the field value is never null, which can save 0-1 byte.|true|
|`valueClass`|Sets the concrete class and serializer to use for the field value. This removes the need to write the class ID for the value. If the field value's class is a primitive, primitive wrapper, or final, this setting defaults to the field's class.|null|
|`serializer`|Sets the serializer to use for the field value. If the serializer is set, some serializers required the value class to also be set. If null, the serializer registered with Kryo for the field value's class will be used.|null|
|`variableLengthEncoding`|If true, variable length values are used. This only applies to int or long fields.|true|
|`optimizePositive`|If true, positive values are optimized for variable length values. This only applies to int or long fields when variable length encoding is used.|true|

#### FieldSerializer annotations

可以通过注释配置每个字段的 serializers。

|Annotation|Description|
|---|---|
|`@Bind`|Sets the CachedField settings for any field.|
|`@CollectionBind`|Sets the CollectionSerializer settings for Collection fields.|
|`@MapBind`|Sets the MapSerializer settings for Map fields.|
|`@NotNull`|Marks a field as never being null.|

```java
public class SomeClass {
   @NotNull
   @Bind(serializer = StringSerializer.class, valueClass = String.class, canBeNull = false) 
   Object stringField;

   @Bind(variableLengthEncoding = false)
   int intField;

   @BindMap(
      keySerializer = StringSerializer.class, 
      valueSerializer = IntArraySerializer.class, 
      keyClass = String.class, 
      valueClass = int[].class, 
      keysCanBeNull = false)
   Map map;
   
   @BindCollection(
      elementSerializer = LongArraySerializer.class,
      elementClass = long[].class, 
      elementsCanBeNull = false) 
   Collection collection;
}
```

### VersionFieldSerializer

`VersionFieldSerializer` 扩展 `FieldSerializer`，提供向后兼容性。这意味着可以在不使之前序列化的字节失效的情况下添加字段。不支持删除、重命名或更改字段类型。

当添加字段时，必须使用 `@Since(int)` 注释来指示添加的版本，以便与之前序列化的字节兼容。注释值永远不能改变。

`VersionFieldSerializer` 相对 `FieldSerializer` 只增加了一点开销：一个额外的 `varint`。

#### VersionFieldSerializer settings

|Setting|Description|Default value|
|---|---|---|
|`compatible`|When false, an exception is thrown when reading an object with a different version. The version of an object is the maximum version of any field.|true|
`VersionFieldSerializer` 继承了 `FieldSerializer` 的所有设置选项。

### TaggedFieldSerializer

`TaggedFieldSerializer` 扩展 `FieldSerializer` 以提供向后和向前兼容性。这意味着可以添加、删除或重命名字段，且不会使之前序列化的字节失效。不支持更改字段类型。

只有带 `@Tag(int)` 注释的字段才会被序列化。字段 tag 值必须 uniquew，不管时在类中还是所有超类中。遇到重复 tag 值将抛出异常。

向前和向后兼容性以及序列化性能取决于 `readUnknownTagData` 和 `chunkedEncoding` 设置。此外，对每个 tag 值，在写入字段前都写入一个 varint。

当 `readUnknownTagData` 和 `chunkedEncoding` 为 false，不能删除字段，但可以用 @Deprecated 注释。在读取老的字节时依然会读取 Deprecated 字段，但不会写入新的字节。类可以通过 Deprecated 字段并写入其它地方来更新。字段可以重命名或设置为 private。

#### TaggedFieldSerializer settings

|Setting|Description|Default value|
|---|---|---|
|`readUnknownTagData`|When false and an unknown tag is encountered, an exception is thrown or, if `chunkedEncoding` is true, the data is skipped.  <br>  <br>When true, the class for each field value is written before the value. When an unknown tag is encountered, an attempt to read the data is made. This is used to skip the data and, if references are enabled, any other values in the object graph referencing that data can still be deserialized. If reading the data fails (eg the class is unknown or has been removed) then an exception is thrown or, if `chunkedEncoding` is true, the data is skipped.  <br>  <br>In either case, if the data is skipped and references are enabled, then any references in the skipped data are not read and further deserialization may receive the wrong references and fail.|false|
|`chunkedEncoding`|When true, fields are written with chunked encoding to allow unknown field data to be skipped. This impacts performance.|false|
|`chunkSize`|The maximum size of each chunk for chunked encoding.|1024|
TaggedFieldSerializer 同样继承了 FieldSerializer 的所有设置选项。

### CompatibleFieldSerializer


## Thread safety

Kryo 不是线程安全的。每个线程都应该有自己的 Kryo、Input 和 Output 实例。

### Pooling

由于 Kryo 不是线程安全的，并且构造和配置 Kryo 实例的成本相对较高，因此在多线程环境可以考虑使用 ThreadLocal 或线程池。

```java
static private final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
   protected Kryo initialValue() {
      Kryo kryo = new Kryo();
      // Configure the Kryo instance.
      return kryo;
   };
};

Kryo kryo = kryos.get();
```

对池化，Kryo 提供了 Pool 类，支持 Kryo, Input, Output 以及任何实例的池化。

```java
// Pool constructor arguments: thread safe, soft references, maximum capacity
Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8) {
   protected Kryo create () {
      Kryo kryo = new Kryo();
      // Configure the Kryo instance.
      return kryo;
   }
};

Kryo kryo = kryoPool.obtain();
// Use the Kryo instance here.
kryoPool.free(kryo);
```

```java
Pool<Output> outputPool = new Pool<Output>(true, false, 16) {
   protected Output create () {
      return new Output(1024, -1);
   }
};

Output output = outputPool.obtain();
// Use the Output instance here.
outputPool.free(output);
```

```java
Pool<Input> inputPool = new Pool<Input>(true, false, 16) {
   protected Input create () {
      return new Input(1024);
   }
};

Input input = inputPool.obtain();
// Use the Input instance here.
inputPool.free(input);
```

## Benchmarks

Kryo 提供了许多基于 JMH 的测试和 ggplot2 文件。

![[images/Pasted image 20240201110414.png]]

![[images/Pasted image 20240201110424.png]]

![[images/Pasted image 20240201110431.png]]

![[images/Pasted image 20240201110439.png]]



## 参考

- https://github.com/EsotericSoftware/kryo