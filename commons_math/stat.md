# 统计

- [统计](#统计)
  - [简介](#简介)
  - [描述统计](#描述统计)
    - [计算 summary 统计量](#计算-summary-统计量)
    - [维护最近 100 个数据的滚动平均值](#维护最近-100-个数据的滚动平均值)
    - [计算多个样本的统计量并聚合结果](#计算多个样本的统计量并聚合结果)
  - [Frequency distributions](#frequency-distributions)
    - [统计 integer 频率分布](#统计-integer-频率分布)
    - [统计 string 频率分布](#统计-string-频率分布)
  - [协方差和相关性](#协方差和相关性)
  - [假设检验](#假设检验)
    - [单样本 t 检验](#单样本-t-检验)
    - [单因素 ANOVA 检验](#单因素-anova-检验)
  - [参考](#参考)

2024-12-10
@author Jiawei Mao
***

## 简介

统计包提供了基本的描述性统计、频率分布、线性回归、方差分析、相关性分析和各种假设检验。

## 描述统计

描述统计相关功能在`stat.descriptive` 包中：

- 算术和几何平均；
- 方差和标准差；
- 和、乘积、对数和、平方和；
- 最小、最大、中位数和百分位数；
- 偏度和峰度；
- 第一、第二、第三和第三动量。

除了百分位数和中位数，计算其它统计量都不需要保存完整输入数据。stat 提供了两种接口和实现，即存储和不存储输入数据。例如，`PSquarePercentile` 和 `RandomPercentile` 分别使用 `PSquare` 和 `RANDOM` 算法来计算百分位数。

`UnivariateStatistic` 为顶层接口，所有统计量都实现该接口。其 `evaluate()` 以 double[] 数组为参数，返回对应的统计值。`StorelessUnivariateStatistic` 扩展 `UnivariateStatistic` 接口，添加了 `increment()`, `getResult()` 和相关方法，以支持无缓存的流式实现。流式实现在调用 `increment()` 添加值时维护计数器、加和和其它状态信息。实现 `StorelessUnivariateStatistic` 接口的统计量不管通过 `increment()` 添加的数据流有多长，都只需要固定存储大小。

`AbstractUnivariateStatistic` 和 `AbstractStorelessUnivariateStatistic` 提供了顶层接口的抽象实现。如下图所示：

<img src="./images/image-20240418081041820.png" alt="image-20240418081041820" style="zoom:50%;" />

每个统计量都作为单独的类实现，扩展上面两个抽象类之一。实例化和使用统计量的方法有：

- 直接实例化和使用；
- 使用聚合类 `DescriptiveStatistics` 和 `StreamingStatistics` 更方便、高效。

<img src="./images/image-20240418081250736.png" alt="image-20240418081250736" style="zoom:50%;" />

`DescriptiveStatistics` 在内存中存储输入数据，并提供滚动窗口计算统计量的功能。

`StreamingStatistics` 不存储完整的输入数据。它使用 `RandomPercentile` 维护数据流的有界数据样本。

|聚合|统计量|是否存储|是否支持滚动|
|---|---|---|---|
|`DescriptiveStatistics`|min, max, mean, geometric mean, n, sum, sum of squares, standard deviation, variance, percentiles, skewness, kurtosis, median|Yes|Yes|
|`StreamingStatistics`|min, max, mean, geometric mean, n, sum, sum of squares, standard deviation, variance, percentiles|No|No|

`StreamingStatistics` 支持使用各种 `aggregate` 方法对结果进行**聚合**。

`MultivariateSummaryStatistics` 类似于 `StreamingStatistics`，但处理 n-tuple 值，而非标量值。它还可以计算输入数据的协方差矩阵。

`DescriptiveStatistics` 和 `StreamingStatistics` 都**不是线程安全**的。

还有一个工具类 `StatUtils`，提供直接从 double[] 数组计算统计量的 static 方法。

下面是一些计算描述统计量的示例。

### 计算 summary 统计量

- 使用 `DescriptiveStatistics` 聚合类（存储输入数据）

```java
// 1. 创建 DescriptiveStatistics
DescriptiveStatistics stats = new DescriptiveStatistics();

// 2. 从数组添加数据
for (int i = 0; i < inputArray.length; i++) {
    stats.addValue(inputArray[i]);
}

// 3. 计算统计量
double mean = stats.getMean();
double std = stats.getStandardDeviation();
double median = stats.getPercentile(50);
```

- 使用 `StreamingStatistics` 聚合类处理数据流

```java
// 1. 创建 StreamingStatistics
StreamingStatistics stats = new StreamingStatistics();

// 2. 从输入流读取数据
// 添加值，更新 sums, counters 等
while (line != null) {
    line = in.readLine();
    stats.addValue(Double.parseDouble(line.trim()));
}
in.close();

// 3. 计算统计量
double mean = stats.getMean();
double std = stats.getStandardDeviation();
```

- 使用 `StatUtils` 工具类

```java
// 直接用 double[] 计算统计量
double mean = StatUtils.mean(values);
double std = FastMath.sqrt(StatUtils.variance(values));
double median = StatUtils.percentile(values, 50);

// 计算前 3 个数据的均值
mean = StatUtils.mean(values, 0, 3);
```

### 维护最近 100 个数据的滚动平均值

使用 `DescriptiveStatistics`，将窗口大小设置为 100：

```java
// 1. 创建 DescriptiveStatistics，设置窗口大小
DescriptiveStatistics stats = new DescriptiveStatistics();
stats.setWindowSize(100);

// 2. 从输入流读取数据、
// 每隔 100 个数据显示最近 100 个数据的均值
long nLines = 0;
while (line != null) {
    line = in.readLine();
    stats.addValue(Double.parseDouble(line.trim()));
    if (nLines == 100) {
        nLines = 0;
        // 3. 输出滚动平均值
        System.out.println(stats.getMean());
    }
}
in.close();
```

### 计算多个样本的统计量并聚合结果

使用多个 `StreamingStatistics`，并将它们合并为最终结果：

```java
// 1. 为每个样本单独创建 StreamingStatistics 
StreamingStatistics setOneStats = new StreamingStatistics();
StreamingStatistics setTwoStats = new StreamingStatistics();

// 2. 添加值
setOneStats.addValue(2);
setOneStats.addValue(3);
setTwoStats.addValue(2);
setTwoStats.addValue(4);
...
// 3. 聚合结果
StreamingStatistics aggregate = new StreamingStatistics();
aggregate.aggregate(setOneStats, setTwoStats);

// Full sample data is reported by the aggregate
double totalSampleSum = aggregate.getSum(); // 11
```

!!! note
    所谓聚合，就是将两部分数据集合并，计算合并后数据集的统计量。

也可以使用 `StatisticalSummary` 聚合结果：

```java
// 为每个样本创建 StreamingStatistic
StreamingStatistics setOneStats = new StreamingStatistics();
StreamingStatistics setTwoStats = new StreamingStatistics();

// 添加数据
setOneStats.addValue(2);
setOneStats.addValue(3);
setTwoStats.addValue(2);
setTwoStats.addValue(4);
...
// 聚合结果
StatisticalSummary aggregatedStats = StatisticalSummary.aggregate(setOneStats, setTwoStats);

// 整个样本数据的统计量
double totalSampleSum = aggregatedStats.getSum(); // 11
```

## Frequency distributions

`Frequency` 提供离散值的计数和百分比。

String, integer, long, char 以及任何实现 `Comparable` 接口的类都可以用 `Frequency` 计数。

在计数时，所有数值默认按自然顺序排序，但是可以通过向构造函数提供 `Comparator` 来覆盖该默认行为。

下面是一些示例。

### 统计 integer 频率分布

`LongFrequency` 提供混合 integer, long, Integer 和 Long 的频率统计：

```java
 LongFrequency f = new LongFrequency();
 f.addValue(1);
 f.addValue(new Integer(1));
 f.addValue(new Long(1));
 f.addValue(2);
 f.addValue(new Integer(-1));

 System.out.prinltn(f.getCount(1));   // displays 3
 System.out.println(f.getCumPct(0));  // displays 0.2
 System.out.println(f.getPct(new Integer(1)));  // displays 0.6
 System.out.println(f.getCumPct(-2));   // displays 0
 System.out.println(f.getCumPct(10));  // displays 1
```

### 统计 string 频率分布

区分大小写，自然排序：

```java
Frequency<String> f = new Frequency<>();
f.addValue("one");
f.addValue("One");
f.addValue("oNe");
f.addValue("Z");
System.out.println(f.getCount("one")); // displays 1
System.out.println(f.getCumPct("Z"));  // displays 0.5
System.out.println(f.getCumPct("Ot")); // displays 0.25
```

不区分大小写：

```java
Frequency<String> f = new Frequency<>(String.CASE_INSENSITIVE_ORDER);
f.addValue("one");
f.addValue("One");
f.addValue("oNe");
f.addValue("Z");
System.out.println(f.getCount("one"));  // displays 3
System.out.println(f.getCumPct("z"));  // displays 1
```

## 协方差和相关性

`org.hipparchus.stat.correlation` 提供计算成对数组或矩阵 columns 之间的协方差和相关性：

- `Covariance` 计算协方差；
- `PearsonsCorrelation` 计算 Pearson 相关系数；
- `SpearmansCorrelation` 计算 Spearman 秩相关；
- `KendallsCorrelation` 计算 Kendall tao 秩相关。

实现注意事项：

- 无偏协方差的计算公式为 $cov(X,Y)=\sum(x_i-E(X))(y_i-E(Y))/(n-1)$，是否对协方差进行偏差校正由参数 `biasCorrected` 设置，默认为 `true`，若为 `false`，底数为 $n$；
- `SpearmansCorrelation` 对输入数据应用 rank-transformation，在排序后的数据上计算 Pearson 相关系数。排序算法可配置，默认为 `NaturalRanking`；
- `KendallsCorrelation` 计算两个量之间的关联性。tao 检验是基于 tao 系数的非参假设检验。

**计算 2 个数组的协方差**

- 计算 2 个 double 数组的协方差

```java
new Covariance().covariance(x, y)
```

- 没有校正的协方差

```java
covariance(x, y, false)
```

**协方差矩阵**

计算矩阵 `data` 不同 columns 的协方差矩阵：

```java
new Covariance().computeCovarianceMatrix(data)
```

这里默认计算的无偏协方差，若需要去掉校正，可使用：

```java
computeCovarianceMatrix(data, false)
```

**2 个数组的 Pearson 相关系数**

x 和 y 是两个 double 数组，计算相关系数：

```java
new PearsonsCorrelation().correlation(x, y)
```

**Pearson 相关矩阵**

计算矩阵 data 不同 columns 之间的相关系数矩阵：

```java
new PearsonsCorrelation().computeCorrelationMatrix(data)
```

返回的相关系数矩阵的第 i,j 条是 `data` 的第 i 列和第 j 列之间的 Pearson 相关系数。

**Pearson 相关显著性和标准差**

计算与 Pearson 相关系数的标准差和显著性，首先要创建 `PearsonsCorrelation` 实例：

```java
PearsonsCorrelation correlation = new PearsonsCorrelation(data);
```

`data` 

## 假设检验

> 2024年12月10日⭐

`org.hipparchus.stat.inference` 包提供 t-检验、卡方检验、G-检验、单因素方差分析、Mann-Whitney U、Wilcoxon signed rank 和 Binomial 检验，以及 t, chi-square, G, one-way ANOVA, Mann-Whitney U, Wilcoxon signed rank, Kolmogorov-Smirnov 检的 p-Value 计算。

对应的类分别为：`TTest`, `ChiSquareTest`, `GTest`, `OneWayAnova`, `MannWhitneyUTest`, `WilcoxonSignedRankTest`, `BinomialTest`, `KolmogorovSmirnovTest`。

`InferenceTestUtils` 类提供创建 test 实例或直接计算检验统计量的静态方法。下面的示例都使用 `InferenceTestUtils` 执行检验。

创建检验对象实例的方法有两种，例如

- 静态方法：`InferenceTestUtils.getTTest()`
- 构造函数：`new TTest()`

**实现要点：**

- 支持单样本和双样本 t 检验。双样本 t 检验可以是配对 t 检验，也可以不配对；非配对双样本 t 检验可以假设总体方差相等或不等下进行。当假设方差相等，使用合并方差估计来计算 t 统计量，自由度等于样本大小之和减 2.当假设方差不等，t 检验统计量使用两个样本方差，并采用 Welch-Satterwaite 近似来计算自由度。不管哪种情况，都提供计算 t 统计量、p-value 和执行固定显著性水平的检验。假设等方差的类或方法名以 "homoscedastic" 开头，仅以 "t" 开头的类或方法不假设方差相等。
- t 检验返回的 p-value 的有效性取决于数据是否符合 t 检验的前提假设
- t，卡方和方差分析返回的 p-value 是精确的，基于 `distribution` 包中 t 分布、卡方分布和 F 分布的数值近似
- G-test 实现提供了两个 p-value: gTest(expected, observed)，

### 单样本 t 检验

将 `double[]` 数组的平均值与固定值进行比较：

```java
double[] observed = {1d, 2d, 3d};
double mu = 2.5d;
System.out.println(InferenceTestUtils.t(mu, observed));
```

该代码显示与单样本 t 检验相关的 t 统计量。

也可以将 `StreamingStatistics` 描述的数据集的平均值与固定值比较：

```java
double[] observed ={1d, 2d, 3d};
double mu = 2.5d;
StreamingStatistics sampleStats = new StreamingStatistics();
for (int i = 0; i < observed.length; i++) {
    sampleStats.addValue(observed[i]);
}
System.out.println(TestUtils.t(mu, sampleStats));
```

### 单因素 ANOVA 检验

> 2024年12月10日⭐

```java
double[] classA =
   {93.0, 103.0, 95.0, 101.0, 91.0, 105.0, 96.0, 94.0, 101.0 };
double[] classB =
   {99.0, 92.0, 102.0, 100.0, 102.0, 89.0 };
double[] classC =
   {110.0, 115.0, 111.0, 117.0, 128.0, 117.0 };
List classes = new ArrayList();
classes.add(classA);
classes.add(classB);
classes.add(classC);
```

然后使用 `OneWayAnova` 实例或 `InferenceTestUtils` 方法计算所有均值相等的零假设相关的 F 统计量或 p-value：

```java
double fStatistic = InferenceTestUtils.oneWayAnovaFValue(classes); // F-value
double pValue = InferenceTestUtils.oneWayAnovaPValue(classes); // P-value
```

如果固定显著性水平，则返回一个 bool 值，表示在该显著性水平是否拒绝零假设：

```java
InferenceTestUtils.oneWayAnovaTest(classes, 0.01); // returns a boolean
                                 // true means reject null hypothesis
```

## 参考

- https://hipparchus.org/hipparchus-stat/index.html