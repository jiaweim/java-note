

# 11. 谨慎覆盖 `clone` 方法
`Cloneable` 接口用于表示该对象允许克隆。可惜的是它并没有达到该目的。其主要缺点是缺少一个 `clone` 方法，`Object` 的 `clone` 方法是 `protected`。即使实现了 `Cloneable`，其它类不通过映射也无法调用 `clone` 方法，映射也可能失败，因为不能保证其 `clone` 方法是可访问的。尽管有许多缺点，其应用依然十分广泛，所以值得我们好好研究一下。下面说明如何很好的实现 `clone` 方法。

既然 `Cloneable` 不包含方法，它到底干了什么？它决定 `Object` 的 `protected` 的 `clone` 方法实现：如果一个类实现了 `Cloneable`，则 `Object` 的 `clone` 方法返回一个逐字段复制的新对象，否则抛出 `CloneNotSupportedException`。这是接口的非典型用法，不值得效仿。正常来说，实现了一个接口表示该类具有什么功能，而 `Cloneable` 接口则修改了父类的一个 `protected` 方法的行为。

要使实现 `Cloneable` 有效果，则其所有的父类都必须遵守一个十分复杂、不好操作并且没有具体文档说明的协议。

`clone` 方法本身的协议是很弱的，下面是 `java.lang.Object` 中的文档：

创建并返回该对象的 一个拷贝，这个拷贝的具体定义取决于该对象的类。一般来说，对于任意对象 x，满足：
```java
x.clone() != x
```

并且：
```java
x.clone().getClass() == x.getClass()
```

但这些要求不是必须的，虽然通常也满足：
```java
x.clone().equals(x)
```
但这也不是必须的。

按照惯例，应该通过 `super.clone` 获得拷贝。如果一个类及其所有的父类（除了 `Object`）都满足该惯例，则满足 `x.clone().getClass() == x.getClass()`。

并且，通过该方法拷贝的对象应该独立于原来的对象。为了实现该目的，对 mutable 对象，需要深度拷贝，如果一个对象只包含 immutable 字段，则可以不作修改。

`Object` 拷贝规则：
- 基本类型，如 int, float, long等，拷贝值
- String，拷贝地址，即引用但在修改时会重新生成新的字符串，所以可以看作 immutable类型
- 对象，拷贝地址



# 18. Prefer interfaces to abstract classes
Java 提供了两种允许多种实现的类型：接口（interface）和抽象类（abstract class）。两者一个显著的差别是，抽象类允许包含部分实现的方法，而接口不允许，另一个重要的差别是实现抽象类必须