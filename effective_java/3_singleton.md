# Singleton

2024-04-15⭐
@author Jiawei Mao
****

## 简介

单例（singleton）指一个类只实例化一次，通常用于表示无状态对象或独有的系统组件。

单例类的测试比较麻烦，除非它实现了某个接口，否则无法 mock 其对象。

## 单例实现

实现单例模式的常见方法有两种，这两种方法将构造函数保持为 `private`，然后提供一个 `public static` 成员访问实例：

- 使用字段
- 使用工厂方法

### 使用字段

```java
public class Elvis {
	public static final Elvis INSTANCE = new Elvis();
	private Elvis() { ... }
	
	public void leaveTheBuilding() { ... }
}
```

`private` 构造函数只被调用一次，用于初始化 `public static final` 字段 `Elvis.INSTANCE`。因为没有 `public` 或 `protected` 构造函数，因此在 `Elvis` 类初始化后，只存在一个 `Elvis` 实例。

!!! warning
    通过 `AccessibleObject.setAccessible` 反射技术可以调用 `private` 构造函数。如果要避免该情况，可以修改构造函数，在被要求创建第二个实例时抛出异常。

### 使用工厂方法

第二种实现单例模式的方法：

```java
public class Elvis {
	private static final Elvis INSTANCE = new Elvis();
	private Elvis() { ... }
	
	public static Elvis getInstance() { return INSTANCE; }
	
	public void leaveTheBuilding() { ... }
}
```

每次调用 `Elvis.getInstance()` 返回相同的对象引用，不会创建其它 `Elvis` 实例（反射除外）。

`static` **字段**的主要优点：

- 从 API 可以很容易看出是单例模式
- 简单

`static` **工厂**方法的主要优点：

- 灵活，可以在不改变 API 的情况下将类改为非单例实现，factory 方法可以返回独有实例，也可以为每个调用它的线程返回一个独有实例。
- 可以编写 generic singleton factory。
- 可以作为方法引用在 supplier 使用，例如，`Elvis::instance` 是一个 `Supplier<Elvis>`。

除非确实需要 `static` 方法的这些优点，否则**推荐**使用 `static` 字段。

## 序列化

要序列化单例类，单纯声明 `implements Serializable` 是不够的。为了保证为单例，需要将所有字段声明为 `transient`，并提供一个 `readResolve` 方法（T89）。否则，每次反序列化一个已序列化的实例，都会创建一个新的实例。为了避免该情况，在 Elvis 中添加 `readResolve` 方法：

```java
private Object readResolve() {
// Return the one true Elvis and let the garbage collector
// take care of the Elvis impersonator.
	return INSTANCE;
}
```

### enum 实现（推荐）

实现单例的第三种方式是声明一个单元素 enum：

```java
public enum Elvis {
    INSTANCE;

    public void leaveTheBuilding() { ... }

}
```

该方法类似于 public field 方法，但更简洁，面对复杂的序列化和反射攻击，也能够保证单例。

这种方法看着不自然，但是单元素 enum 通常是实现单例模式的最佳方式。不过如果你的单例必须继承 enum 外的超类，就不能使用该方法。

