# 11. 重写 equals 时要重写 hashCode

在类中覆盖 `equals`，必须同时覆盖 `hashCode`。否则该类会违反 `hashCode` 约定，导致在 `HashMap` 和 `HashSet` 等集合中工作异常。以下是 `Object` 规范中的协议：

- 在应用执行过程中反复调用对象的 `hashCode` 方法，只要 `equals` 中使用的信息没有变化，就必须始终返回相同的值。在这次执行和下次执行该值无需相同。
- 如果 `equals(Object)` 判断两个对象相等，那么这两个对象调用 `hashCode` 返回的整数必然相同。
- 如果两个对象根据 `equals(Object)` 判断不相等，此时这两个对象调用 `hashCode` 返回值不一定不同。不过，为不等的对象生成不同的 hash 值可以提高散列表的性能。

没有覆盖 `hashCode` 会违反第 2 个协议：**相等对象的哈希值必须相等**。

根据类的 `equals` 方法，两个不同的实例完全可能相等，但是对 `Object` 的 `hashCode` 方法，这两个对象没有太多共同点。`Object` 的 `hashCode` 方法会返回两个看上去随机的数，而不是两个相等的数。

例如，假设使用 [Item 10](10_equals.md) 中的 `PhoneNumber` 作为 `HashMap` 的 key：

```java
Map<PhoneNumber, String> m = new HashMap<>();
m.put(new PhoneNumber(707, 867, 5309), "Jenny");
```

