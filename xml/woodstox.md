# Woodstox

## 简介

Woodstox 是一个快速、开源的 StAX 实现。

使用 WoodStox 步骤：
1. 获得使用 `XMLInputFactory2` 实例；
2. 创建 `XMLStreamReader` 实例；

```java
XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory2.newFactory();
XMLStreamReader2 reader = factory.createXMLStreamReader(new File(""));
```

余下和使用标准 `XMLStreamReader` 一样。

## 配置

WoodStox 提供了几个便捷配置：

```java
void configureForXmlConformance()
```
使创建的 reader 尽可能符合 XML 标准，还行 XML 规范要求的所有检查和转换。

关于默认的 StAX 属性设置，建议实现：
- 启用 `SUPPORT_DTD` 属性
- 启用 `IS_NAMESPACE_AWARE`
- 启用 `IS_REPLACING_ENTITY_REFERENCES`
- 启用 `IS_SUPPORTING_EXTERNAL_ENTITIES`
其它标准设置保持不变。

WoodStox 配置可以分为三部分：

- Stax 1.x 标准配置，对应 `XMLInputFactory` 和 `XMLOutputFactory`
- Stax 2 扩展配置，对应 `XMLInputFactory2` 和 XMLOutputFactory2
- Woodstox 特定配置，对应 `WstxInputProperties` 和 `WstxOutputProperties`

### Stax 1.x 标准

`XMLInputFactory` 定义的设置：

- **isCoalescing**

将相邻字符数据合并为单个 `CHARACTERS` 事件。如何禁用，一段 text 可能以多个事件返回。
类型：Boolean
默认：False

- **isNamespaceAware**

是否启用命名空间处理。
如果禁用，则不绑定命名空间，并且完整的元素/属性名流报告为 localName，例如，`<xml:space>` 的 localName 为 "xml:space"，且没有命名空间前缀或 URI。
如果启用，则处理命名空间生命，并按预期应用前缀/命名空间绑定。
类型：Boolean
默认：True

- **supportDTD**

是否支持 DTD。
如果启用，则读取 DTD 定义（内部和外部），并扩展解析的实体。
如果禁用，则跳过内部 DTD 定义，并且不读取外部 DTD。
> 禁用时没有 DTD 验证。

类型：Boolean
默认：True

- **isValidating**

是否启用 DTD 验证。不影响 XML Schema、Relax NG 或其它验证设置。
只有启用 **supportDTD** 才有效。

- **isSupportingExternalEntities**

如果启用 DTD 处理，则可以解析外部实体。禁用该设置可以禁用此扩展。

`XMLOutputFactory` 只有一个设置：

- **IS_REPAIRING_NAMESPACES**



## Validate

1. 创建 `XMLValidationSchemaFactory` 实例：

```java
public static XMLValidationSchemaFactory newInstance(String schemaType);
```

其中 `schemaType` 支持如下 4 种类型，这些字段定义在 `XMLValidationSchema` 接口中：

```java
public final static String SCHEMA_ID_DTD = "http://www.w3.org/XML/1998/namespace";
public final static String SCHEMA_ID_RELAXNG = "http://relaxng.org/ns/structure/0.9";
public final static String SCHEMA_ID_W3C_SCHEMA = "http://www.w3.org/2001/XMLSchema";
public final static String SCHEMA_ID_TREX = "http://www.thaiopensource.com/trex";
```

2. 创建 `XMLValidationSchema`

`XMLValidationSchemaFactory` 提供了多个重载方法：

```java
public XMLValidationSchema createSchema(InputStream in)
public XMLValidationSchema createSchema(InputStream in, String encoding)
public abstract XMLValidationSchema createSchema(InputStream in, String encoding,
                                                     String publicId, String systemId)
public XMLValidationSchema createSchema(Reader r)
public abstract XMLValidationSchema createSchema(Reader r, String publicId,
                                                     String systemId)
public abstract XMLValidationSchema createSchema(URL url)
public abstract XMLValidationSchema createSchema(File f)                                                     
```

3. 按照常规方法创建 `XMLStreamReader2`

```java
XMLInputFactory2 ifact = (XMLInputFactory2)XMLInputFactory.newInstance();
XMLStreamReader2 sr = ifact.createXMLStreamReader(new File(args[0]));
```

4. 启用验证

```java
sr.validateAgainst(rng);
```

5. 迭代元素

```java
while (reader.hasName()) {
    reader.next();
}
reader.close();
```
