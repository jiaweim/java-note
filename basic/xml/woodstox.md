# Woodstox

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
