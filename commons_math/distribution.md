# 分布

## 简介

commons-math 提供的所有分布都在 `org.apache.commons.math3.distribution` 包中，可以分为三种类型：

- `IntegerDistribution`

- `RealDistribution`
- `MultivariateRealDistribution`

## EmpiricalDistribution

使用 **EmpiricalDistribution** 类，可以基于一组给定数值生成数据。

```java
double[] input = load("data.txt"); // Get some data.
int binCount = 500;
EmpiricalDistribution empDist = EmpiricalDistribution.from(binCount, input);
ContinuousDistribution.Sampler sampler = empDist.createSampler(RandomSource.MT.create());
double value = sampler.nextDouble(); 
```

概率密度函数由输入的数据进行估计。其估计方法本质上是**高斯平滑可变核方法**。

所创建的采样器会返回服从经验分布的随机数值（即当生成大量此类数值时，其分布特征会与输入文件中数据的分布形态高度吻合）。**输入数据不会常驻内存存储**。
