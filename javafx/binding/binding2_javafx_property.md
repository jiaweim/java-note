# JavaFX 属性

2023-06-27, 16:04
****
## 1. 简介

JavaFX 对属性、事件和绑定具有良好支持。JavaFX 的所有属性都是 observable，可以监听属性的失效或值的变化。JavaFX 属性包含 read-write 和 read-only 类型，所有 read-write 属性支持绑定。

在 JavaFX 中，属性可以是单个值或一组值。这里介绍单值属性，后面会介绍集合属性。

在 JavaFX 中，属性均定义为单独的类。如 `IntegerProperty`, `DoubleProperty`, `StringProperty` 类分别定义 `int`, `double` 和 `String` 类型属性。这些都是抽象类，它们均有两种具体实现：

- read-write 实现，如 `SimpleDoubleProperty` 为 `DoubleProperty` 的 read-write 实现
- read-only 实现，如 `ReadOnlyDoubleWrapper` 为 `DoubleProperty` 的 read-only 实现

下面创建一个初始值为 100 的 `IntegerProperty`：

```java
IntegerProperty counter = new SimpleIntegerProperty(100);
```

## 2. getter 和 setter

`Property` 类提供了两对 getter 和 setter 方法：

- `get()`/`set()`，用于基本类型，如 `IntegerProperty` 的 `get(`) 返回值和 `set()` 参数为 `int` 类型
- `getValue()`/`setValue()`，用于对象类型，如 `IntegerProperty` 的 `getValue()` 返回值和 `setValue()` 参数为 `Integer` 类型

```ad-tip
对引用类型属性，如 `StringProperty` 和 `ObjectProperty<T>`，两对 getter 和 setter 方法都采用对象类型，即 `StringProperty` 的 `get()` 和 `getValue()` 都返回 `String`，`set()` 和 `setValue()` 的参数都是 `String`。基本类型由于自动装箱，采用哪个版本的 getter 和 setter 都行，提供 `getValue()` 和 `setValue()` 是为了方便编写泛型代码。
```

**示例：** 演示 `IntegerProperty` 及其 `get()` 和 `set()` 方法的使用。

`counter` 为 read-write 属性，因为它是 `SimpleIntegerProperty` 类型。

```java
IntegerProperty counter = new SimpleIntegerProperty(1);
int counterValue = counter.get();
System.out.println("Counter:" + counterValue);

counter.set(2);
counterValue = counter.get();
System.out.println("Counter:" + counterValue);
```

```
Counter:1
Counter:2
```

## 3. read-only 属性

read-only 属性的设计需要了解以下。`ReadOnlyXXXWrapper` 类包装了两个 `XXX` 类型的属性：

- 一个 read-only
- 一个 read-write

两个属性的值同步。其 `ReadOnlyXXXWrapper.getReadOnlyProperty()` 返回 `ReadOnlyXXXProperty` 对象。

**示例：** 创建 read-only `Integer` 属性

- `idWrapper` 其实是 read-write 类型
- `id` 是 read-only 属性
- 当 `idWrapper` 的值改变，`id` 的值随之改变

```java
ReadOnlyIntegerWrapper idWrapper = new ReadOnlyIntegerWrapper(100);
ReadOnlyIntegerProperty id = idWrapper.getReadOnlyProperty();

System.out.println("idWrapper:" + idWrapper.get());
System.out.println("id:" + id.get());

// Change the value
idWrapper.set(101);
System.out.println("idWrapper:" + idWrapper.get());
System.out.println("id:" + id.get());
```

```
idWrapper:100
id:100
idWrapper:101
id:101
```

```ad-tip
wrapper 属性一般作为类的 `private` 变量使用，这样可以在类的内部修改属性值。然后提供了一个 `public` 方法返回 wrapper 的 read-only 属性对象，这样该属性对外为 read-only。
```

## 4. 属性类

单值属性有 7 种类型。这些属性：

- 基类名为 `XXXProperty`
- read-only 类名为 `ReadOnlyXXXProperty`
- wrapper 类名为 `ReadOnlyXXXWrapper`

各个类型的 `XXX` 值如下表：

| 类型    | XXX 值  |
| ------- | ------- |
| int     | Integer |
| long    | Long    |
| float   | Float   |
| double  | Double  |
| boolean | Boolean |
| String  | String  |
| Object  | Object  |

`Property` 对象包含三种信息：

- 对包含该属性的 bean 引用
- name
- value

`Property` 具体类提供了 4 个构造函数，以 `SimpleIntegerProperty` 为例：

```java
SimpleIntegerProperty()
SimpleIntegerProperty(int initialValue)
SimpleIntegerProperty(Object bean, String name)
SimpleIntegerProperty(Object bean, String name, int initialValue)
```

`initialValue` 的默认值取决于属性类型，数值类型为 0，`boolean` 类型为 `false`，引用类型为 `null`。

属性可以是 bean 的一部分，也可以是独立对象：

- 作为 bean 的一部分时，构造函数中的 `bean` 参数是对 bean 对象的引用
- 作为独立对象时，`bean` 为 `null`

`bean` 默认为 `null`。

`name` 是属性名称，默认为空字符串。

**示例：** 创建一个属性，作为 bean 的一部分

```java
public class Person {

    private StringProperty name = new SimpleStringProperty(this, "name", "Li");
    // More code goes here...
}
```
