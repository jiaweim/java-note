# 随机数生成器

## 简介



## 随机排列、组合和抽样

要在集合中选择对象的随机样本，可以使用 `RandomDataGenerator` 的 `nextSample` 方法。具体来说：

- 如果 `c` 是包含至少 `k` 个对象的几何，`randomData` 是一个 `RandomDataGenerator` 实例，那么 `randomData.nextSample(c, k)` 返回一个长度为 `k` 的 `object[]` 数组，包含从集合中随机选择的元素。
- 如果 `c` 包含重复引用，返回的数组中也可能包含重复引用；否则返回的元素为 unique，即采用无放回抽样。

如果 `randomData` 是 `RandomDataGenerator` 实例，而 `n` 和 `k` 是整数，且 $k\le n$，那么：

- `randomData.nextPermutation(n, k)` 返回长度为 `k` 的 `int[]` 数组，其元素从整数 0 到 n-1 随机选择，没有重复。简而言之，`randomData.nextPermutation(n, k)` 返回一个从 n 取 k 个元素的随机排列。