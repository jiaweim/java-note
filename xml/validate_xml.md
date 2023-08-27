# XML 验证

Last updated: 2022-07-12, 11:32
***

## 简介

XML 验证用于确定指定 XML 文档符合指定 XML schema 格式。

创建 XML schema 的技术有多种。例如：

- Document Type Definition (DTD)，XML 内置的 schema 语言
- W3C XML Schema (WXS)，一个面向对象的 XML schema 语言，WXS 提供了一个类型系统来约束 XML 稳定的数据。WXS 由 W3C 维护，是 W3C 推荐的规范；
- ...

虽然 JAXP 在 XML 解析中支持验证，但还是推荐使用 `javax.xml.validation` API 进行验证，JAXP 验证 API 将解析与严重去耦合。

使用示例：

```java
// parse an XML document into a DOM tree
DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
Document document = parser.parse(new File("instance.xml"));

// create a SchemaFactory capable of understanding WXS schemas
SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

// load a WXS schema, represented by a Schema instance
Source schemaFile = new StreamSource(new File("mySchema.xsd"));
Schema schema = factory.newSchema(schemaFile);

// create a Validator instance, which can be used to validate an instance document
Validator validator = schema.newValidator();

// validate the DOM tree
try {
    validator.validate(new DOMSource(document));
} catch (SAXException e) {
    // instance document is invalid!
}
```

JAXP 解析 API 已与验证 API 集成，可以用验证 API 创建 `Schema` 实例，然后用 `DocumentBuilderFactory.setSchema(Schema)` 或 `SAXParserFactory.setSchema(Schema)` 将其与 `DocumentBuilderFactory` 或 `SAXParserFactory` 关联。不要在设置 schema 的同时调用 parser factory 的 `setValidating(true)`，设置 schema 会导致解析器使用新的验证 API，而设置 `setValidating(true)` 会导致解析器使用自己的内部验证方法。同时启用两者，要么导致冗余的验证，要么出错。

SAX 提供了 XML schema 验证，StAX 没有类似的验证方法。

## 验证步骤

1. 创建 `SchemaFactory` 实例

`SchemaFactory` 用于创建 `Schema` 对象，是验证 API 的入口。

`SchemaFactory` 读取外部 schema，并为验证做好准备工作。

`SchemaFactory` 不是线程安全的，推荐将方法标记为 `synchronized`。

`SchemaFactory` 提供了多个工厂方法：

```java
public static SchemaFactory newDefaultInstance()
public static SchemaFactory newInstance(String schemaLanguage)
public static SchemaFactory newInstance(String schemaLanguage, String factoryClassName, ClassLoader classLoader)
```

其中，`schemaLanguage` 指定 schema 语言。默认支持：

- javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI
- javax.xml.XMLConstants.RELAXNG_NS_URI

常用模式：

```java
SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
```

2. 创建 `Schema`

`SchemaFactory` 提供了多个重载方法创建 `Schema`，如下：

```java
public Schema newSchema(Source schema) 
public Schema newSchema(File schema)
public Schema newSchema(URL schema)
public abstract Schema newSchema(Source[] schemas)
```

这里主要是传入 schema 文件。

3. 创建 `Validator`

```java
Validator validator = schema.newValidator();
```

4. 使用 `Validator` 验证

## 参考

- https://docs.oracle.com/en/java/javase/18/docs/api/java.xml/javax/xml/validation/package-summary.html
