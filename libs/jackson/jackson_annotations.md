# jackson-annotations

- [jackson-annotations](#jackson-annotations)
  - [简介](#简介)
  - [Serialization 注释](#serialization-注释)
    - [@JsonAnyGetter](#jsonanygetter)
  - [参考](#参考)

****

## 简介

该项目包含 jackson 数据处理器的通用注释。

## Serialization 注释

### @JsonAnyGetter

`@JsonAnyGetter` 注释可以灵活地将 `Map` 字段作为标准属性。

例如，ExtendableBean 具有 name 属性和一组 key/value 可扩展属性：

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
@Test
public void whenSerializingUsingJsonAnyGetter_thenCorrect()
  throws JsonProcessingException {
 
    ExtendableBean bean = new ExtendableBean("My bean");
    bean.add("attr1", "val1");
    bean.add("attr2", "val2");

    String result = new ObjectMapper().writeValueAsString(bean);
 
    assertThat(result, containsString("attr1"));
    assertThat(result, containsString("val1"));
}
```





## 参考

- https://github.com/FasterXML/jackson-annotations
- https://www.baeldung.com/jackson