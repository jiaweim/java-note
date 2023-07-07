# Layout 工具类

2023-07-06, 17:21
****
## 1. 简介

layout 有几个工具类，用于辅助设置 spacing 和 directions。

## 2. Insets

`Insets` 类表示矩形区域四个方向的内边距。如图所示：

![|360](Pasted%20image%2020230706170357.png)

在上图中，两个矩形可能交叉，此时，offsets 可能为负值。offsets 是相对值，所以要正确解析 offsets，需要知道相对的是哪个矩形。

该类包含两个构造函数：

|构造函数|说明|
|---|---|
|`Insets(double topRightBottomLeft)`|四个offsets 值相同|
|`Insets(double top, double right, double bottom, double left)`|分别指定 offset 值|

`Insets.EMPTY()` 定义 offsets 均为 0 的 `Insets` 对象。

在 JavaFX 中，在以下情况会用到 `Insets`：

- Border insets：layout bounds 到 border 内边缘的距离
- Background insets：layout bounds 到 background 内边缘的距离
- Outsets：
- Insets

border stroke 或图片可能落在 `Region` 的 layout bounds 外。`Outsets` 表示 `Region` layout bounds 到 border 外边缘的距离。

## 3. HPos Enum

`HPos` 包括三个常量：`LEFT`, `CENTER`, `RIGHT`，用于定义水平位置和对齐。

## 4. VPos Enum

`VPos` 包括四个常量：`TOP`, `CENTER`, `BASELINE`, `BOTTOM`，用于定义垂直位置和对齐。

## 5. Pos Enum

`Pos` 定义水平和垂直位置和对齐，包括 `VPos` 和 `HPos`的所有可能组合。

- `BASELINE_CENTER`, `BASELINE_LEFT`, `BASELINE_RIGHT`
- `BOTTOM_CENTER`, `BOTTOM_LEFT`, `BOTTOM_RIGHT`
- `CENTER`, `CENTER_LEFT`, `CENTER_RIGHT`
- `TOP_CENTER`, `TOP_LEFT`, `TOP_RIGHT`

`Pos` 还包含两个方法：

- `getHpos(`), 返回 `HPos`，获得水平定位和对齐策略
- `getVpos()`, 返回 `VPos`, 获得垂直定位和对齐策略

## 6. HorizontalDirection Enum

`HorizontalDirection` 包含两个常量：`LEFT`和 `RIGHT`，分别表示左侧和右侧两个方向。

## 7. VerticalDirection Enum

`VerticalDirection` 包含两个常量：`UP` 和 `DOWN`，分别表示上、下两个方向。

## 8. Orientation Enum

`Orientation` 包含两个常量：`HORIZONTAL` 和 `VERTICAL`，分别表示水平、垂直方向。

## 9. Side Enum

`Side` 包含四个常量：`TOP`, `RIGHT`, `BOTTOM`, `LEFT`，表示矩形的四个边。

## 10. Priority Enum

容器包含的空间可能比子节点 preferred size 所需空间多一点或少一点。在容器缩放时，`Priority`用于指定子节点的缩放优先级。

`Priority`包含三个值：`ALWAYS`, `NEVER`, `SOMETIMES`：

- `ALWAYS`, 随着空余空间的缩放而缩放
- `NEVER`, 不缩放
- `SOMETIMES`, 当没有其它节点为 `ALWAYS`，或为 `ALWAYS` 优先级的节点无法消耗所有变化的空间，才执行缩放。
