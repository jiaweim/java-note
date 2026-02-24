# SVG

2023-07-14, 09:33
****
## 1. 简介

`SVGPath` 类通过编码的字符串数据绘制图形。

- SVG 规范： https://www.w3.org/TR/SVG/
- 以字符串构造路径的规则： https://www.w3.org/TR/SVG/paths.html

JavaFX 支持一部分 SVG 规范。

SVGPath 包含一个无参构造函数：

```java
SVGPath sp = new SVGPath();
```

`SVGPath` 包含两个属性：

- `content`, 表示 svg 字符串
- `fillRule`, 指定填充规则，`FillRule.NONE_ZERO` 或 `FillRule.EVEN_ODD`，和上节讨论的填充规则一样

例如，使用 SVGPath 绘制三角形：

```java
SVGPath sp2 = new SVGPath();
sp2.setContent("M50, 0 L0, 50 L100, 50 Z");
sp2.setFill(Color.LIGHTGRAY);
sp2.setStroke(Color.BLACK);
```

`SVGPath` 的 `content` 字符串满足如下规则：

- 该字符串包含一系列命令
- 每个命令名称只有一个字符
- 命令后为参数
- 参数之间以逗号或空格分开。如 “M50, 0 L0, 50 L100, 50 Z” 和 “M50 0 L0 50 L100 50 Z” 意思相同。一般建议以逗号分隔。
- 命令之间不需要空格。例如“M50 0 L0 50 L100 50 Z” 可以写为 “M50 0L0 50L100 50Z”.

下面来详细讨论一下命令：

```svg
M50, 0 L0, 50 L100, 50 Z
```

该内容包含四个命令：

|命令	|解释|
|---|---|
|M50,0	|MoveTo(50,0)|
|L0,50	|LineTo(0,50)|
|L100,50|LineTo(100,50)|
|Z|ClosePath|

命令名和 `PathElement` 一一对应。

命令参数为坐标值，可以为绝对值或相对值：

- 当命令大写（如 M），表示绝对值
- 当命令小写（如 m），表示相对值

"closepath" 的命令为 `Z` 或 `z`。`closepath` 命令不带参数，大小写无所谓。

如下两个 SVG paths：

- M50, 0 L0, 50 L100, 50 Z
- M50, 0 l0, 50 l100, 50 Z

第一个路径使用绝对坐标系，第二个使用绝对和相对坐标系。

和 `Path` 一样，`SVGPath` 必须以 `moveTo` 开头，且必须是绝对坐标系。如果 `SVGPath` 以相对坐标 `moveTo` 命令开头，依然会当绝对坐标处理。

上面两个SVG paths 绘制的两个三角形完全不同，虽然参数相同。如下图所示：

![|250](Pasted%20image%2020230713225357.png)
第二个 path (M50, 0 l0, 50 l100, 50 Z) 命令的含义：

- moveTo (50, 0)
- 从当前点 (50, 0) 到 (50, 50) 绘制一条线。因为 `l` 是相对命令，所以(0, 50) 加上上一个点 (50, 0) 获得终点 (50, 50)
- 从当前点 (50, 50) 到 (150, 100) 绘制一条线。原因同上
- 关闭路径 (Z)

## 2. SVGPath 命令

下表列出了 `SVGPath` 常用命令和等价的 PathElement 类。命令以绝对路径（大写）给出，相对路径换成小写即可。其中 (+) 表示可以使用多个参数。

|命令|参数|命令名称|Path类|
|---|---|---|---|
|M|(x,y)+|moveto|`MoveTo`|
|L|(x,y)+|lineto|`LineTo`|
|H|x+|lineto|`HLineTo`|
|V|y+|lineto|`VLineTo`|
|A|(rx,ry,x-axis-rotation,large-arc-flag,sweep-flag,x,y)+|arcto|`ArcTo`|
|Q|(x1,y1,x,y)+|Quadratic Bezier curveto|`QuadCurveTo`|
|T|(x,y)+|Shorthand/smooth quadratic Bezier|curveto|`QuadCurveTo`|
|C|(x1, y1, x2, y2, x, y)+|curveto|`CubicCurveTo`|
|S|(x2,y2,x,y)+|Shorthand/smooth curveto|`CubicCurveTo`|
|Z|None|closePath|ClosePath|

## 3. moveTo

"moveTo" 命令在指定坐标 (x, y) 开始一个新的 subpath。`M` 后可以跟一对或多对坐标。第一对坐标识别起点 (x, y) 坐标，额外的点视为 "lineTo" 命令的参数。

如果 "moveTo" 命令是相对的，则  "lineTo" 命令也是相对的；如果 "moveTo" 命令是绝对的；则  "lineTo" 命令也是绝对额。

例如，下面两个 SVG paths 等价：

```svg
M50, 0 L0, 50 L100, 50 Z
M50, 0, 0, 50, 100, 50 Z
```

## 4. lineTo

"lineto"  命令有三个：`L`, `H`, `V`，都用绘制直线。

`L` 在当前点和指定点 (x, y) 之间绘制直线。如果指定多对 (x, y) 坐标，则绘制一条折线；最后一对 (x, y) 成为新的当前点。例如，下面的两个 SVG paths 绘制相同的三角形：

```svg
M50, 0 L0, 50 L100, 50 L50, 0
M50, 0 L0, 50, 100, 50, 50, 0
```

H 和 V 命令用于绘制水平和垂直线。

