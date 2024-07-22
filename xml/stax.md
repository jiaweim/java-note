# StAX

## 简介

StAX 是类似于 SAX 的 XML 解析器，不过两者有两个主要差别：

- StAX 是 pull 型解析器，可以只提取所需信息，SAX 是推式解析器，使用 SAX 需要做更多的工作
- StAX 可以读写XML，SAX只能读
- SAX 支持 Schema 验证

StAX 包含两套 API，提供了不同程度的抽象，基于指针的 API 和基于迭代器的 API。两套 API 的差别：

- Iterator API 返回的 immutable `XMLEvent` 对象，因此可以持有该对象后续再解析
- Cursor API 当你移动指针后，之前的数据不能再次获取
- Cursor API 相对 Iterator API 内存占用更小

## 主要接口和类

|接口或类|Curosr or Event API|说明|
|---|---|---|
|`XMLInputFactory` class|Both|用于创建 `XMLStreamReader` 或 `XMLEventReader` 的工厂类|
|`XMLOutputFactory` class|Both|用于创建 `XMLStreamWriter` 或 `XMLEventWriter` 的工厂类|
|`XMLEventFactory` class|Event Iterator|用于创建 XMLEvent 的工厂类|
|`XMLStreamReader` interface|	Cursor|解析 XML|
|`XMLStreamWriter` interface|	Cursor|生成 XML|
|`XMLEventReader` interface	|Event Iterator|解析 XML|
|`XMLEventWriter` interface|Event Iterator|生成 XML|
|`XMLEvent` interface|	Event Iterator|所有 XML 事件的基类|
|`XMLStreamException`|Both|异常|


## Cursor API

cursor API 允许程序员尽可能简单地解析和生成 XML，有两个主要接口：

- XMLStreamReader，解析 XML
- XMLStreamWriter，生成 XML

### XMLStreamReader

使用 `XMLStreamReader` 的 Cursor API 使用一个虚拟指针定位 XML 文档元素，然后通过以下方法解析指针下的元素：

|方法|说明|
|---|---|
|`getEventType()`|获取事件类型，XMLStreamConstants接口中定义的所有的常量类型|
|`hasNext()`|判断下面是否还有元素。只有当它返回true，调用 next() 及其它移动指针的方法才有意义|
|`next()`|将指针移动到下一个标记，返回一个 int 类型的事件类型，具体时间定义在 `XMLStreamConstant` 接口中|
|`getLocalName()`|当前事件的 localName|
|getPrefix()|当前事件的 prefix|
|getAttributeXXX()|获取当前属性的一组方法|
|getNamespaceXXX()|获取当前命名空间的一组方法|
|getTextXXX()|获取当前文本事件的一组方法|
|getPIData()|返回当前事件指令事件的数据部分|

使用 `XMLStreamReader` 解析 XML 的步骤：

1. 创建 `XMLInputFactory`
2. 使用 `XMLInputFactory.createXMLStreamReader()` 实例化 `XMLStreamReader`

### 使用 XMLStreamReader 的典型步骤

使用 StAX cursor API 的 XMLStreamReader 解析 XML 文档的典型步骤。

1. 导入 `javax.xml.stream.*` 类
2. 使用 `XMLInputFactory.newInstance()` 创建 `XMLInputFactory`

```java
XMLInputFactory xmlif = XMLInputFactory.newInstance();
```



### XMLStreamWriter

`XMLStreamWriter` 用于生成 XML。

使用 `XMLStreamWriter` 的基本步骤：

```java
XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
XMLStreamWriter xmlw = outputFactory.createXMLStreamWriter(new FileOutputStream("output.xml"));

// generate xml
// Write the default XML declaration
xmlw.writeStartDocument();
xmlw.writeCharacters("\n");
xmlw.writeCharacters("\n");
// Write a comment
xmlw.writeComment("this is a comment");
xmlw.writeCharacters("\n");
// Write the root element "person" with a single attribute "gender"
xmlw.writeStartElement("person");
xmlw.writeNamespace("one", "http://namespaceOne");
xmlw.writeAttribute("gender", "f");
xmlw.writeCharacters("\n");
// Write the "name" element with some content and two attributes
xmlw.writeCharacters("    ");
xmlw.writeStartElement("one", "name", "http://namespaceOne");
xmlw.writeAttribute("hair", "pigtails");
xmlw.writeAttribute("freckles", "yes");
xmlw.writeCharacters("Pippi Longstocking");
// End the "name" element
xmlw.writeEndElement();
xmlw.writeCharacters("\n");
// End the "person" element
xmlw.writeEndElement();
// End the XML document
xmlw.writeEndDocument();
// Close the XMLStreamWriter to free up resources
xmlw.close();
```

输出样式：

```xml
<?xml version="1.0" ?>
<!--this is a comment-->
<person xmlns:one="http://namespaceOne" gender="f">
    <one:name hair="pigtails" freckles="yes">Pippi Longstocking</one:name>
</person>
```

## Event Iterator API

## XMLInputFactory 属性

| 属性                           | 说明                      | 返回类型    | 默认值   |
| ---------------------------- | ----------------------- | ------- | ----- |
| isValidating                 | 是否启用特定于实现的 DTD 验证       | Boolean | False |
| isNamespaceAware             | 是否启用命名空间处理，用于支持 XML 1.0 | Boolean | True  |
| isCoalescing                 | 是否合并相邻字符数据              | Boolean | False |
| isReplacingEntityReferences  | 是否将内部实体引用替换为实际文本        | Boolean | True  |
| isSupportingExternalEntities | 是否解析外部实体                | Boolean | False |
| supportDTD                   | 指定使用的处理器是否支持 DTD        | Boolean | True  |
| reporter                     |                         |         |       |

Specifies the implementation of javax.xml.stream.XMLReporter that should be used.Specifies the implementation of javax.xml.stream.XMLReporter that should be used.

XMLReporter

Null

resolver

Specifies the implementation of javax.xml.stream.XMLResolver that should be used.

XMLResolver

Null

allocator

Specifies the implementation of javax.xml.stream.util.XMLEventAllocator that should be used.

util.XMLEventAllocator

Null

## 参考

- https://docs.oracle.com/cd/E13222_01/wls/docs90/xml/stax.html