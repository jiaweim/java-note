# 集合

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
