# JavaFX Chart 简介

2023-06-01
****

JavaFX Chart API 定义在 `javafx.scene.chart` 包中，JavaFX 支持的图表类型如下所示：

![[Pasted image 20230601224000.png]]

抽象类 `Chart` 继承 `Node`，是所有 chart 的基类。Chart 可以添加到 scene graph，且支持 CSS 个性化。

JavaFX 将图表分为两类：

- 不包含坐标轴，如 `PieChart`
- 包含 x-轴和 y-轴，如 `XYChart`

每个 Chart 至少包含三部分：

- 标题
- legend
- 数据内容

不同类型的 chart 定义数据方式不同，下面是所有 `Chart` 的共同属性。。

|属性|说明|
|---|---|
|title|标题|
|titleSide|标题位置，默认上方，可用值 Side enum: TOP, RIGHT, BOTTOM, LEFT|
|legend|图例|
|legendSide|指定 legend 位置|
|legendVisible|legend 可见性|
|animated|动画效果，default=True|

`legend` 是 `Node` 类型，指定 chart 的图例。legend 默认在 chart 下方。


## LineChart

由 `javafx.scene.chart.LineChart` 类表示。