- H 命令在当前点 (cx, cy) 和 (x, cy) 之间绘制水平线
- V 命令在当前点 (cx, cy) 和 (cx, y) 之间绘制垂直线

H 和 V 命令后也可以跟多个参数化，最后一个参数定义当前点。例如 "M0, 0H200, 100 V50Z" 从 (0, 0) 到 (200, 0) 绘制水平线；再从 (200, 0) 到 (100, 0) 绘制水平线（貌似没什么意义），第二个命令使 (100, 0) 成为当前点；第三个命令从 (100, 0) 到 (100, 50) 绘制垂直线；命令 z 从 (100, 50) 到 (0, 0) 绘制直线。

示例：H, V 命令

```java
SVGPath p1 = new SVGPath();
p1.setContent("M0, 0H-50, 50, 0 V-50, 50, 0, -25 L25, 0");
p1.setFill(Color.LIGHTGRAY);
p1.setStroke(Color.BLACK);
```

![|150](Pasted%20image%2020230714084750.png)

## 5. arcto

"arcto" 命令 `A` 在当前点和指定 (x, y) 之间绘制椭圆弧。

- rx 和 ry 指定沿 x 轴和 y 轴的半径
- x-axis-rotation 使椭圆 x 轴的旋转角度（度数）
- large-arc-flag 和 sweep-flag 用于选择 4 种可能圆弧中的一个，使用 0 和 1 指定值，0 表示 false, 1 表示 true

arcto 也可以包含多个参数，最后一个弧线的终点成为当前点。

**示例：** 创建 2 个 arcs，第 1 个使用 1 个参数，第 2 个使用 2 个参数

```java
SVGPath p1 = new SVGPath();
// rx=150, ry=50, x-axis-rotation=0, large-arc-flag=0,
// sweep-flag 0, x=-50, y=50
p1.setContent("M0, 0 A150, 50, 0, 0, 0, -50, 50 Z");
p1.setFill(Color.LIGHTGRAY);
p1.setStroke(Color.BLACK);

// Use multiple arcs in one "arcTo" command
SVGPath p2 = new SVGPath();

// rx1=150, ry1=50, x-axis-rotation1=0, large-arc-flag1=0,
// sweep-flag1=0, x1=-50, y1=50
// rx2=150, ry2=10, x-axis-rotation2=0, large-arc-flag2=0,
// sweep-flag2=0, x2=10, y2=10
p2.setContent("M0, 0 A150 50 0 0 0 -50 50, 150 10 0 0 0 10 10 Z");
p2.setFill(Color.LIGHTGRAY);
p2.setStroke(Color.BLACK);
```

![|300](Pasted%20image%2020230714090204.png)

## 6. Quadratic Bezier curveto

命令 Q 和 T 都用于绘制二次 Bezier 曲线。

- 命令 Q 在当前点和指定点 (x, y) 之间使用控制点 (x1, y1) 绘制二次 Bezier 曲线。
- 命令 T 在当前点和指定点 (x, y) 之间使用上一个命令控制点的反射作为控制点绘制二次 Bezier 曲线。如果没有上一个命令或上一个命令不是 Q, q, T, t，则将当前点作为控制点。

即命令 Q 指定控制点，命令 T 预设控制点。

**示例：** 使用 Q 和 T 命令绘制二次 Bezier 曲线

Q 的前两个数指定控制点，后两个数指定终点 (x, y)。

```java
SVGPath p1 = new SVGPath();
p1.setContent("M0, 50 Q50, 0, 100, 50");
p1.setFill(Color.LIGHTGRAY);
p1.setStroke(Color.BLACK);

SVGPath p2 = new SVGPath();
p2.setContent("M0, 50 Q50, 0, 100, 50 T200, 50");
p2.setFill(Color.LIGHTGRAY);
p2.setStroke(Color.BLACK);
```

![|350](Pasted%20image%2020230714091205.png)
## 7. Cubic Bezier curveto

命令 C 和 S 用于绘制三次 Bezier 曲线。

- 命令 C ((x1, y1, x2, y2, x, y)+) 在当前点和指定点 (x, y) 之间使用控制点 (x1, y1) 和 (x2, y2) 绘制三次 Bezier 曲线
- 命令 S ((x2,y2,x,y)+) 在当前点和指定点 (x, y) 之间绘制三次 Bezier 曲线。第一个控制点为上一个命令的第二个控制点。如果没有上一个命令，或上一个命令不是 C, c, S, s，则使用当前点作为第一个控制点；(x2, y2) 为第二个控制点。

 **示例：** 使用 C 和 S 命令绘制三次 Bezier 曲线

第二条 path 中的 S 命令上一个 C 命令的第二个控制点作为第一个控制点

```java
SVGPath p1 = new SVGPath();
p1.setContent("M0, 0 C0, -100, 100, 100, 100, 0");
p1.setFill(Color.LIGHTGRAY);
p1.setStroke(Color.BLACK);

SVGPath p2 = new SVGPath();
p2.setContent("M0, 0 C0, -100, 100, 100, 100, 0 S200 100 200, 0");
p2.setFill(Color.LIGHTGRAY);
p2.setStroke(Color.BLACK);
```

![|350](Pasted%20image%2020230714093232.png)

## 8. closepath

"closepath" 命令 Z 和 z 在当前点和当前 subpath 的起点之间绘制一条直线。Z 和 z 效果一样。

