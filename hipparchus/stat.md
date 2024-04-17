# 统计

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

`AbstractUnivariateStatistic` 和 `AbstractStorelessUnivariateStatistic` 提供了顶层接口的抽象实现。

每个统计量都作为单独的类实现，扩展上面两个抽象类之一。实例化和使用统计量的方法有：

- 直接实例化和使用；
- 使用聚合类 `DescriptiveStatistics` 和 `StreamingStatistics` 更方便。

`DescriptiveStatistics` 在内存中存储输入数据，并提供滚动窗口计算统计量。

`StreamingStatistics` 不会存储完整的输入数据。它使用 `RandomPercentile` 维护来自流的有界数据样本。

|聚合|统计量|是否存储|是否支持滚动|
|---|---|---|---|
|`DescriptiveStatistics`|min, max, mean, geometric mean, n, sum, sum of squares, standard deviation, variance, percentiles, skewness, kurtosis, median|Yes|Yes|
|`StreamingStatistics`|min, max, mean, geometric mean, n, sum, sum of squares, standard deviation, variance, percentiles|No|No|

`StreamingStatistics` 支持使用各种 `aggregate` 方法对结果进行聚合。

`MultivariateSummaryStatistics` 类似于 `StreamingStatistics`，但处理 n-tuple 值，而不是标量值。它还可以计算输入数据的协方差矩阵。

`DescriptiveStatistics` 和 `StreamingStatistics` 都不是线程安全的。

还有一个工具类 `StatUtils`，提供直接从 double[] 数组计算统计量的 static 方法。

下面是一些计算描述统计量的示例。

### 计算 summary 统计量

- 使用 `DescriptiveStatistics` 聚合类（存储输入数据）

```java
// 创建 DescriptiveStatistics
DescriptiveStatistics stats = new DescriptiveStatistics();

// 从数组添加数据
for (int i = 0; i < inputArray.length; i++) {
    stats.addValue(inputArray[i]);
}

// 计算统计量
double mean = stats.getMean();
double std = stats.getStandardDeviation();
double median = stats.getPercentile(50);
```

- 使用 `StreamingStatistics` 聚合类处理数据流

```java
// 创建 StreamingStatistics
StreamingStatistics stats = new StreamingStatistics();

// 从输入流读取数据
// 添加值，更新 sums, counters 等
while (line != null) {
    line = in.readLine();
    stats.addValue(Double.parseDouble(line.trim()));
}
in.close();

// 计算统计量
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
// 创建 DescriptiveStatistics，设置窗口大小
DescriptiveStatistics stats = new DescriptiveStatistics();
stats.setWindowSize(100);

// 从输入流读取数据、
// 每隔 100 个数据显示最近 100 个数据的均值
long nLines = 0;
while (line != null) {
    line = in.readLine();
    stats.addValue(Double.parseDouble(line.trim()));
    if (nLines == 100) {
        nLines = 0;
        System.out.println(stats.getMean());
    }
}
in.close();
```

### 计算多个样本的统计量并聚合结果

使用多个 `StreamingStatistics`，并将它们合并为最终结果：

```java
// 为每个样本单独创建 StreamingStatistics 
StreamingStatistics setOneStats = new StreamingStatistics();
StreamingStatistics setTwoStats = new StreamingStatistics();

// 添加值
setOneStats.addValue(2);
setOneStats.addValue(3);
setTwoStats.addValue(2);
setTwoStats.addValue(4);
...
// 聚合结果
StreamingStatistics aggregate = new StreamingStatistics();
aggregate.aggregate(setOneStats, setTwoStats);

// Full sample data is reported by the aggregate
double totalSampleSum = aggregate.getSum(); // 11
```

也可以使用 StatisticalSummary 聚合结果：

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

