# Stroke

2023-06-05
****
## 简介

Stroking 指绘制形状，形状的轮廓又称为 stroke，可以将其理解为画笔。`Shape` 类包含多个属性用于定义 stroke：

- stroke
- strokeType
- strokeLineCap
- stroleLineJoin
- strokeMiterLimit
- strokeDashOffset

## 属性

### stroke

`stroke` 属性指定颜色。除了 `Line`, `Path` 和 `Polyline` 的默认颜色为 `Color.BLACK`，其它形状默认为 `null`。

### strokeWidth

`strokeWidth` 指定线条宽度，默认 1.0px。

### strokeType

画笔沿着形状的边框绘制，`strokeType` 定义 `strokeWidth` 和边框的相对位置，由 enum `StrokeType` 定义，包括 `CENTERED`, `INSIDE` 和 `OUTSIDE`，默认 `CENTERED`：

| StrokeType | 说明                                       |
| ---------- | ------------------------------------------ |
| `CENTERED` | `strokeWidth` 的一半在边框里，一半在边框外 |
| `INSIDE`   | `strokeWidth` 全在边框里                   |
| `OUTSIDE`  | `strokeWidth` 全在边框外                   |

`strokeWidth` 包含在 layout bounds 中。如下所示，4 个正方形（50px）：

- 第一个没有 stroke，layout bounds 为 $50\times 50$；
- 第二个 `strokeWidth` 为 4px，`INSIDE` 类型，layout bounds 为 $50\times 50$；
- 第三个 `strokeWidth` 为 4px，`CENTERED` 类型，因此有 2px 在边框内，2px 在边框外，所以 layout bounds 为 $54\times 54$；
- 第四个 `strokeWidth` 为 4px，类型为 `OUTSIDE`，即整个 stroke 都在边框外，所以 layout bounds 为 $58\times 58$；

![[Pasted image 20230602153044.png|200]]

### strokeLineCap

`strokeLineCap` 定义线段末端的样式，可选样式为 `StrokeLineCap` enum 类型: 

| StrokeLineCap | 说明、 |
| ------------- | ------ |
|`BUTT`|不添加装饰，默认|
|`ROUND`|在线段末端添加圆弧，圆弧半径为 stroke width 的一半。|
|`SQUARE`|线段向外延伸，延伸长度为 `strokeWidth` 的一半。|

![](images/2019-06-05-16-14-01.png)

从左到右，分别为 `BUTT`, `ROUND`, `SQUARE`。

### strokeLineJoin

定义线段的连接方式，取值为 enum StrokeLineJoin: `MITER`, `BEVEL`, `ROUND`。如图所示，从左到右依次为 MITER, BEVEL 和 ROUND。默认 MITER。

![](images/2019-06-05-16-14-34.png)

### strokeMiterLimit

定义 StrokeLineJoin.MITER 连接方式的最大延伸长度。值小于 1.0 时以 1.0 处理。如下图所示：

![](images/2019-06-05-16-14-57.png)

当两条线段的夹角很小时，MITER 连接方式会使延伸距离很长，A 是完整的延伸长度，以该延伸长度和 stroke width 的比值为单位（4.65）。如果延伸长度大于 `strokeMiterLimit`，则直接截断。

## 虚线

虚线使用 double 数组定义，`Shape.getStrokeDashArray()` 返回该 double 数组，数组值分别为 dash length, gap, dash length 等格式。`Shape.strokeDashOffset()` 返回 dash pattern 前面的 offset 值。

下面定义两个 `Polygon`，使用相同的 dash pattern，不同的 dash offset。第一个 offset 为0，为默认值:

```java
Polygon p1 = new Polygon(0, 0, 100, 0, 100, 50, 0, 50, 0, 0);
p1.setFill(null);
p1.setStroke(Color.BLACK);
p1.getStrokeDashArray().addAll(15.0, 5.0, 5.0, 5.0);

Polygon p2 = new Polygon(0, 0, 100, 0, 100, 50, 0, 50, 0, 0);
p2.setFill(null);
p2.setStroke(Color.BLACK);
p2.getStrokeDashArray().addAll(15.0, 5.0, 5.0, 5.0);
p2.setStrokeDashOffset(20.0);
```

样式：

![[Pasted image 20230605150248.png]]
