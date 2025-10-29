# Ranges

2025-10-29⭐
@author Jiawei Mao
***
## 示例

```java
List<Double> scores;
Iterable<Double> belowMedianScores = Iterables.filter(scores, Range.lessThan(median));
...
Range<Integer> validGrades = Range.closed(1, 12);
for(int grade : ContiguousSet.create(validGrades, DiscreteDomain.integers())) {
  ...
}
```

## 简介

range (范围)，有时也称为区间（interval），是特定 domain 的一个 convex (连续、不间断)。凸性（convexity）意味着对任意 `a <= b <= c`，`range.contains(a) && range.contains(c)` 意味着 `range.contains(b)`。

range 可以扩展到无穷大，例如，range `x > 3` 包含任意大于 3 的值；range 也可以在有限范围，如 `2 <= x < 5`。下面使用有数学背景的程序员熟悉的更紧凑的表示方法：

- `(a..b) = {x | a < x < b}`
- `[a..b] = {x | a <= x <= b}`
- `[a..b) = {x | a <= x < b}`
- `(a..b] = {x | a < x <= b}`
- `(a..+∞) = {x | x > a}`
- `[a..+∞) = {x | x >= a}`
- `(-∞..b) = {x | x < b}`
- `(-∞..b] = {x | x <= b}`
- `(-∞..+∞) = all values`

上面使用的 a 和 b 成为端点（endpoint）。为了保持一致性，Guava 的 `Range` 要求 upper-endpoint 不小于 lower-endpoint。仅当至少一个边界为闭区间时，才允许两个端点相同：

- `[a..a]`: singleton range
- `[a..a); (a..a]`: empty, but valid
- `(a..a)`: invalid

所有 range 都是 *immutable*。

## 构建 Ranges

可以从 `Range` 的 static 方法构建 `Range`：

