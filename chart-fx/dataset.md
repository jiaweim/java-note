# 数据集


****
## 简介

数据集的主要类图如下：

![[Pasted image 20230605155205.png]]
其中：

- `DataSet2D` 定义二维数据点
- `DataSet3D` 定义三维数据点
- `DataSetError` 定义包含测量误差信息的数据
- `Histogram` 用于直方图数据的存储和处理

## DataSetMetaData

`DataSetMetaData` 接口专门用于处理元数据，与 `DataSet2D`, `DataSet3D` 以及 `DataSetError` 处于同一类图水平。

`AbstractDataSet` 在实现 `DataSet` 接口的同时实现了该接口，因此所有数据集类都支持处理元数据。

meta 信息：

```java
List<String> getErrorList();  
  
List<String> getInfoList();  
  
Map<String, String> getMetaInfo();  
  
List<String> getWarningList();
```

`EditableDataSet` 接口指可编辑数据集，只有 `DoubleDataSet` 和 `DoubleErrorDataSet` 等二维数据集实现该接口，`DoubleSet3D` 和 `Histogram` 不支持。

## DoubleDataSet

将 x, y 保存在两个单独数组，为 `Dataset` 的 double 实现。

- `DoubleDataSet(final String name)`

创建指定名称的数据集。

`DoubleDataSet` 添加数据的方式有两种：

- 一种是一个个添加：

```java
public DoubleDataSet add(final double x, final double y)
```

- 一种是一次性添加

```java
public DoubleDataSet set(final double[] xValues, final double[] yValues)
```

- 构造时添加

```java
final DoubleDataSet dataSet3 = new DoubleDataSet("data set #1", xValues, yValues1, N_SAMPLES, false)
```

最后的 `false` 指是否深度复制输入数组。
## DataSetError

`DataSetError` 在 `DataSet` 基础上添加了数据点误差信息。

![](Pasted%20image%2020230802095009.png)

误差类型定义在 enum `ErrorType` 中:

- `NO_ERROR`, 没有误差
- `SYMMETRIC`，误差对称
- `ASYMMETRIC`，误差不对称

|方法|说明|
|---|---|
|`ErrorType getErrorType(final int dimIndex)`|返回指定维度的误差类型|
|`double getErrorNegative(final int dimIndex, final int index)`|返回指定维度 `dimIndex` 索引 `index` 处的负误差（误差总是以正数表示）|
|`double getErrorPositive(final int dimIndex, final int index)`|返回指定维度 `dimIndex` 索引 `index` 处的正误差|
|`double getErrorNegative(final int dimIndex, final double x)`|返回 x 坐标对应点在指定维度 dimIndex 的负误差|
|`double getErrorPositive(final int dimIndex, final double x)`|返回 x 坐标对应点在指定维度 dimIndex 的正误差|
|`double[] getErrorsNegative(final int dimIndex)`|返回所有数据点在指定维度 dimIndex 的负误差|
|`double[] getErrorsPositive(final int dimIndex)`|返回所有数据点在指定维度 dimIndex 的正误差|

## DataSet2D

经典的二位数据集实现。类图如下：

![](Pasted%20image%2020230802102020.png)

针对二位数据，添加了相关的便捷方法：

```java
// 返回指定 x 坐标的 y 值
default double getValue(final double x) {
    return getValue(DIM_Y, x);
}

// 返回指定 index 处的 x 值
default double getX(final int index) {
    return get(DIM_X, index);
}

// 返回第一个与指定 x 最接近数据点的 index
default int getXIndex(double x) {
    return getIndex(DIM_X, x);
}

// 所有 x 值
default double[] getXValues() {
    return getValues(DIM_X);
}

// 返回指定 index 处的 y 值
default double getY(final int index) {
    return get(DIM_Y, index);
}

// 返回第一个与指定 y 最接近数据点的 index
default int getYIndex(double y) {
    return getIndex(DIM_Y, y);
}

// 返回 Y 值
default double[] getYValues() {
    return getValues(DIM_Y);
}
```

## DataSet3D

DataSet3D 表示三维数据点。类图如下：

![|300](Pasted%20image%2020230802113241.png)
其中 GridDataSet 表示笛卡尔坐标的网格数据集。

## Histogram

`Histogram` 是专门为直方图设计的数据集。直方图的 N 个 bins，由 n+1 个数值定义。

`Histogram` 额外添加了 bin-0 和 bin-N+1，用于存储超出范围的数据，在绘图时默认不显示。

`Histogram` 的类图如下：

![|500](Pasted%20image%2020230802113951.png)

## TestDataSet

![](Pasted%20image%2020230804132536.png)

`TestDataSet` 用于生成测试数据集。声明了几个相关方法：

```java
public interface TestDataSet<D extends TestDataSet<D>> extends DataSet2D {

    //  fire 更新通知
    D fireInvalidated(UpdateEvent evt);

    // 生成 x
    double[] generateX(final int count);

    // 生成 y
    double[] generateY(final int count);

    // 生成新的数据
    D update();
}
```



## 实现类

### FifoDoubleErrorDataSet

`FifoDoubleErrorDataSet` 数据集实现了 `DataSetError` 和 `DataSet2D`：

- 实现 FIFO 的固定长度数据集
- 添加了 `maxDistance` 功能，对参数 `x`，删除与其距离超过 maxDistance 的数据

### TransposedDataSet

对 DataSet 进行封装，执行数据转置操作。


## 工具类

### Formatter

`Formatter<T>` 接口定义数据和字符串格式化，只有一个实现 `DefaultNumberFormatter`，提供了数字的格式化功能。

`DefaultNumberFormatter` 内部类 enum `FormatMode` 指定格式化模式：

- `FIXED_WIDTH_ONLY`：使用十进制表示，字符串宽度通过 `setNumberOfCharacters()` 设置
- `FIXED_WIDTH_EXP`：使用指数表示，字符串宽度通过 `setNumberOfCharacters()` 设置
- `FIXED_WIDTH_AND_EXP`：使用十进制或指数表示，优先选择更短、有效数字更多的表示
    - 字符串宽度通过 `setNumberOfCharacters()` 设置，N.B. 当 `getNumberOfCharacters()` >= 6 时该模式才有效。
- `OPTIMAL_WIDTH`：默认选项，通过 `setFixedPrecision()` 设置有效数字，使用十进制和指数更短的表示
- `METRIC_PREFIX`：使用标准 SI 前缀。默认 precision 为 3，例如 1234.5678 -> "1.234k"
- `BYTE_PREFIX`：以 1024 为基数使用标准 SI 前缀
- `JDK`：使用默认 `toString()`

### ArrayCache

为大型基本类型数组提供简单缓存。用法：

```java
private final static String UNIQUE_IDENTIFIER = "class/app unique name";  
[..]  
 
final double[] localTempBuffer = ArrayCache.getCachedDoubleArray(UNIQUE_IDENTIFIER, 200);  
 
[..] user code [..]  
 
ArrayCache.release(UNIQUE_IDENTIFIER, 100);  
```

### Tuple

![|300](Pasted%20image%2020230804121848.png)

`Tuple` 包含 2 个字段的类。主要用于存储坐标点。

`DoublePoint` 指定坐标值为 `Double` 类型。

`DoublePointError` 坐标值为 `DoublePoint`，即包含 4 个数，2 个值为坐标，2 个值为误差。

### DataRange

`DataRange` 用于定义 DataSet 和 Axis 的最小值和最大值。

### CacheCollection

CacheCollection 实现数组缓冲功能，以节省内存。类图如下：

![](Pasted%20image%2020230807122919.png)