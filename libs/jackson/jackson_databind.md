# jackson-databind

@author Jiawei Mao
***
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
String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
Map<String, Object> map = 
    objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});
```

## 自定义

Jackson 库的最大优势是序列化和反序列化过程高度可定制。

### 设置序列化或反序列化 Feature

将 JSON 转换为 Java 类时，JSON 字符串可能包含新的字符，Jackson 默认会抛出异常。对 JSON 字符串

```java
String jsonString 
  = "{ \"color\" : \"Black\", \"type\" : \"Fiat\", \"year\" : \"1970\" }";
```

使用默认配置将其解析为 Car 会抛出 `UnrecognizedPropertyException` 异常。

通过设置，可以让 Jackson 忽略无法识别的字段：

```java
@Test
public void testUnkownProperties() throws Exception {
    String JSON_CAR = "{ \"color\" : \"Black\", \"type\" : \"Fiat\", \"year\" : \"1970\" }";

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Car car = objectMapper.readValue(JSON_CAR, Car.class);

    JsonNode jsonNodeRoot = objectMapper.readTree(JSON_CAR);
    JsonNode jsonNodeYear = jsonNodeRoot.get("year");

    assertNotNull(car);
    assertThat(car.getColor(), equalTo("Black"));
    assertThat(jsonNodeYear.asText(), is("1970"));
}
```

还有一个 `FAIL_ON_NULL_FOR_PRIMITIVES` Feature，定义是否允许基本类型值为 `null`：

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
```

`FAIL_ON_NUMBERS_FOR_ENUM` 指定是否将 enum 值序列化/反序列化为数字：

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
```

完整 Feature 列表可以参考 [Jackson Wiki](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features)。

### 创建自定义 Serializer 或 Deserializer

`ObjectMapper` 可以注册自定义 serializer 和 deserializer。

当 JSON 结构与对应的 Java 类不同时，自定义 serializer 和 deserializer 非常有用。

**示例：** 下面是一个自定义 JSON serializer

```java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CustomCarSerializer extends StdSerializer<Car> {

    public CustomCarSerializer() {
        this(null);
    }

    public CustomCarSerializer(final Class<Car> t) {
        super(t);
    }

    @Override
    public void serialize(Car car, JsonGenerator jsonGenerator,
                          SerializerProvider serializer) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("model: ", car.getType());
        jsonGenerator.writeEndObject();
    }
}
```

自定义 serializer 可以按如下方式调用：

```java
ObjectMapper mapper = new ObjectMapper();

SimpleModule serializerModule = new SimpleModule("CustomSerializer", 
        new Version(1, 0, 0, null, null, null));
serializerModule.addSerializer(Car.class, new CustomCarSerializer());
mapper.registerModule(serializerModule);

Car car = new Car("yellow", "renault");
String carJson = mapper.writeValueAsString(car);
assertThat(carJson, containsString("renault"));
assertThat(carJson, containsString("model"));
```

`carJson` 的内容：

```json
{"model: ":"renault"}
```

**示例：** 自定义 JSON deserializer

```java
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomCarDeserializer extends StdDeserializer<Car> {

    private final Logger Logger = LoggerFactory.getLogger(getClass());

    public CustomCarDeserializer() {
        this(null);
    }

    public CustomCarDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public Car deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Car car = new Car();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        try {
            final JsonNode colorNode = node.get("color");
            final String color = colorNode.asText();
            car.setColor(color);
        } catch (final Exception e) {
            Logger.debug("101_parse_exeption: unknown json.");
        }
        return car;
    }
}
```

调用自定义 deserializer：

```java
String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
ObjectMapper mapper = new ObjectMapper();
SimpleModule module =
    new SimpleModule("CustomCarDeserializer", new Version(1, 0, 0, null, null, null));
module.addDeserializer(Car.class, new CustomCarDeserializer());
mapper.registerModule(module);
Car car = mapper.readValue(json, Car.class);
```

### 处理 Date 数据

`java.util.Date` 默认序列化为一个数字，即从 1970 年 1 月 1 日开始的毫秒数。这种格式可读性太差，需要进一步抓换才能以人类可读的格式显示。

下面将 Car 实例封装到 Request 类中，并添加 datePurchased 属性：

```java
import java.util.Date;

