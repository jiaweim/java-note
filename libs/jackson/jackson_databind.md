# jackson-databind

## 简介

jackson-databind 包含通用数据绑定功能和 jackson 数据处理器的树模型。它建立在 jackson-core 和 jackson-annotation 基础之上。

## 使用 ObjectMapper 读写

从基本的读写操作开始。`ObjectMapper` 的读写操作：

- `readValue` 解析或反序列化 JSON 内容为 Java 对象
- `writeValue` 将 Java 对象序列化为 JSON 输出

接下来使用具有两个字段的 `Car` 作为序列化和反序列化对象：

```java
public class Car {

    private String color;
    private String type;

    public Car() {}

    public Car(final String color, final String type) {
        this.color = color;
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
```

### 序列化为 JSON

使用 `ObjectMapper` 类的 `writeValue` 方法将 Java 对象序列化为 JSON：

```java
@Test
public void testWriteToFile(@TempDir Path folder) throws Exception {
    File resultFile = folder.resolve("car.json").toFile();

    ObjectMapper objectMapper = new ObjectMapper();
    Car car = new Car("yellow", "renault");
    objectMapper.writeValue(resultFile, car);

    Car fromFile = objectMapper.readValue(resultFile, Car.class);
    assertThat(car.getType(), is(fromFile.getType()));
    assertThat(car.getColor(), is(fromFile.getColor()));
}
```

输出文件内容为：

```json
{"color":"yellow","type":"renault"}
```

`ObjectMapper` 的 `writeValueAsString` 和 `writeValueAsBytes` 将 Java 对象序列化为 JSON，然后以字符串或字节数组形式返回。

```java
@Test
public void whenWriteJavaToJson_thanCorrect() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Car car = new Car("yellow", "renault");
    String carAsString = objectMapper.writeValueAsString(car);
    
    assertThat(carAsString, containsString("yellow"));
    assertThat(carAsString, containsString("renault"));
}
```

### JSON 反序列

使用 `ObjectMapper` 将 JSON String 转换为 Java 对象：

```java
final String EXAMPLE_JSON = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";

@Test
void whenReadJsonToJava_thanCorrect() throws Exception {
    final ObjectMapper objectMapper = new ObjectMapper();
    final Car car = objectMapper.readValue(EXAMPLE_JSON, Car.class);

    assertThat(car, is(notNullValue()));
    assertThat(car.getColor(), is("Black"));
}
```

`readValue` 也支持其它格式的输入，如包含 JSON 的文件：

```java
@Test
public void wheReadFromFile_thanCorrect() throws Exception {
    File resource = new File("src/main/resources/json_car.json");

    ObjectMapper objectMapper = new ObjectMapper();
    Car fromFile = objectMapper.readValue(resource, Car.class);

    assertThat("BMW", is(fromFile.getType()));
    assertThat("Black", is(fromFile.getColor()));
}
```

文件内容：

```json
{
  "color": "Black",
  "type": "BMW"
}
```

从 URL 读取：

```java
@Test
public void testReadFromUrl() throws Exception {
    URL resource = new URL("file:src/main/resources/json_car.json");

    ObjectMapper objectMapper = new ObjectMapper();
    Car fromFile = objectMapper.readValue(resource, Car.class);

    assertThat("BMW", is(fromFile.getType()));
    assertThat("Black", is(fromFile.getColor()));
}
```

### 反序列化为 JsonNode

将 JSON 解析为 JsonNode 对象：

```java
@Test
public void testReadTree() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(EXAMPLE_JSON);
    assertThat(jsonNode, is(notNullValue()));
    assertThat(jsonNode.get("color").asText(), is("Black"));
}
```

### 从 Json Array 创建 Java List

```java
@Test
void testReadJsonToList() throws Exception {
    String LOCAL_JSON = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"BMW\" }]";
    ObjectMapper objectMapper = new ObjectMapper();
    List<Car> listCar = objectMapper.readValue(LOCAL_JSON, new TypeReference<List<Car>>() {

    });
    for (final Car car : listCar) {
        assertThat(car, notNullValue());
        assertThat(car.getType(), equalTo("BMW"));
    }
}
```

### 从 Json String 创建 Java Map


```java
@Test
public void testReadJsonToMap() throws Exception {
    final ObjectMapper objectMapper = new ObjectMapper();
    final Map<String, Object> map = objectMapper.readValue(EXAMPLE_JSON, new TypeReference<Map<String, Object>>() {
    });
    assertThat(map, is(notNullValue()));
    for (final String key : map.keySet()) {
        assertThat(key, is(notNullValue()));
    }
}
```

## 自定义

Jackson 库的最大优势是序列化和反序列化过程高度可定制。

### 设置序列化或反序列化 Feature

将 JSON 转换为 Java 类时，JSON 字符串可能包含新的字符，Jackson 默认会抛出异常：

```java

```


## 参考

- https://www.baeldung.com/jackson-object-mapper-tutorial