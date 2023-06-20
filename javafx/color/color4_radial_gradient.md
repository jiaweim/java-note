# 径向渐变色

2023-06-20, 11:02
****
## 1. 简介

径向渐变色从原点开始，然后以圆形或椭圆向外渐变。对圆形，该形状由原点和半径定义。颜色的起点也称为渐变的焦点（focus point of the gradient）。径向渐变色由三分部组成：

- 渐变形状（对圆形为原点和半径）
- 焦点：渐变线上的第一个点
- color-stops

渐变的焦点与渐变形状的原点可以不同。如下所示，焦点由两个参数确定：

- 角度（focus angle）：过原点水平线与焦点、原点连接线的角度
- 距离（focus distance）：原点和焦点的距离


![|500](Pasted%20image%2020230620085210.png)

焦点处定义 0% 位置的 color-stop。其它位置的颜色通过插值确定，即画一条穿过焦点的点，该点颜色根据渐变线上两侧 color-stop 的颜色进行插值。

## 2. RadialGradient 类

RadialGradient 类实现了径向渐变色。该类提供了两个构造函数：

```java
RadialGradient(double focusAngle, double focusDistance, 
			   double centerX, double centerY, double radius, 
			   boolean proportional, CycleMethod cycleMethod, 
			   List<Stop> stops)

RadialGradient(double focusAngle, double focusDistance, 
			   double centerX, double centerY, double radius, 
			   boolean proportional, CycleMethod cycleMethod, 
			   Stop... stops)
```

`focusAngle` 定义焦点的角度。顺时针计算角度，负值表示逆时针计算角度。

`focusDistance` 根据圆半径的百分比定义，在 -1 到 1 之间，所以焦点肯定在渐变圆内。最远在渐变圆圆弧上。

因此，焦点和焦距都支持正负数。如下图所示：

![|500](Pasted%20image%2020230620091409.png)

这里定义了 4 个位于 80% 距离的焦点。

centerX 和 centerY 指定渐变圆的原点位置，radius 指定渐变圆半径。

`proportional` 影响渐变圆参数的解释。如果为 true，则指定的比例值，如果为 false，则指定绝对值。

`cycleMethod` 和 `stops` 与 `LinearGradient` 中的相同。

- 白到黑渐变

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, 
									   NO_CYCLE, stops);
Circle c = new Circle(50, 50, 50);
c.setFill(rg);
```

![](Pasted%20image%2020230620092128.png)

这里 `focusAngle` 和 `focusDistance` 都是 0，所以焦点与原点重合。

centerX、centerY 和 proportional 都是 0.5，所以渐变圆刚好填充整个形状。

- 20% NO_CYCLE 渐变

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.2, true, 
									   NO_CYCLE, stops);
Circle c = new Circle(50, 50, 50);
c.setFill(rg);
```

![](Pasted%20image%2020230620092922.png)

这里 radius 只有 0.2，渐变圆填不满整个形状。采用了 NO_CYCLE 模式，所以用最外围的颜色填充余下空间。

- REPEAT 模式

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.2, true, 
									   REPEAT, stops);
Circle c = new Circle(50, 50, 50);
c.setFill(rg);
```

![](Pasted%20image%2020230620094053.png)

从渐变线理解 REPEAT。 

- 60 度的焦点角，20% 半径

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(60, 0.2, 0.5, 0.5, 0.2, true, 
									   REPEAT, stops);
Circle c = new Circle(50, 50, 50);
c.setFill(rg);
```

![](Pasted%20image%2020230620095834.png)

这里渐变圆还是位于中心，但是焦点移到 60 度 20% 半径位置，所以看起来就歪了。

## 3. 椭圆填充

- 填充矩形

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, 
									   REPEAT, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(rg);
```

矩形宽 200px，高 100px，因为 proportional=true，这样就出现两个半径，x 轴方向100px，y 轴方向 50px。于是出现了椭圆。

![](Pasted%20image%2020230620100852.png)

- 不想要椭圆填充

设置 proportional=false 就不会出现椭圆了。这时半径值只能指定为 px 数值。

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(0, 0, 100, 50, 50, false, 
									   REPEAT, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(rg);
```

![](Pasted%20image%2020230620101223.png)

这里半径为 50，刚好填充矩形的高度方向，采用了 REPEAT 模式，所以宽度方向沿渐变线重复填充。

如何用径向渐变填充三角形或其它形状？径向渐变的形状，是圆形还是椭圆，有几个影响因素。

| proportional 值 | 填充区域的边框 | 渐变形状 |
| --------------- | -------------- | -------- |
| true            | 正方形         | 圆       |
| true            | 非正方形       | 椭圆     |
| false           | 正方形         | 圆       |
| false           | 非正方形       | 圆       |

需要强调的是，填充区域的边框，不是填充区域本身。例如，假设你要填充一个三角形，三角形的边界由其宽度和高度决定，如果宽度和高度相同，则边界为正方形，否则为矩形。

下面填充一个三角形：

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.2, true, 
									   REPEAT, stops);
Polygon triangle = new Polygon(0.0, 0.0, 0.0, 100.0, 100.0, 100.0);
triangle.setFill(rg);
```

![](Pasted%20image%2020230620103234.png)

效果如上图左所示。右边的三角形，边框是矩形，所以填充成了椭圆：

```java
Polygon triangle = new Polygon(0.0, 0.0, 0.0, 100.0, 200.0, 100.0);
```

- 多种颜色

最后，用多种颜色填充一个圆。焦点放在逆时针 30 度方向的渐变圆边上，渐变圆原点放在中心：

```java
Stop[] stops = new Stop[]{
					new Stop(0, Color.WHITE),
					new Stop(0.40, Color.GRAY),
					new Stop(0.60, Color.TAN),
					new Stop(1, Color.BLACK)};
RadialGradient rg = new RadialGradient(-30, 1.0, 0.5, 0.5, 0.5, 
									   true, REPEAT, stops);
Circle c = new Circle(50, 50, 50);
c.setFill(rg);
```

![](Pasted%20image%2020230620103723.png)

## 4. String 格式

`RadialGradient.valueOf(String colorString)` 可以解析字符串格式定义的 RadialGradient。该格式一般在 CSS 中使用，其语法为：

```css
radial-gradient([focus-angle], [focus-distance], [center], radius, 
				[cycle-method], colorstops-list)
```

`[]` 内的参数为可选参数。

focus-angle 和 focus-distance 默认为 0。focus-angle 角度支持多种单位，focus-distance 只能指定为比例。例如：

- focus-angle 45.0deg
- focus-angle 0.5rad
- focus-angle 30.0grad
- focus-angle 0.125turn
- focus-distance 50%

`center` 和 `radius` 支持相对值或绝对值，但要统一，即两者都用相对值或都用绝对值。center 默认为 (0, 0)。示例：

- center 50px 50px, radius 50px
- center 50% 50%, radius 50%

`cycle-method` 参可用参数包括 repeat 和 reflect。不指定表示 NO_CYCLE。

`colorstops-list` 使用位置和颜色定义。位置是从焦点到渐变圆边距离的百分比。示例：

- white, black
- white 0%, black 100%
- red, green, blue
- red 0%, green 80%, blue 100%

代码示例：

```java
String colorValue = 
	"radial-gradient(focus-angle 45deg, focus-distance 50%, " +
	"center 50% 50%, radius 50%, white 0%, black 100%)";
RadialGradient rg = RadialGradient.valueOf(colorValue);
Circle c = new Circle(50, 50, 50);
c.setFill(rg);
```

![](Pasted%20image%2020230620105652.png)