public class Request {

    Car car;
    Date datePurchased;

    public Car getCar() {
        return car;
    }

    public void setCar(final Car car) {
        this.car = car;
    }

    public Date getDatePurchased() {
        return datePurchased;
    }

    public void setDatePurchased(final Date datePurchased) {
        this.datePurchased = datePurchased;
    }
}
```

按如下方式设置日期格式：

```java
ObjectMapper objectMapper = new ObjectMapper();
DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
objectMapper.setDateFormat(df);
String carAsString = objectMapper.writeValueAsString(request);
// output: {"car":{"color":"yellow","type":"renault"},"datePurchased":"2016-07-03 11:43 AM CEST"}
```

在某些情况下，我们需要在创建 SimpleDateFormat 时指定 Locale，以便在所有机器提供一致的输出。

```java
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a z", Locale.ENGLISH);
```

### 处理集合

Jackson 能够将 JSON 数组解析为我们想要的集合类型。

**示例：** 将 JSON 数组解析为 Java 数组：

```java
String jsonCarArray = 
  "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
Car[] cars = objectMapper.readValue(jsonCarArray, Car[].class);
// print cars
```

**示例：** 将 JSON 数组解析为 Java List

```java
String jsonCarArray = 
  "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
ObjectMapper objectMapper = new ObjectMapper();
List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
// print cars
```

## ObjectMapper Builder 模式

到目前为止，已经介绍了配置 `ObjectMapper` 的多种方法。而用 builder 模式可以简化 API 调用。

### ObjectMapperBuilder 类

下面创建 ObjectMapperBuilder 类，在其中包含 enableIdentation, preserveOrder 和 dateFormat 三种配置。

```java
public class ObjectMapperBuilder {
    private boolean enableIndentation;
    private boolean preserveOrder;
    private DateFormat dateFormat;
}
```

ObjectMapper 有大量可配置选项，我们只关注其中一小部分。

下面在 builder 中添加一些配置方法：

```java
ObjectMapperBuilder enableIndentation() {
    this.enableIndentation = true;
    return this;
}

ObjectMapperBuilder dateFormat() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Kolkata")));
    this.dateFormat = simpleDateFormat;
    return this;
}

ObjectMapperBuilder preserveOrder(boolean order) {
    this.preserveOrder = order;
    return this;
}
```

然后添加 build() 方法生成 ObjectMapper 实例：

```java
public ObjectMapper build() {
    ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.enableIndentation);
    objectMapper.setDateFormat(this.dateFormat);
    if (this.preserveOrder) {
        objectMapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    }

    return objectMapper;
}
```

### 使用 Builder

使用 ObjectMapperBuilder 创建 ObjectMapper 实例：

```java
ObjectMapper mapper = new ObjectMapperBuilder()
  .enableIndentation()
  .dateFormat()
  .preserveOrder(true)
  .build();
```

创建 Car 及序列化 JSON 字符串：

```java
Car givenCar = new Car("White", "Sedan");
String givenCarJsonStr = "{ \"color\" : \"White\", \"type\" : \"Sedan\" }";
```

使用 `mapper` deserialize `givenCarJsonStr`：

```java
Car actual = mapper.readValue(givenCarJsonStr, Car.class);

assertThat(actual.getColor(), is("White"));
assertThat(actual.getType(), is("Sedan"));
```

serialize Request 对象:

```java
Request request = new Request();
request.setCar(givenCar);
Date date = new Date(1684909857000L);
request.setDatePurchased(date);

String actual = mapper.writeValueAsString(request);
String expected = "{\n" + "  \"car\" : {\n" + "    \"color\" : \"White\",\n" +
        "    \"type\" : \"Sedan\"\n" + "  },\n" + "  \"datePurchased\" : \"2023-05-24 12:00 PM IST\"\n" +
        "}";
assertEquals(expected, actual);
```


## 参考

- https://www.baeldung.com/jackson-object-mapper-tutorial