| Range type | Method                                                       |
| ---------- | ------------------------------------------------------------ |
| `(a..b)`   | [`open(C, C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#open-java.lang.Comparable-java.lang.Comparable-) |
| `[a..b]`   | [`closed(C, C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#closed-java.lang.Comparable-java.lang.Comparable-) |
| `[a..b)`   | [`closedOpen(C, C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#closedOpen-java.lang.Comparable-java.lang.Comparable-) |
| `(a..b]`   | [`openClosed(C, C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#openClosed-java.lang.Comparable-java.lang.Comparable-) |
| `(a..+∞)`  | [`greaterThan(C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#greaterThan-C-) |
| `[a..+∞)`  | [`atLeast(C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#atLeast-C-) |
| `(-∞..b)`  | [`lessThan(C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#lessThan-C-) |
| `(-∞..b]`  | [`atMost(C)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#atMost-C-) |
| `(-∞..+∞)` | [`all()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#all--) |

```java
Range.closed("left", "right"); // 按字典顺序排在 "left" 和 "right" 之间的所有 strings
Range.lessThan(4.0); // 严格小于 4 个 double 值
```

也可以通过显式传递边界类型来构造 `Range`：

| Range type                                   | Method                                                       |
| -------------------------------------------- | ------------------------------------------------------------ |
| Bounded on both ends                         | [`range(C, BoundType, C, BoundType)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#range-java.lang.Comparable-com.google.common.collect.BoundType-java.lang.Comparable-com.google.common.collect.BoundType-) |
| Unbounded on top (`(a..+∞)` or `[a..+∞)`)    | [`downTo(C, BoundType)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#downTo-java.lang.Comparable-com.google.common.collect.BoundType-) |
| Unbounded on bottom (`(-∞..b)` or `(-∞..b]`) | [`upTo(C, BoundType)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#upTo-java.lang.Comparable-com.google.common.collect.BoundType-) |

其中，`BoundType` 是一个 enum 类型，包含 `CLOSED` 和 `OPEN` 两个值：

```java
Range.downTo(4, boundType); // 可以决定是否要包含 4
Range.range(1, CLOSED, 4, OPEN); // Range.closedOpen(1, 4) 的另一种写法
```

## 操作

`Range` 的最基本操作是 `contains(C)`，是该 `Range` 是否包含指定值。此外，`Range` 可以在函数范式中作为 `Predicate` 使用。`Range` 还支持 `containsAll(Iterable<? extends C>)`：

```java
Range.closed(1, 3).contains(2); // returns true
Range.closed(1, 3).contains(4); // returns false
Range.lessThan(5).contains(5); // returns false
Range.closed(1, 4).containsAll(Ints.asList(1, 2, 3)); // returns true
```

### 查询

`Range` 提供如下方法查看 range 的端点：

- [`hasLowerBound()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#hasLowerBound--) 和 [`hasUpperBound()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#hasUpperBound--)，检查 range 是否包含指定端点，还是无穷
- [`lowerBoundType()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#lowerBoundType--) 和 [`upperBoundType()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#upperBoundType--) ，返回相应端点的 `BoundType`，可以是 `CLOSED` 或 `OPEN`。如果该 range 不包含对应的 endpoint，则抛出 `IllegalStateException`
- [`lowerEndpoint()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#lowerEndpoint--) 和 [`upperEndpoint()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#upperEndpoint--)，返回指定端点值，如果该 range 没有指定该端点值，抛出 `IllegalStateException`
- [`isEmpty()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#isEmpty--)  测试该 range 是否为空，即它的形式为 `[a,a)` 或 `(a,a]`

```java
Range.closedOpen(4, 4).isEmpty(); // returns true
Range.openClosed(4, 4).isEmpty(); // returns true
Range.closed(4, 4).isEmpty(); // returns false
Range.open(4, 4).isEmpty(); // Range.open throws IllegalArgumentException

Range.closed(3, 10).lowerEndpoint(); // returns 3
Range.open(3, 10).lowerEndpoint(); // returns 3
Range.closed(3, 10).lowerBoundType(); // returns CLOSED
Range.open(3, 10).upperBoundType(); // returns OPEN
```

### 区间运算

**encloses**

ranges 之间的最基本关系是 `encloses(Range)`，如果内部 range 的边界没有延生外外部 range 的边界之外，就返回 true。这完全通过端点之间的比较实现：

- `[3..6]` 包含 `[4..5]`
- `(3..6)` 包含 `(3..6)`
- `[3..6]` 包含 `[4..4)` (虽然后者为空)
- `(3..6]` 不包含 `[3..6]`
- `[4..5]` 不包含 `(3..6)` **虽然前一个 range 包含后一个 range 的所有值**, 使用离散 domain 可以解决该问题
- `[3..6]` 不包含 `(1..1]` **虽然前一个 range 包含后一个 range 的所有值**

**isConnected**

`Range.isConnected(Range)` 测试两个 ranges 是否连接。具体而言，`isConnected` 判断两个 ranges 是否共同包含某个范围，或者说两个 ranges 的并集是否形成的连通集合（没有断点）。

`isConnected` 是一个自反、对称关系：

```java
Range.closed(3, 5).isConnected(Range.open(5, 10)); // returns true
Range.closed(0, 9).isConnected(Range.closed(3, 4)); // returns true
Range.closed(0, 5).isConnected(Range.closed(3, 9)); // returns true
Range.open(3, 5).isConnected(Range.open(5, 10)); // returns false
Range.closed(1, 5).isConnected(Range.closed(6, 10)); // returns false
```

**intersection**

[`Range.intersection(Range)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#intersection-com.google.common.collect.Range-)  交集操作，当两个 ranges 为 `isConnected` 才存在，如果不存在交集，抛出 `IllegalArgumentException`。

`intersection` 是一个可交换的结合运算。

```java
Range.closed(3, 5).intersection(Range.open(5, 10)); // returns (5, 5]
Range.closed(0, 9).intersection(Range.closed(3, 4)); // returns [3, 4]
Range.closed(0, 5).intersection(Range.closed(3, 9)); // returns [3, 5]
Range.open(3, 5).intersection(Range.open(5, 10)); // throws IAE
Range.closed(1, 5).intersection(Range.closed(6, 10)); // throws IAE
```

**span**

[`Range.span(Range)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#span-com.google.common.collect.Range-) 返回包含两个 ranges 的最小范围，如果两个 range 是 `isConnected`，则返回两者并集。

`span` 是一个可交换、结合的运算。

```java
Range.closed(3, 5).span(Range.open(5, 10)); // returns [3, 10)
Range.closed(0, 9).span(Range.closed(3, 4)); // returns [0, 9]
Range.closed(0, 5).span(Range.closed(3, 9)); // returns [0, 9]
Range.open(3, 5).span(Range.open(5, 10)); // returns (3, 10)
Range.closed(1, 5).span(Range.closed(6, 10)); // returns [1, 10]
```

## 离散域（discrete domain）

有些 `Comparable` 类型是离散的，这表示对具有两个 end-points 的 range，可以枚举所有值。

在 Guava 中，[`DiscreteDomain`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/DiscreteDomain.html) 实现类型 `C` 的离散操作。离散域表示**该类型所有值**的集合，不能表示子集。例如，不能表示 "整数中的质数"、"长度为 5 个字符串"等。

[`DiscreteDomain`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/DiscreteDomain.html) 类提供了 3 种实现：

| Type         | DiscreteDomain                                               |
| ------------ | ------------------------------------------------------------ |
| `Integer`    | [`integers()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/DiscreteDomain.html#integers--) |
| `Long`       | [`longs()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/DiscreteDomain.html#longs--) |
| `BigInteger` | `bigIntegers()`                                              |

有了 `DiscreteDomain` 实例后，可以使用如下 `Range` 操作：

- [`ContiguousSet.create(range, domain)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ContiguousSet.html#create-com.google.common.collect.Range-com.google.common.collect.DiscreteDomain-): 将 `Range<C>` 看作一个 `ImmutableSortedSet<C>`, 并添加一些额外操作。不适合无界 ranges，除非类型本身是有界的
- [`canonical(domain)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Range.html#canonical-com.google.common.collect.DiscreteDomain-): 将 ranges 转换为规范形式。如果 `ContiguousSet.create(a, domain).equals(ContiguousSet.create(b, domain))` 并且 `!a.isEmpty()`, 那么 `a.canonical(domain).equals(b.canonical(domain))`. (这不表示 `a.equals(b)`.)

```java
ImmutableSortedSet<Integer> set = ContiguousSet.create(Range.open(1, 5), DiscreteDomain.integers());
// 集合包含 [2, 3, 4]

ContiguousSet.create(Range.greaterThan(0), DiscreteDomain.integers());
// 集合包含 [1, 2, ..., Integer.MAX_VALUE]
```

`ContiguousSet.create` 没有构造整个 range，而是返回 range 的集合视图。

### 自定义 DiscreteDomain

可以自定义 `DiscreteDomain` 对象，在实现时需要注意：

- 离散域总是包含其类型所有值的集合，不能表示部分子集。因此，无法构造只精确到分钟的 `DiscreteDomain<LocalDateTime>`，因为这没有包含 `LocalDateTime` 所有可能值（`LocalDateTime` 精确到纳秒）
- `DiscreteDomain` 可以是无限的，如 `DiscreteDomain<BigInteger>`。此时应该使用 `minValue()` 和 `maxValue()` 的默认实现，即抛出 `NoSuchElementException` 。对这类 `DiscreteDomain` 不能使用 `ContiguousSet.create` 操作

## Comparator

为了在功能和 API 的简洁性之间取得平衡，`Range` 没有提供基于 `Comparator` 的接口，因此不需要担心基于不同 comparator 如何交互。

如果需要自定义 `Comparator`，则可以采用以下两种方式之一：

- 使用通用的 `Predicate` 而不是 `Range`（因为 `Range` 实现了 `Predicate` 接口，因此可以使用 `Predicates.compose(range, function)` 来获得一个新的 `Predicate`）
- 使用一个 wrapper 类包含你的类型，在其中定义你所需的顺序

## 参考

- https://github.com/google/guava/wiki/RangesExplained