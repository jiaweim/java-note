# 集合

## RangeSet

`RangeSet` 描述一组不相连的非空范围（range）。当添加新的 range 到 `RangeSet`，相交的 ranges 或合并，空的 range 被忽略。例如：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(1, 10)); // {[1, 10]}
rangeSet.add(Range.closedOpen(11, 15)); // disconnected range: {[1, 10], [11, 15)}
rangeSet.add(Range.closedOpen(15, 20)); // connected range; {[1, 10], [11, 20)}
rangeSet.add(Range.openClosed(0, 0)); // empty range; {[1, 10], [11, 20)}
rangeSet.remove(Range.open(5, 10)); // splits [1, 10]; {[1, 5], [10, 10], [11, 20)}
```

如果要合并类似 `Range.closed(1, 10)` 和 `Range.closedOpen(11, 15)` 的 ranges，需要首先使用 `Range.canonical(DiscreteDomain)` 对 ranges 进行预处理，例如使用 `DiscreteDomain.integers()`。

### 创建 RangeSet

- 创建 mutable 空 `RangeSet`

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();
```

- 从 `Range` 集合创建

```java
List<Range<Integer>> numberList = Arrays.asList(Range.closed(0, 2));
TreeRangeSet<Integer> numberRangeSet = TreeRangeSet.create(numberList);
```

- 对 immutable `RangeSet`, 使用 `ImmutableRangeSet` 中的 builder

```java
ImmutableRangeSet<Integer> rangeSet = new ImmutableRangeSet.Builder<Integer>().add(Range.closed(0, 2)).build();
```

在创建 `ImmutableRangeSet` 时，不允许添加重叠的 ranges，否则抛出 `IllegalArgumentException`。

- 移出 ranges

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();

rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8));
rangeSet.add(Range.closed(9, 15)); // [[0..2], [3..5], [6..8], [9..15]]

rangeSet.remove(Range.closed(3, 5));
rangeSet.remove(Range.closed(7, 10)); // [[0..2], [6..7), (10..15]]

assertTrue(rangeSet.contains(1));
assertFalse(rangeSet.contains(9));
assertTrue(rangeSet.contains(12));
```

### Views

`RangeSet` 支持许都视图，包括：

- `complement()`：`RangeSet` 补集视图。`complement` 也是一个 `RangeSet`，同样包含不连续的非空 ranges。
- `subRangeSet(Range<C>)`：返回 `RangeSet` 与指定 `Range` 的交集视图。是传统有序集合的 `headSet`, `subSet` 和 `tailSet` 的泛化版本。
- `intersects(Range<C> otherRange)`：如果 `RangeSet` 与指定 `Range` 存在交集，返回 true。等价于调用 `subRangeSet(Range<C>)` 然后测试返回的 range是否为空。
- `asRanges()`：将 `RangeSet` 转换为可迭代的 `Set<Range<C>>`。
- `asSet(DiscreteDomain<C>)` (`ImmutableRangeSet` only)：将 `RangeSet<C>` 作为 `ImmutableSortedSet<C>`，查看范围内的元素而非 ranges。(如果 `DiscreteDomain` 和 `RangeSet` 的上边界或下边界均为 unbounded,则不支持该操作).

`complement()` 示例：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();

rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

RangeSet<Integer> complementRangeSet = rangeSet.complement(); // [(-∞..0), (2..3), (5..6), (8..+∞)]

assertTrue(complementRangeSet.contains(-1000));
assertFalse(complementRangeSet.contains(2));
assertFalse(complementRangeSet.contains(3));
assertTrue(complementRangeSet.contains(1000));
```

`subRangeSet` 示例：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

RangeSet<Integer> subRangeSet
        = rangeSet.subRangeSet(Range.closed(4, 14)); // [[4..5], [6..8]]

assertFalse(subRangeSet.contains(3));
assertFalse(subRangeSet.contains(14));
assertTrue(subRangeSet.contains(7));
```

`intersects` 示例：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();

rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 10));
rangeSet.add(Range.closed(15, 18)); // [[0..2], [3..10], [15..18]]

assertTrue(rangeSet.intersects(Range.closed(4, 17)));
assertFalse(rangeSet.intersects(Range.closed(19, 200)));
```

### Queries

`RangeSet` 支持几个查询操作，其中最突出的几个：

- `contains(C)`：`RangeSet` 最基本的操作，查询 `RangeSet` 是否有任意一个 range 包含指定元素。
- `rangeContaining(C)`：返回包含指定元素的 `Range`，如果没有则返回 `null`。
- `encloses(Range<C>)`：测试 `RangeSet` 中是否有 `Range` 包含指定 range。
- `span()`：返回包含 `RangeSet` 中每个范围的最小 `Range`。

`contains(C)` 示例：

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

assertTrue(rangeSet.contains(1));
assertFalse(rangeSet.contains(9));
```

`span()` 示例：

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

Range<Integer> experienceSpan = rangeSet.span();

assertEquals(0, experienceSpan.lowerEndpoint().intValue());
assertEquals(8, experienceSpan.upperEndpoint().intValue());
```



## RangeMap

`RangeMap` 定义不相交 range 到值得映射。和 `RangeSet` 不同, `RangeMap` 不会合并相邻的 range，即使是映射到相同的值。例如：

```java
RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
rangeMap.put(Range.closed(1, 10), "foo"); // {[1, 10] => "foo"}
rangeMap.put(Range.open(3, 6), "bar"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo"}
rangeMap.put(Range.open(10, 20), "foo"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo", (10, 20) => "foo"}
rangeMap.remove(Range.closed(5, 11)); // {[1, 3] => "foo", (3, 5) => "bar", (11, 20) => "foo"}
```

`RangeMap` 提供了两个视图：
- `asMapOfRanges()`，即 `Map<Range<K>, V>` 视图，可用于迭代 `RangeMap`。
- `subRangeMap(Range<K>)` 以 `RangeMap` 的形式返回 `RangeMap` 与  `Range` 的交集。提供了传统 `headMap`, `subMap` 和 `tailMap` 的通用版本。

## 参考

- https://www.baeldung.com/guava-rangeset
- https://github.com/google/guava/wiki/NewCollectionTypesExplained#rangeset
