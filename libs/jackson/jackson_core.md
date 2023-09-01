# jackson-core

- [jackson-core](#jackson-core)
  - [简介](#简介)
  - [功能](#功能)
    - [JsonFactory Feature](#jsonfactory-feature)
      - [命名规范](#命名规范)
  - [使用](#使用)
  - [示例](#示例)
  - [参考](#参考)

***

## 简介

jackson-core 定义了底层流 API，以及 JSON 的解析器和生成器实现。

核心抽象不是针对 JSON，但是由于历史原因，许多命名包含 JSON。

该包是 jackson-databind 的基础，其它数据格式的实现，如 XML, CSV, Protobuf 等也建立在这个基础包之上，实现了核心接口，从而可以使用标准的数据绑定包，而无需考虑底层数据格式。

## 功能

jackson-core 定义了一组简单的开/关功能。

- JsonFactory.Feature (2.10+)
  - General
    - StreamReadFeature
    - StreamWriteFeature
  - JSON-specific
    - JsonReadFeature
    - JsonWriteFeature

### JsonFactory Feature

jackson-core 有一组开关特性，可以改变对 JSON 的读写。这些特性在 `JsonFactory` 上设置，在创建 `JsonParser` 或 `JsonGenerator` 之后无法动态修改。

```java
JsonFactory f = new JsonFactory();
f.disable(JsonFactory.Feature.CANONICALIZE_FIELD_NAMES);
```

#### 命名规范

这些特性决定是否以及如何规范化 JSON 对象属性名。

- `CANONICALIZE_FIELD_NAMES` (默认 true)



## 使用

- 通常从创建 `JsonFactory` 开始

```java
// Builder-style since 2.10:
JsonFactory factory = JsonFactory.builder()
// configure, if necessary:
     .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
     .build();

// older 2.x mechanism, still supported for 2.x
JsonFactory factory = new JsonFactory();
// configure, if necessary:
factory.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS);
```

也可以使用 `ObjectMapper` (jackson-databind) 创建：

```java
JsonFactory factory = objectMapper.getFactory();
```

- 读

所有的读操作都是通过 `JsonParser` (非 JSON 格式为 `JsonParser` 的子类)，它的实例通过 `JsonFactory` 创建。

- 写

所有写操作都是通过 `JsonGenerator` (非 JSON 格式为 `JsonGenerator` 的子类)，它的实例通过 `JsonFactory` 创建。

## 示例

读写事件流。

- 从事件流读




## 参考

- https://github.com/FasterXML/jackson-core
- http://www.cowtowncoder.com/blog/archives/2009/01/entry_132.html