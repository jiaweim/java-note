- [Static, Default, Private](#static-default-private)
  - [Static Method](#static-method)
  - [Default Methods](#default-methods)
  - [默认方法冲突](#%e9%bb%98%e8%ae%a4%e6%96%b9%e6%b3%95%e5%86%b2%e7%aa%81)

# Static, Default, Private
早期的 Java 中所有的接口方法都是 `abstract`，即没有实现。现在接口中可以定义三种实现方法：static, default 和 private 方法。

## Static Method
以前接口里没有静态方法，并不是技术问题，而是在接口里所有抽象方法中添加具体实现方法感官不大对。不过现在这种想法已经变了，特别是接口中的工厂方法。例如，`IntSequence` 接口中的 `digitsOf` 静态方法根据参数生成数值序列：
```java
IntSequence digits = IntSequence.digitsOf(1729);
```

该方法生成 `IntSequence` 特定实现类的实例，但是调用者不需要知道具体是哪个实现类：
```java
public interface IntSequence {
    ...
    static IntSequence digitsOf(int n) {
        return new DigitSequence(n);
    }
}
```
在 Java 8 之前，经常将静态方法放在伴侣类中，如 `COllection/Collections`, `Path/Paths` 等，现在不需要这种分离了。

## Default Methods
可以在接口中提供接口方法的 `default` 实现，如下：
```java
public interface IntSequence {
    default boolean hasNext() { return true; }
    // By default, sequences are infinite
    int next();
}
```
实现接口的类可以选择覆盖 `hasNext`  方法，或者直接继承默认实现。

> 默认方法终结了经典的接口+伴侣类的模式，例如 Java API 中的 `Collection / AbstractCollection`, `WindowListener / WindowAdapter`，现在只需要在接口中实现方法。

默认方法一个重要应用是接口演变。例如 `Collection` 接口已经在 Java 中存在好多年，你准备自定义实现该接口：
```java
public class Bag implements Collection
```

在 Java 8中，该接口中添加了 `stream` 方法。如果 `stream` 不是默认方法，则 `Bag` 类如果不实现该新方法，会出现编译错误，现在就没这问题了。


## 默认方法冲突
如果一个类实现多个接口，如果这些接口中存在同名方法，就出现方法冲突。解决冲突很容易。例如，加入 `Person` 接口有 `getId` 方法：
```java
public interface Person {
    String getName();
    default int getId() { return 0; }
}
```

另有 `Identified` 接口也有该方法：
```java
public interface Identified {
    default int getId() { return Math.abs(hashCode()); }
}
```

这两个 `getId` 有相同的方法签名，同时实现这两个接口出现方法冲突：
```java
public class Employee implements Person, Identified {
    ...
}
```
此时你需要覆盖该方法，可以选择性的使用其中一个接口的方法，也可以自定义实现：
```java
public class Employee implements Person, Identified {
    public int getId() { return Identified.super.getId(); }
    ...
}
```
> `super` 关键字用于选择父类中的方法，此处还需要指定是哪个接口中的方法。

现在假如 `Identified` 接口没有提供默认实现：
```java
interface Identified {
    int getId();
}
```
此时依然需要覆盖该方法。
