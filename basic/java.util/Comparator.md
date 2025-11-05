# Comparator

## 简介

`java.util.Comparator` 比较函数用于集合排序。可以将其传递给排序方法（如 `Collections.sort` 或 `Arrays.sort`）控制顺序。`Comparator` 还可用于控制数据结构中元素的顺序，如 `SortedMap` 和 `SortedMap`。



## 创建

```java
static <T extends Comparable<? super T>>
Comparator<T> naturalOrder()
```

返回一个按自然顺序比较 `Comparable` 对象的 `Comparator`。





## 参考

- https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Comparator.html