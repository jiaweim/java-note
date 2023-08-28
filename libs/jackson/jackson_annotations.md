# jackson-annotations

- [jackson-annotations](#jackson-annotations)
  - [简介](#简介)
  - [Serialization 注解](#serialization-注解)
    - [@JsonAnyGetter](#jsonanygetter)
    - [@JsonGetter](#jsongetter)
    - [@JsonPropertyOrder](#jsonpropertyorder)
    - [@JsonRawValue](#jsonrawvalue)
  - [参考](#参考)

****

## 简介

见到介绍 jackson 数据处理器的通用注解。

## Serialization 注解

### @JsonAnyGetter

`@JsonAnyGetter` 注解可以将 `Map` 字段作为标准属性。

**示例：** `ExtendableBean` 具有 `name` 属性和一组 key/value 可扩展 Map 属性

```java
public class ExtendableBean {
    public String name;
    private Map<String, String> properties;

    public ExtendableBean() {
        properties = new HashMap<>();
    }

    public ExtendableBean(final String name) {
        this.name = name;
        properties = new HashMap<>();
    }

    @JsonAnySetter
    public void add(final String key, final String value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }
}
```

将其序列化，`Map` 的所有键值对被序列化为标准属性：

```json
{
    "name":"My bean",
    "attr2":"val2",
    "attr1":"val1"
}
```

操作代码：

```java
ExtendableBean bean = new ExtendableBean("My bean");
bean.add("attr1", "val1");
bean.add("attr2", "val2");

String result = new ObjectMapper().writeValueAsString(bean);

assertThat(result, containsString("attr1"));
assertThat(result, containsString("val1"));
```

### @JsonGetter

`@JsonGetter` 注解与 `@JsonProperty` 注解功能相同，将方法标记为 getter 方法。

**示例：** 使用 @JsonGetter 注解将 getTheName() 方法指定为 name 属性的 getter 方法

```java
public class MyBean {
    public int id;
    private String name;

    @JsonGetter("name")
    public String getTheName() {
        return name;
    }
}
```

使用：

```java
MyBean bean = new MyBean(1, "My bean");

String result = new ObjectMapper().writeValueAsString(bean);

assertThat(result, containsString("My bean"));
assertThat(result, containsString("1"));
```

### @JsonPropertyOrder

@JsonPropertyOrder 注解指定属性序列化顺序。

**示例：** 设置 MyBean 属性的序列化顺序

```java
@JsonPropertyOrder({ "name", "id" })
public class MyBean {
    public int id;
    public String name;
}
```

输出：

```json
{
    "name":"My bean",
    "id":1
}
```

还可以使用 `@JsonPropertyOrder(alphabetic=true)` 按字母顺序序列化属性。

### @JsonRawValue

`@JsonRawValue` 注解让 Jackson 按照原样序列化属性。

**示例：** 使用 `@JsonRawValue` 注解嵌入自定义 JSON 作为字段值

```java
public class RawBean {
    public String name;

    @JsonRawValue
    public String json;
}
```

输出：

```json
{
    "name":"My bean",
    "json":{
        "attr":false
    }
}
```

测试代码：

```java
RawBean bean = new RawBean("My bean", "{\"attr\":false}");

String result = new ObjectMapper().writeValueAsString(bean);
assertThat(result, containsString("My bean"));
assertThat(result, containsString("{\"attr\":false}"));
```



## 参考

- https://github.com/FasterXML/jackson-annotations
- https://www.baeldung.com/jackson-annotations
- https://www.baeldung.com/jackson-advanced-annotations