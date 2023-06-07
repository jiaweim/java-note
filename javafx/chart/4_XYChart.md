# XYChart

## 简介

`XYChart<X.Y>` 定义包含两个坐标轴的 chart。泛型参数 `X` 和 `Y` 分别定义 x-axis 和 y-axis 的数据类型。

## 坐标轴表示

`XYChart` 的轴由抽象类 `Axis<T>` 的子类定义。类图如下：

![[2022-04-24-15-33-14.png|400]]

泛型参数 `T` 表示数据类型，如 `String`, `Number` 等。

`Axis` 的 `label` 属性定义轴标签。

`CategoryAxis` 和 `NumberAxis` 分别用于绘制 `String` 和 `Number`。它们包含特定于数值的属性。例如，`NumberAxis` 继承 `ValueAxis<T>` 的 `lowerBound` 和 `upperBound` 属性，分别用于指定数据的最小和最大值。

默认情况下，`ValueAxis` 的轴范围一般根据数据自定设置，可以将 `Axis<T>` 的 `autoRanging` 属性设置为 false 来关闭该行为。

例如，分别创建 CategoryAxis 和 NumberAixs，并设置标签：

```java
CategoryAxis xAxis = new CategoryAxis();
xAxis.setLabel("Country");

NumberAxis yAxis = new NumberAxis();
yAxis.setLabel("Population (in millions)");
```

## 添加数据

`XYChart` 中的数据由 x 轴和 y 轴定义二维坐标确定。`XYChart` 的数据保存在 `ObservableList` 类型的命名series 中。每个 series 包含多个数据。如何渲染数据点取决于图表类型。例如，散点图将数据点渲染为 symbol，而条形图将其渲染为一个 bar。

`XYChart.Data<X,Y>` 定义一个数据点，包含如下属性：

- XValue, x 轴坐标
- YValue, y 轴坐标
- extraValue, Object 类型，用于存储额外信息
- node，数据呈现方式

创建数据方式：

```java
XYChart.Data<Number, Number> data1 = new XYChart.Data<>(1950, 555);
XYChart.Data<Number, Number> data2 = new XYChart.Data<>(2000, 1275);
XYChart.Data<Number, Number> data3 = new XYChart.Data<>(2050, 1395);
```

`XYChart.Series<X,Y>` 表示一系列数据项。该类定义如下属性：

- name, series 名称
- data, `ObservableList of XYChart.Data<X,Y>` 实例，包含的数据
- chart, 对所属 chart 的引用
- node, 用于显示该 series 的 `Node`，根据 chart 类型会自动创建

下面创建一个 series，设置 name 并添加数据：

```java
XYChart.Series<Number, Number> seriesChina = new XYChart.Series<>();
seriesChina.setName("China");
seriesChina.getData().addAll(data1, data2, data3);
```

`XYChart.data` 属性为 `XYChart.Series` 