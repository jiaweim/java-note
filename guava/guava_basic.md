# Guava 基本工具

## Ordering
2025-11-04⭐
***
Guava 的 `Ordering` 提供 `Comparator` 的 fluent 实现,可用于构建复杂的 comparator 并应用于集合对象.

`Ordering` 本质上是`Comparator` 实例,只是提供了额外便捷功能.

### 创建 Ordering

通过静态方法提供创建的 Ordering:

| Method                                                       | Description                            |
| ------------------------------------------------------------ | -------------------------------------- |
| [`natural()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#natural--) | 使用 `Comparable` 类型的自然排序       |
| [`usingToString()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#usingToString--) | 按 `toString()` 返回的字符串的字典排序 |

通过 `Ordering.from(Comparator)` 使用已有 `Comparator` 创建 `Ordering`.

但是,创建 `Ordering` 的最常见方法是完全跳过 `Comparator`,直接扩展 `Ordering` abstract 类:

```java
Ordering<String> byLengthOrdering = new Ordering<String>() {
  public int compare(String left, String right) {
    return Integer.compare(left.length(), right.length());
  }
};
```

### Chaining

可以对 `Ordering` 包装获得派生 orderings.常见变体如下:

| Method                                                       | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`reverse()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#reverse--) | 返回反向排序                                                 |
| [`nullsFirst()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#nullsFirst--) | 将 null 元素排在非 null 元素前面。类似的还是有 [`nullsLast()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#nullsLast--) |
| [`compound(Comparator)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#compound-java.util.Comparator-) | 使用指定 `Comparator` 来打破平局的情况（"break ties"）       |
| [`lexicographical()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#lexicographical--) | 按字典顺序排序                                               |
| [`onResultOf(Function)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#onResultOf-com.google.common.base.Function-) | 对元素应用指定函数后再排序                                   |

例如，假设有个类需要 comarator：

```java
class Foo {
  @Nullable String sortedBy;
  int notSortedBy;
}
```

需要处理 `sortedBy` 的 null 值。解决方案：

```java
Ordering<Foo> ordering = Ordering.natural()
    .nullsFirst().onResultOf(new Function<Foo, String>() {
  public String apply(Foo foo) {
    return foo.sortedBy;
  }
});
```

要理解 `Ordering` 的调用链，需要从右到左读。上面示例首先查找 `sortedBy` 字段，将 `sortedBy` 值为 null 的元素移到顶部，余下按自然顺序排序。

> [!IMPORTANT]
>
> `compound` 不遵循 backwards 顺序，需要从左向右读。

调用链太长不容易理解。建议将链接长度限制在 3 个以内。还可以将中间函数分离来简化代码：

```java
Ordering<Foo> ordering = Ordering.natural().nullsFirst().onResultOf(sortKeyFunction);
```

### 应用

Guava 提供了许多基于 `Ordering` 的方法。例如：

| Method                                                       | Description                                                  | See also                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`greatestOf(Iterable iterable, int k)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#greatestOf-java.lang.Iterable-int-) | 返回 `k`  个最大元素。不一定稳定                             | [`leastOf`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#leastOf-java.lang.Iterable-int-) |
| [`isOrdered(Iterable)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#isOrdered-java.lang.Iterable-) | 测试指定 `Iterable` 是否有序                                 | [`isStrictlyOrdered`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#isStrictlyOrdered-java.lang.Iterable-) |
| [`sortedCopy(Iterable)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#sortedCopy-java.lang.Iterable-) | 返回排序 `List` copy                                         | [`immutableSortedCopy`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#immutableSortedCopy-java.lang.Iterable-) |
| [`min(E, E)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#min-E-E-) | 返回两个参数中的最小值，如果两个值相等，返回第一个参数       | [`max(E, E)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#max-E-E-) |
| [`min(E, E, E, E...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#min-E-E-E-E...-) | 返回最小值，如果有多个最小值，返回第一个                     | [`max(E, E, E, E...)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#max-E-E-E-E...-) |
| [`min(Iterable)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#min-java.lang.Iterable-) | 返回指定 `Iterable` 的最小元素。如果为空，抛出`NoSuchElementException` | [`max(Iterable)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#max-java.lang.Iterable-), [`min(Iterator)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#min-java.util.Iterator-), [`max(Iterator)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Ordering.html#max-java.util.Iterator-) |

### Java 8

在 Java 8 中，`Ordering` 的大部分功能可以由 `Stream` 和 `Comparator` 提供，其余功能可以由 `com.google.common.collect.Comparators` 提供。

## Object 方法

### compare/compareTo
2025-11-04⭐
***

直接实现 `Comparator` 或 `Comparable` 接口可能很痛苦。例如：

```java
class Person implements Comparable<Person> {
  private String lastName;
  private String firstName;
  private int zipCode;

  public int compareTo(Person other) {
    int cmp = lastName.compareTo(other.lastName);
    if (cmp != 0) {
      return cmp;
    }
    cmp = firstName.compareTo(other.firstName);
    if (cmp != 0) {
      return cmp;
    }
    return Integer.compare(zipCode, other.zipCode);
  }
}
```

这段代码很容易出错。为此，Guava 提供了 `ComparisonChain`。

`ComparisonChain` 执行 lazy 比较：执行比较知道找到非 0 结果，然后忽略余下输入。

```java
public int compareTo(Foo that) {
 return ComparisonChain.start()
     .compare(this.aString, that.aString)
     .compare(this.anInt, that.anInt)
     .compare(this.anEnum, that.anEnum, Ordering.natural().nullsLast())
     .result();
}
```

这种 fluent 范式更容易阅读，不易出错，而且性能更好。另外，Guava 还提供了额外的比较工具 `Ordering`。

