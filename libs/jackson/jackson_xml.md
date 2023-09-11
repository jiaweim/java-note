# jackson-dataformat-xml

- [jackson-dataformat-xml](#jackson-dataformat-xml)
  - [简介](#简介)
  - [XmlMapper](#xmlmapper)
  - [jackson-xml 选项](#jackson-xml-选项)
  - [美观输出](#美观输出)
  - [序列化为 XML](#序列化为-xml)
    - [序列化为 XML String](#序列化为-xml-string)
    - [序列化为 XML 文件](#序列化为-xml-文件)
  - [Deserialize XML 为 Java](#deserialize-xml-为-java)
    - [Deserialize XML String](#deserialize-xml-string)
    - [Deserialize XML File](#deserialize-xml-file)
  - [处理大写元素名称](#处理大写元素名称)
    - [从 XML String 解析](#从-xml-string-解析)
    - [序列化为 XML String](#序列化为-xml-string-1)
  - [序列化 List](#序列化-list)
  - [参考](#参考)

2023-09-01, 10:42
@author Jiawei Mao
****

## 简介

jackson-dataformat-xml 是 Jackson 用于读写 XML 文件的扩展组件。

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.15.2</version>
</dependency>
```

## XmlMapper

`XmlMapper` 是 Jackson 解析 XML 的主要类。创建 `XmlMapper`：

```java
import com.fasterxml.jackson.xml.XmlMapper;

// with Jackson 2.10 and later
ObjectMapper mapper = XmlMapper.builder()
    // possible configuration changes
    .build();

// or, with Jackson versions before 2.10 (also exists for later versions)
ObjectMapper mapper = new XmlMapper();
```

## jackson-xml 选项

|注解|作用域|说明|应用场景|
|---|---|---|---|
|@JacksonXmlRootElement|类上|指定 XML 根元素名称|序列化|
|@JacksonXmlProperty|属性或 getter/setter 方法|	指定属性对应 XML 元素名称|序列化和反序列化|
|@JacksonXmlText|属性或 getter/setter方法|将属性直接作为未被标签包裹的普通文本|序列化和反序列化|
|@JacksonXmlCData|属性或 getter/setter方法|将属性值包裹在 CDATA 标签中|序列化|
|@JacksonXmlElementWrapper|属性或 getter/setter 方法上|类中有集合的属性时，是否生成包裹的标签|序列化时|



## 美观输出

```java
DefaultXmlPrettyPrinter xmlPrettyPrinter = new DefaultXmlPrettyPrinter();
// you can then configure the settings on the xmlPrettyPrinter instance
xmlMapper.setDefaultPrettyPrinter(xmlPrettyPrinter);
```



## 序列化为 XML

`XmlMapper` 是 `ObjectMapper` 的子类，在 ObjectMapper 的基础上添加了 XML 功能。

首先，创建一个 Java 类：

```java
class SimpleBean {

    private int x = 1;
    private int y = 2;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
```

### 序列化为 XML String

使用 `writeValueAsString` 将 Java 对象转换为 XML String:

```java
XmlMapper xmlMapper = new XmlMapper();
String xml = xmlMapper.writeValueAsString(new SimpleBean());
assertNotNull(xml);
```

得到的 XML：

```xml
<SimpleBean>
    <x>1</x>
    <y>2</y>
</SimpleBean>
```

### 序列化为 XML 文件

```java
XmlMapper xmlMapper = new XmlMapper();
xmlMapper.writeValue(new File("target/simple_bean.xml"), new SimpleBean());
File file = new File("target/simple_bean.xml");
assertNotNull(file);
```

## Deserialize XML 为 Java

### Deserialize XML String

```java
XmlMapper xmlMapper = new XmlMapper();
SimpleBean value = xmlMapper.readValue("<SimpleBean><x>1</x><y>2</y></SimpleBean>", SimpleBean.class);
assertTrue(value.getX() == 1 && value.getY() == 2);
```

### Deserialize XML File

```java
File file = new File("src/test/resources/simple_bean.xml");
XmlMapper xmlMapper = new XmlMapper();
SimpleBean value = xmlMapper.readValue(file, SimpleBean.class);
assertTrue(value.getX() == 1 && value.getY() == 2);
```

## 处理大写元素名称

Java 类中字段一般为小写，如果 XML 中元素名称为大写，那么如何自动解析为字段？

### 从 XML String 解析

假设 XML 有个字段为大写：

```xml
<SimpleBeanForCapitalizedFields>
    <X>1</X>
    <y>2</y>
</SimpleBeanForCapitalizedFields>
```

为了正确处理元素名，可以用 `@JsonProperty` 注释说明：

```java
class SimpleBeanForCapitalizedFields {

    @JsonProperty("X")
    private int x = 1;
    private int y = 2;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
```

然后就能用标准流程解析：

```java
XmlMapper xmlMapper = new XmlMapper();
SimpleBeanForCapitalizedFields value = xmlMapper.readValue("<SimpleBeanForCapitalizedFields><X>1</X><y>2</y></SimpleBeanForCapitalizedFields>", 
                SimpleBeanForCapitalizedFields.class);
assertTrue(value.getX() == 1 && value.getY() == 2);
```

### 序列化为 XML String

使用 `@JsonPropert` 注释后，字段也能正确序列化为对应名称：

```java
XmlMapper xmlMapper = new XmlMapper();
xmlMapper.writeValue(new File("target/simple_bean_capitalized.xml"), new SimpleBeanForCapitalizedFields());
File file = new File("target/simple_bean_capitalized.xml");
assertNotNull(file);
```

文件内容：

```xml
<SimpleBeanForCapitalizedFields>
    <y>2</y>
    <X>1</X>
</SimpleBeanForCapitalizedFields>
```

## 序列化 List

Person 中包含 Address List，目标 XML：

```xml
<Person>
    <firstName>Rohan</firstName>
    <lastName>Daye</lastName>
    <phoneNumbers>
        <phoneNumbers>9911034731</phoneNumbers>
        <phoneNumbers>9911033478</phoneNumbers>
    </phoneNumbers>
    <address>
        <streetName>Name1</streetName>
        <city>City1</city>
    </address>
    <address>
        <streetName>Name2</streetName>
        <city>City2</city>
    </address>
</Person>
```

!!! warning
    这里 `phoneNumbers` 


## 参考

- https://github.com/FasterXML/jackson-dataformat-xml
- https://www.baeldung.com/jackson-xml-serialization-and-deserialization