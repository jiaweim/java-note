# ScatterChart

## 简介

散点图将数据呈现为符号（symbol），相同序列中的数据使用相同符号。符号的位置由数据的 x 坐标和 y 坐标确定。

散点图由 `ScatterChart` 类实现。x 轴和 y 轴可以使用任意类型的 `Axis`。构造函数：

- `ScatterChart(Axis<X> xAxis, Axis<Y> yAxis)`
- `ScatterChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<XYChart.Series<X,Y>> data)`

`Axis` 的 `autoRanging` 默认为 true。
