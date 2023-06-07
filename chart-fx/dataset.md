# 数据集

## Dataset

`Dataset` 的类图如下：

![[Pasted image 20230605155205.png]]其中：

- `DataSet2D` 定义二维数据点
- `DataSet3D` 定义三维数据点
- `DataSetError` 定义包含测量误差信息的数据
- `Histogram` 用于直方图数据的存储和处理

另外，`DataSetMetaData` 接口专门用于处理元数据，与 `DataSet2D`, `DataSet3D` 以及 `DataSetError` 处于同一类图水平，类图如下：
![[Pasted image 20230605155916.png]]
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

## DefaultDataSet

对 `DoubleDataSet` 进行简单包装，作为 x,y 二维数据集的默认实现。

## DataSetError

- DoubleErrorDataset

数据保存在 x, y, +eyn, -eyn 四个 double 数组中。

- DefaultErrorDataSet

对 `DoubleErrorDataset` 进行简单包装，作为 `DataSetError` 的默认实现。

## Event

### EventSource

- `AtomicBoolean autoNotification()`

一般来说，如果数据集中的数据发生变化，应该通知注册的监听器。Chart 一般通过 `Dataset` 注册监听器，以便在数据发生变化时发出通知，并更新 chart。