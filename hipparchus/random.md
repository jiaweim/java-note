# 随机数生成器

## 简介

Hipparchus random 包含如下工具：

- 生成随机数
- 生成随机向量
- 生成随机字符串
- 生成随机样本和排列
- 分析输入文件中值的分布，并生成与文件中值类似的值
- 为直方图生成数据

数据生成使用的随机数据源是可插入的。在大多数情况下，默认使用 Well 生成器。random 包中还提供适合蒙特卡洛分析（但不适合密码学）的优秀 PRNG。

下面展示如何使用 Hipparchus 生成不同类型的随机数。所有示例均使用 JDK 提供的默认 PRGN。使用其它 PRNG，只需要在构造函数中设置对应的 `RandomGenerator`。

## 随机数

`RandomDataGenerator` 类实现生成随机数字序列的方法。

### 概率分布的随机数字序列

JDK 内置的 `Math.random()` 生成的数值序列遵循均匀分布，即生成的值在 0 到 1 之间均匀分布。Hipparchus 支持从 distributions 包中的每个分布生成随机序列。`RandomDataGenerator` 的 `nextXxx` 方法获得满足指定分布的随机数，无需实例化分布类。例如，要活的均值为 3、标准差为 1.5 的正态分布的随机数，可以用：

```java
RandomDataGenerator randomDataGenerator = new RandomDataGenerator(1000)
randomDataGenerator.nextNormal(3,1.5)
```

这里使用默认的 Well 生成器作为随机源，并将 1000 作为初始 seed。要生成用于模拟的随机数序列，只需要创建一个 `RandomDataGenerator`，然后反复调用它。

对用于定义的分布，或者 `RandomDataGenerator` 的 `nextXxx` 方法未包含的分布，可以使用 `nextDeviate` 方法，该方法以实数或整数分布为参数，为任意分布实现基于反演的通用采样方法。另外还有 `nextDeviates` 方法，它们以分布和整数作为参数，用于生成随机数组很方便。任何分布，包括 `nextXxx` 方法所涵盖的分布，都可以作为参数。

### seed

`RandomDataGenerator` 默认使用 Well19937c 生成器，该生成器默认以系统时间和系统标识哈希码为 seed。从相同 seed 开始总是生成相同的值序列。要生成随机数序列，应该实例化一个 `RandomDataGenerator`，然后不停调用它的方法升成新数值，而不是每升成一个数值，创建一个新示例。例如，生成 1-1,000,000 范围内的 1000 个随机 long：

```java
RandomDataGenerator randomData = new RandomDataGenerator(); 
for (int i = 0; i < 1000; i++) {
    value = randomData.nextLong(1, 1000000);
}
```

以下通常**不会**产生良好的随机序列，因为 PRNG 每次循环都会重新 seed：

```java
for (int i = 0; i < 1000; i++) {
    RandomDataGenerator randomData = new RandomDataGenerator(); 
    value = randomData.nextLong(1, 1000000);
}
```

以下代码则每次执行都会产生相同的随机序列：

```java
RandomDataGenerator randomData = new RandomDataGenerator(1000); 
for (int i = 0; i = 1000; i++) {
    value = randomData.nextLong(1, 1000000);
}
```

## 随机向量

有些算法需要随机向量而不是随机标量。

当这些向量的分量不相关时，可以一次生成一个然后打包成向量。`UncorrelatedRandomVectorGenerator ` 类通过设置每个分量的均值和方差并生成整个向量来简化此过程。

当这些分量相关时，生成它们就困难多了。`CorrelatedRandomVectorGenerator` 提供了这些功能。用户需要设置一个完整的协方差矩阵，而不是简单的标准差向量。该矩阵包含概率的方差和相关性信息。

 相关随机向量生成主要用于包含多个变量的物理问题的蒙特卡洛模拟，例如，生成添加到 nominal 向量的误差向量。一种常见的情况是从多元正态分布抽取向量。

### 从二元正态分布生成随机向量

```java
// Create and seed a RandomGenerator (could use any of the generators in the random package here)
RandomGenerator rg = new JDKRandomGenerator();
rg.setSeed(17399225432l);  // Fixed seed means same results every time

// Create a GassianRandomGenerator using rg as its source of randomness
GaussianRandomGenerator rawGenerator = new GaussianRandomGenerator(rg);

// Create a CorrelatedRandomVectorGenerator using rawGenerator for the components
CorrelatedRandomVectorGenerator generator = 
    new CorrelatedRandomVectorGenerator(mean, covariance,
                                        1.0e-12 * covariance.getNorm(),
                                        rawGenerator);

// Use the generator to generate correlated vectors
double[] randomVector = generator.nextVector();
... 
```





## 随机排列、组合和抽样

要在集合中选择对象的随机样本，可以使用 `RandomDataGenerator` 的 `nextSample` 方法。具体来说：

- 如果 `c` 是包含至少 `k` 个对象的集合，`randomData` 是一个 `RandomDataGenerator` 实例，那么 `randomData.nextSample(c, k)` 返回一个长度为 `k` 的 `object[]` 数组，包含从集合中随机选择的元素。
- 如果 `c` 包含重复引用，返回的数组中也可能包含重复引用；否则返回的元素为 unique，即采用无放回抽样。

如果 `randomData` 是 `RandomDataGenerator` 实例，而 `n` 和 `k` 是整数，且 $k\le n$，那么：

- `randomData.nextPermutation(n, k)` 返回长度为 `k` 的 `int[]` 数组，其元素从整数 0 到 n-1 随机选择，没有重复。简而言之，`randomData.nextPermutation(n, k)` 返回一个从 n 取 k 个元素的随机排列。



## 参考

- https://hipparchus.org/hipparchus-core/random.html