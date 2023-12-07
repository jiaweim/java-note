# Gson

- [Gson](#gson)
  - [简介](#简介)
  - [基本类型](#基本类型)
  - [对象类型](#对象类型)
  - [对象序列化要点](#对象序列化要点)
  - [嵌套类（包括内部类）](#嵌套类包括内部类)
  - [数组](#数组)
  - [集合](#集合)
  - [Map](#map)
  - [泛型序列化](#泛型序列化)
  - [任意对象类型集合](#任意对象类型集合)
  - [内置 Serializer 和 Deserializer](#内置-serializer-和-deserializer)
  - [自定义 Serialization 和 Deserialization](#自定义-serialization-和-deserialization)
    - [自定义 Serializer](#自定义-serializer)
    - [自定义 Deserializer](#自定义-deserializer)
    - [优点](#优点)
  - [Compact vs. Pretty Printing](#compact-vs-pretty-printing)
  - [Null 对象](#null-对象)
  - [Streaming](#streaming)
    - [解析 JSON](#解析-json)
  - [参考](#参考)

2022-05-11, 23:30
***

## 简介

Gson 是一个用于将 Java 对象序列化为 JSON 格式的 Java 库。

Gson 的目标：

- 提供易于使用的 API，如 `toString()`、构造函数以及工厂方法将 Java 转换为 JSON，反之亦然
- 允许将不可修改的对象与 JSON 进行转换
- 允许自定义对象表示
- 支持复杂对象
- 生成紧凑且可读的 JSON 输出

gson 使用的主要类是 `Gson`，可以调用 `new Gson()` 创建，或使用 `GsonBuilder` 创建。

`Gson` 实例在调用 Json 操作时不维护任何状态，因此可以使用相同 `Gson` 实例进行多个 JSON 序列化和反序列化操作。

## 基本类型

- 序列化

```java
Gson gson = new Gson();

assertEquals(gson.toJson(1), "1");
assertEquals(gson.toJson("abcd"), "\"abcd\"");
assertEquals(gson.toJson(Long.valueOf(10)), "10");
assertEquals(gson.toJson(new int[]{1}), "[1]");
```

- 反序列化

```java
assertEquals(gson.fromJson("1", int.class), 1);
assertEquals(gson.fromJson("1", Integer.class), Integer.valueOf(1));
assertEquals(gson.fromJson("1", Long.class), Long.valueOf(1));
assertEquals(gson.fromJson("false", Boolean.class), Boolean.FALSE);
assertEquals(gson.fromJson("\"abc\"", String.class), "abc");
assertArrayEquals(gson.fromJson("[\"abc\"]", String[].class), new String[]{"abc"});
```

## 对象类型

```java
class BagOfPrimitives {
    private int value1 = 1;
    private String value2 = "abc";
    private transient int value3 = 3;

    BagOfPrimitives() {
        // no-args constructor
    }
}
```

- 序列化

```java
BagOfPrimitives obj = new BagOfPrimitives();
Gson gson = new Gson();
String json = gson.toJson(obj);
assertEquals(json, "{\"value1\":1,\"value2\":\"abc\"}");
// {"value1":1,"value2":"abc"}
```

字段名称转换为字符串，没有序列化 `transient` 字段。

!!! note
    带**循环引用**的对象会导致无线递归，无法序列化。

- 反序列化

```java
BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);
```

## 对象序列化要点

- 可以使用 `private` 字段，且推荐使用
- 不需要使用注释来指定序列化或去序列化字段。默认包含当前类的所有字段（包括父类）
- 默认忽略 `transient` 字段，序列化和反序列化时均跳过
- null 值处理
  - 序列化时，忽略 null 值
  - 反序列化时，JSON 中的缺少项在对象字段中设置为默认值，对象类型为 `null`，数值类型为 0，布尔值为 `false`
- 合成字段在 JSON 序列化和反序列化时均被忽略
- 内部类中使用的外部类字段被忽略
- 不包括匿名类和本地类。它们被序列化为 JSON null，反序列化时，它们的 JSON 值被忽略并返回 null。将匿名类和本地类转换为 static 嵌套类，可以实现序列化和反序列化。 

## 嵌套类（包括内部类）

Gson 可以很容易序列化静态嵌套类。

也可以反序列化静态嵌套类。但是**无法自动反序列化纯内部类**，因为调用纯内部类的无参构造函数需要外部对象引用，在反序列化时不可用。

解决方法有两种：

- 一个是将内部类换成静态内部类
- 提供自定义 `InstanceCreator`

例如：

```java
public class A {
    public String a;

    class B {

        public String b;

        public B() {
            // No args constructor for B
        }
    }
}
```

**NOTE**：`B` 类默认无法使用 Gson 序列化。

Gson 无法将 `{"b":"abc"}` 反序列化为类 `B`，因此 `B` 是内部类。如果 `B` 是静态内部类就可以，或者为 `B` 实现一个 `InstanceCreator`：

```java
import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;

public class InstanceCreatorForB implements InstanceCreator<A.B> {

    private final A a;

    public InstanceCreatorForB(A a) {
        this.a = a;
    }

    public A.B createInstance(Type type) {
        return a.new B();
    }
}
```

这样是可以序列化 `B`，但是不推荐。

## 数组

- 序列化

```java
Gson gson = new Gson();
int[] ints = {1, 2, 3, 4, 5};
String[] strings = {"abc", "def", "ghi"};
assertEquals(gson.toJson(ints), "[1,2,3,4,5]");
assertEquals(gson.toJson(strings), "[\"abc\",\"def\",\"ghi\"]");
```

- 反序列化

```java
int[] ints2 = gson.fromJson("[1,2,3,4,5]", int[].class);
```

Gson 多维数组，以及任意元素类型的数组。

## 集合

- 序列化

```java
Gson gson = new Gson();
Collection<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

String json = gson.toJson(ints);
assertEquals(json, "[1,2,3,4,5]");
```

- 反序列化

```java
TypeToken<Collection<Integer>> collectionType = new TypeToken<>() {};
Collection<Integer> ints2 = gson.fromJson(json, collectionType);
assertIterableEquals(ints2, ints);
```

上面比较麻烦的一点是，需要定义集合类型。在 Java 中没有办法解决这个问题。

**集合限制**

Gson 可以序列化任意对象的集合，但无法反序列化，因为没有办法让用户指定返回的集合类型。而且，反序列化时，集合必须是特定泛型。

## Map

Gson 默认将 `java.util.Map` 序列化为 JSON 对象。因为 JSON 对象只支持字符串作为成员名，因此 Gson 调用 `toString()` 将 Map 的 key 转换为字符串，null 则转换为 `"null"`。

- 序列化

```java
Gson gson = new Gson();
Map<String, String> stringMap = new LinkedHashMap<>();
stringMap.put("key", "value");
stringMap.put(null, "null-entry");

String json = gson.toJson(stringMap);
// {"key":"value","null":"null-entry"}
assertEquals(json, "{\"key\":\"value\",\"null\":\"null-entry\"}");

Map<Integer, Integer> intMap = new LinkedHashMap<>();
intMap.put(2, 4);
intMap.put(3, 6);
// {"2":4,"3":6}
String json1 = gson.toJson(intMap);
assertEquals(json1, "{\"2\":4,\"3\":6}");
```

- 反序列化

Gson 使用为 `Map` key 类型注册的 `TypeAdapter` 的 `read` 方法反序列化。和集合类似，反序列化 Map 需要使用 `TypeToken` 告诉 Gson `Map` key 和 value 的类型：

```java
Gson gson = new Gson();
TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {};
String json = "{\"key\": \"value\"}";

Map<String, String> stringMap = gson.fromJson(json, mapType);
// ==> stringMap is {key=value}
```

- 复杂类型序列化

Gson 支持复杂类型的 `Map` keys。该特性通过 `GsonBuilder.enableComplexMapKeySerialization()` 启用。启用后，Gson 使用为 `Map` key 类型注册的 `TypeAdapter` 的 `write` 方法序列化 key，而不是默认的 `toString()`。如果 Map 的任意 key 被 TypeAdapter 序列化为 JSON 数组或 JSON 对象，Gson 将把整个 Map 序列化为 JSON 数组；否则 Gson 依然将 Map 序列化为 JSON 对象：

```java
Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
Map<PersonName, Integer> complexMap = new LinkedHashMap<>();
complexMap.put(new PersonName("John", "Doe"), 30);
complexMap.put(new PersonName("Jane", "Doe"), 35);

// 复杂 key 类型，序列化为 JSON 数组
String json = gson.toJson(complexMap);
// [[{"firstName":"John","lastName":"Doe"},30],[{"firstName":"Jane","lastName":"Doe"},35]]

// 简单 key 类型，序列化为 JSON 对象
Map<String, String> stringMap = new LinkedHashMap<>();
stringMap.put("key", "value");
String json2 = gson.toJson(stringMap);
// {"key":"value"}
```

!!! attention
    因为 Gson 默认使用 `toString()` 序列化 Map 的 keys，当 toString() 实现不对，可能导致编码 key 的格式不正确，或者 key 的序列化和反序列化不匹配。解决该问题的一个方法是使用 `enableComplexMapKeySerialization()` 切换为 TypeAdapter 实现序列化和反序列化。

将 enum 反序列化为 Map key 时，如果 Gson 无法找到匹配 `name()` 的 enum 常量，它将返回通过其 `toString()` 值查找 enum 常量。

## 泛型序列化

当调用 `toJson(obj)`，Gson 调用 `obj.getClass()` 获取要序列化的字段信息。类似的，可以传递 `MyClass.class` 对象给 `fromJson(json, MyClass.class)` 方法。如果对象是非泛型类型，该方法很好。如果对象是泛型，则由于 Java 类型擦除，泛型类型信息丢失。例如：

```java
class Foo<T> {
  T value;
}
Gson gson = new Gson();
Foo<Bar> foo = new Foo<Bar>();
gson.toJson(foo); // 不能正确序列化 foo.value

gson.fromJson(json, foo.getClass()); // Fails to deserialize foo.value as Bar
```

上面无法将值解析为 `Bar` 类型，因为 `Gson` 调用 `foo.getClass()` 获取类型信息，但该方法返回的是原始类 `Foo.class`。即 Gson 不知道这是 `Foo<Bar>` 泛型类型。

该问题可以通过为泛型类型指定参数化类型来解决。使用 `TypeToken` ：

```java
Type fooType = new TypeToken<Foo<Bar>>() {}.getType();
gson.toJson(foo, fooType);

gson.fromJson(json, fooType);
```

用于获取 `fooType` 的语法实际上定义了一个匿名内部类，包含一个返回参数化类型的方法 `getType()`。

## 任意对象类型集合

包含混合类型的 JSON 数组，如 `['hello',5,{name:'GREETINGS',source:'guest'}]`。

对应的 `Collection` 如下：

```java
Collection collection = new ArrayList();
collection.add("hello");
collection.add(5);
collection.add(new Event("GREETINGS", "guest"));
```

`Event` 类定义：

```java
class Event {
    private String name;
    private String source;

    private Event(String name, String source) {
        this.name = name;
        this.source = source;
    }
}
```

无需执行特殊操作，使用 `toJson(collectin)` 就能将其正确序列化。

但是，使用 `fromJson(json, Collection.class)` 进行反序列化行不通，Gson 不知道将输入映射到哪种类型。Gson 需要你在 `fromJson()` 中提供集合类型的泛型版本。有三种实现方式：

1. 使用 Gson 底层流式解析器 API 或 DOM 解析器 `JsonParser` 解析数组元素，然后在每个数组元素上使用 `Gson.fromJson()`，这是**推荐**方法。
2. 为 Collection.class 注册一个 typeAdapter。缺点是会破坏其它集合类型的反序列化。
3. 为 MyCollectionMemberType 注册一个 typeAdapter，然后结合 `Collection<MyCollectionMemberType>` 调用 `fromJson()`。

当数组为顶层元素，或者能够将持有集合的字段类型修改为 `Collection<MyCollectionMemberType>` 才能用。

方法 1 示例：

```java
static class Event {
    private String name;
    private String source;

    private Event(String name, String source) {
        this.name = name;
        this.source = source;
    }

    @Override
    public String toString() {
        return String.format("(name=%s, source=%s)", name, source);
    }
}

@Test
void rawCollect() {
    Gson gson = new Gson();
    Collection collection = new ArrayList();
    collection.add("hello");
    collection.add(5);
    collection.add(new Event("GREETINGS", "guest"));
    String json = gson.toJson(collection);

    System.out.println("Using Gson.toJson() on a raw collection: " + json);
    JsonArray array = JsonParser.parseString(json).getAsJsonArray();
    String message = gson.fromJson(array.get(0), String.class);
    int number = gson.fromJson(array.get(1), int.class);
    Event event = gson.fromJson(array.get(2), Event.class);
    System.out.printf("Using Gson.fromJson() to get: %s, %d, %s", message, number, event);
}
```

## 内置 Serializer 和 Deserializer

对默认表示形式不合适的常用类，Gson 提供了内置序列化器和反序列化器，例如：

- `java.net.URL` 匹配 `"https://github.com/google/gson/"` 样式的字符串
- `java.net.URI` 匹配 `"/google/gson/"` 样式字符串

更多内置序列化器可参考 [TypeAdapters](https://github.com/google/gson/blob/main/gson/src/main/java/com/google/gson/internal/bind/TypeAdapters.java) 的内部类。

## 自定义 Serialization 和 Deserialization

当默认表示不是你想要的，Gson 支持注册自定义 serializers 和 deserializers。分两部分：

- JSON Serializer：自定义对象的序列化
- JSON Deserializer：自定义对象的反序列化
- Instance Creator：如果有无参构造函数或注册的 Deserializer，则不需要

```java
GsonBuilder gson = new GsonBuilder();
gson.registerTypeAdapter(MyType2.class, new MyTypeAdapter());
gson.registerTypeAdapter(MyType.class, new MySerializer()); // 序列化
gson.registerTypeAdapter(MyType.class, new MyDeserializer()); // 去序列化
gson.registerTypeAdapter(MyType.class, new MyInstanceCreator()); // 创建实例
```

`registerTypeAdapter` 调用会检查：

1. 如果 typeAdapter 实现了多个接口，它将为所有这些接口注册适配器
2. 如果 typeAdapter 是用于 Object 类或 JsonElement 及其子类，抛出 `IllegalArgumentException` 

### 自定义 Serializer

为 Joda DateTime 类编写自定义 serializer：

```java
private class DateTimeSerializer implements JsonSerializer<DateTime> {
  public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.toString());
  }
}
```

Gson 在序列化 DateTime 对象时会调用 `serialize()` 方法。

### 自定义 Deserializer

JodaTime `DateTime` 的自定义 deserializer：

```java
private class DateTimeDeserializer implements JsonDeserializer<DateTime> {
  public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return new DateTime(json.getAsJsonPrimitive().getAsString());
  }
}
```

Gson 将 JSON string 反序列化为 DateTime 时会调用 `deserialize`。

### 优点

通常希望与原始类型对应的所有泛型注册单个 handler：

- 例如，假设有一个 `Id` 类
- `Id<T>` 类型，对所有泛型类型具有相同的序列化
  - 即输出 id 值
- 反序列化类似，但不完全相同
  - 

## Compact vs. Pretty Printing

Gson 提供的默认 JSON 输出是紧凑型。即输出 JSON 中没有任何空格，跳过 null 值（集合和数组中 null 保留）。

如果想要 Pretty 输出，必须使用 `GsonBuilder` 配置 `Gson`。`JsonFormatter` 不是 public API，所以无法配置格式，目前格默认 line 长度为 80 字符，2 字符缩进，4 字符右边距。

示例：

```java
Gson gson = new GsonBuilder().setPrettyPrinting().create();
String jsonOutput = gson.toJson(someObject);
```

## Null 对象

## Streaming

除了 Gson 的对象模型和数据绑定，还可以使用 Gson 流进行读写。也可以结合流和对象模型一起使用。

流式 API 逐个读取 JSON token：

- `JsonReader` 负责读取 JSON
- `JsonWriter` 负责输出 JSON

### 解析 JSON

- 创建 `JsonReader`

```java
public JsonReader(Reader in) 
```

构造函数很简单。



## 参考

- https://github.com/google/gson/blob/main/UserGuide.md