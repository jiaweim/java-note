# Gson

- [Gson](#gson)
  - [简介](#简介)
  - [使用](#使用)
    - [基本类型](#基本类型)
    - [对象类型](#对象类型)
    - [对象类型细节](#对象类型细节)
    - [嵌套类](#嵌套类)
    - [数组](#数组)
    - [集合](#集合)
    - [泛型序列化](#泛型序列化)
    - [任意类型集合](#任意类型集合)

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

## 使用

使用的主要类是 `Gson`，可以调用 `new Gson()` 创建，或使用 `GsonBuilder` 创建。

`Gson` 实例在调用 Json 操作时不维护任何状态，因此可以使用相同对象进行多个 JSON 序列化和反序列化操作。

### 基本类型

```java
// Serialization
Gson gson = new Gson();
gson.toJson(1);            // ==> 1
gson.toJson("abcd");       // ==> "abcd"
gson.toJson(new Long(10)); // ==> 10
int[] values = { 1 };
gson.toJson(values);       // ==> [1]

// Deserialization
int one = gson.fromJson("1", int.class);
Integer one = gson.fromJson("1", Integer.class);
Long one = gson.fromJson("1", Long.class);
Boolean false = gson.fromJson("false", Boolean.class);
String str = gson.fromJson("\"abc\"", String.class);
String[] anotherStr = gson.fromJson("[\"abc\"]", String[].class);
```

### 对象类型

```java
class BagOfPrimitives {
  private int value1 = 1;
  private String value2 = "abc";
  private transient int value3 = 3;
  BagOfPrimitives() {
    // no-args constructor
  }
}

// Serialization
BagOfPrimitives obj = new BagOfPrimitives();
Gson gson = new Gson();
String json = gson.toJson(obj);  

// ==> json is {"value1":1,"value2":"abc"}
```

不能序列化带有循环引用的对象，因为这会导致无限递归。

```java
// Deserialization
BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);
// ==> obj2 is just like obj
```

### 对象类型细节

- 使用 private 字段是正确的，并且推荐使用
- 不需要使用注释来指定序列化或去序列化的字段。默认包含当前类的所有字段
- 默认忽略 `transient` 字段
- 空值处理
  - 序列化时，忽略空值
  - 反序列化时，JSON 中缺少项，在对象字段中设置为默认值，对象类型为 null，数值类型为 0，布尔值为 false
  - 合成字段在 JSON 序列化和反序列化时均被忽略
  - 内部类、匿名类和本地类中外部类的字段被忽略

### 嵌套类

Gson 可以很容易地序列化静态嵌套类，也可以反序列化静态嵌套类。但是**无法自动对纯内部类进行反序列化**，因为纯内部类的无参构造函数需要外部对象引用，在反序列化时不可用。

解决方法有两种，一个是将内部类换成静态内部类，或者提供自定义 `InstanceCreator`。例如：

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

> B 类使用 Gson 无法自动序列化。

Gson 无法将 `{"b":"abc"}` 反序列化为类 B，因此 B 是内部类。如果 B 是静态内部类就可以，或者自定义实现 `InstanceCreator`：

```java
public class InstanceCreatorForB implements InstanceCreator<A.B> {
  private final A a;
  public InstanceCreatorForB(A a)  {
    this.a = a;
  }
  public A.B createInstance(Type type) {
    return a.new B();
  }
}
```

这样是可以的，但是不推荐。

### 数组

```java
Gson gson = new Gson();
int[] ints = {1, 2, 3, 4, 5};
String[] strings = {"abc", "def", "ghi"};

// Serialization
gson.toJson(ints);     // ==> [1,2,3,4,5]
gson.toJson(strings);  // ==> ["abc", "def", "ghi"]

// Deserialization
int[] ints2 = gson.fromJson("[1,2,3,4,5]", int[].class); 
// ==> ints2 will be same as ints
```

Gson 还支持任意复杂元素类型的多维数组。

### 集合

```java
Gson gson = new Gson();
Collection<Integer> ints = Lists.immutableList(1,2,3,4,5);

// Serialization
String json = gson.toJson(ints);  // ==> json is [1,2,3,4,5]

// Deserialization
Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
Collection<Integer> ints2 = gson.fromJson(json, collectionType);
// ==> ints2 is same as ints
```

上面比较麻烦的一点是，需要定义集合类型。在 Java 中没有办法解决这个问题。

**集合限制**

 Gson 可以序列化任意对象的集合，但无法反序列化，因为没有办法让用户指定结果对象类型。因此，在进行反序列化时，集合必须是特定泛型。

### 泛型序列化

当调用 `toJson(obj)`，Gson 调用 `obj.getClass()` 获取要序列化的字段信息。类似的，可以传递 `MyClass.class` 对象给 `fromJson(json, MyClass.class)` 方法。如果对象是非泛型类型，该方法工作很好。如果对象是泛型，则由于 Java 类型擦除，泛型类型信息丢失。例如：

```java
class Foo<T> {
  T value;
}
Gson gson = new Gson();
Foo<Bar> foo = new Foo<Bar>();
gson.toJson(foo); // May not serialize foo.value correctly

gson.fromJson(json, foo.getClass()); // Fails to deserialize foo.value as Bar
```

上面的代码无法将值解析为 `Bar` 类型，因为 `Gson` 调用 `foo.getClass()` 获取类型信息，但该方法返回的是原始类 `Foo.class`。即 Gson 无法知道这是 `Foo<Bar>` 泛型类型。

这可以通过为泛型类型指定正确的参数化类型来解决。使用 `TypeToken` 类型。

```java
Type fooType = new TypeToken<Foo<Bar>>() {}.getType();
gson.toJson(foo, fooType);

gson.fromJson(json, fooType);
```

用于获取 `fooType` 习惯用法实际上定义了一个匿名内部类，去包含一个返回完全参数化类型的方法 `getType()`。

### 任意类型集合

包含混合类型的 JSON 数组，如 `['hello',5,{name:'GREETINGS',source:'guest'}]`。

对应的 `Collection` 如下：

```java
Collection collection = new ArrayList();
collection.add("hello");
collection.add(5);
collection.add(new Event("GREETINGS", "guest"));
```

`Event` 类定义为：

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

无需执行特殊操作，使用 `toJson(collectin)` 就会得到预期输出。

但是，使用 `fromJson(json, Collection.class)` 进行反序列化就行不通，因为 Gson 不知道将输入映射到哪种类型。Gson 需要你在 `fromJson()` 中提供集合类型的泛型版本。有三种实现方式：

1. 使用 Gson 底层流式解析器 API 或 DOM 解析器 JsonParser解析数组元素，然后在每个数组元素上使用 `Gson.fromJson()`，这是首选方法。
