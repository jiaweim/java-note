# Apache Avro

- [Apache Avro](#apache-avro)
  - [简介](#简介)
  - [Schemas](#schemas)
  - [与其它系统的对比](#与其它系统的对比)
  - [使用（Java）](#使用java)
    - [定义 schema](#定义-schema)
  - [参考](#参考)

***

## 简介

Apache Avro 是一个数据序列化系统。提供：

- 丰富的数据结构
- 紧凑、高效的 binary 数据格式
- 容器文件，用于存储持久化数据
- 远程过程调用（Remote Procedure call, RPC）
- 与动态语言的简单集成。读写数据文件或实现 RPC 协议都不需要代码生成。代码生成作为一种可选的优化，只在静态语言中值得实现。

## Schemas

Avro 依赖于 schemas，Schema 让序列化更快更小。

当 Avro 数据存储在文件中，它的 schema 与它一起存储。这样方便后续处理。

Avro schema 由 JSON 定义。

## 与其它系统的对比

Avro 的功能与 [Thrift](https://thrift.apache.org/) 及 [Protocol Buffers](https://code.google.com/p/protobuf/) 等类似，其主要区别在于：

- 动态类型：Avro 不要求生成代码。数据总是伴随着 schema。
- 无标记数据：由于在读取数据时有 schema，所以数据中不需要编码太多类型信息，从而使序列化文件更小。

## 使用（Java）

Avro 支持 C, C++, C#, Java, PHP, Python 和 Ruby。下面介绍在 Java 中如何用 Avro 序列化。

添加 Maven 依赖：

```xml
<dependency>
  <groupId>org.apache.avro</groupId>
  <artifactId>avro</artifactId>
  <version>1.11.1</version>
</dependency>
```

如果需要代码生成，还需要添加 Avro Maven 插件：

```xml
<plugin>
  <groupId>org.apache.avro</groupId>
  <artifactId>avro-maven-plugin</artifactId>
  <version>1.11.1</version>
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>schema</goal>
      </goals>
      <configuration>
        <sourceDirectory>${project.basedir}/src/main/avro/</sourceDirectory>
        <outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
      </configuration>
    </execution>
  </executions>
</plugin>
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <source>1.8</source>
    <target>1.8</target>
  </configuration>
</plugin>
```

### 定义 schema

Avro 使用 JSON 定义 schema。Schema 由基本类型（null, boolean, int, long, float, double, bytes, string）和复杂类型（record, enum, array, map, union, fixed）组成。下面定义一个简单的 schema user.avsc：

```json
{"namespace": "example.avro",
 "type": "record",
 "name": "User",
 "fields": [
     {"name": "name", "type": "string"},
     {"name": "favorite_number",  "type": ["int", "null"]},
     {"name": "favorite_color", "type": ["string", "null"]}
 ]
}
```

该 schema 定义了

## 参考

- https://avro.apache.org/docs/1.11.1/getting-started-java/
