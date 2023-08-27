# Jackson 概述

## 简介

Jackson 表示  "the Java JSON library" 或 "the best JSON parser for Java" 或 "JSON for Java"。

Jackson 提供了一套数据处理工具，包括流式 JSON 解析、生成库，数据绑定库（POJO 和 JSON）和其他数据文件格式，包括 Avro, BSON, CBOR, CSV, Smile, (Java)Properties, Protobuf, TOML, XML 以及 YAML。

主要包括：

- 三个核心包（streaming, databind, annotations）
- 数据格式库
- 数据类型库
- JAX-RS provider

以及其它的扩展模块。

## 主要模块

### 核心模块

核心模块是扩展模块的基础。目前有 3 个核心模块：

- [Streaming](https://github.com/FasterXML/jackson-core) (jackson-core) 定义底层流 API，包括特定于 JSON 的实现
- [Annotation](https://github.com/FasterXML/jackson-annotations) (jackson-annotations) 包含标准 Jackson 注释
- [Databind](https://github.com/FasterXML/jackson-databind) (jackson-databind) 实现了数据绑定（对象序列化），依赖于上面两个包

### 第三方数据类型模块

这些扩展是 Jackson 模块插件，在 `ObjectMapper.registerModule()` 中注册，通过添加 serializers 和 deserializers 来支持各种常见 Java 库的数据类型，这样 Jackson `databind` 包（`ObjectMapper`, `ObjectReader`, `ObjectWriter`）就可以读写这些类型。

### 数据格式模块

