# 线性渐变色

2023-06-19, 23:18
****
## 1. 简介

线性渐变色通过**渐变线**（gradient line）定义：

- 渐变线上的每个点颜色不同，垂直于渐变线的直线上的所有点颜色相同
- 渐变线由起点和终点定义
- 渐变线上的一些点定义了渐变线的颜色，这些点称为 stop-color points
- stop-color points 之间的颜色通过插值计算

如下图所示：

![](Pasted%20image%2020230619215343.png)

这是一个线性渐变颜色填充的矩形区域，渐变线从左到右定义。起点是白色，终点是黑色。在矩形左侧，所有点都是白色；在矩形右侧，所有点都是黑色。在矩形中间，颜色在白色到黑色之间变化。

## 2. LinearGradient 类

`LinearGradient` 类实现了线性渐变色。该类提供了两个构造函数：

```java
LinearGradient(double startX, double startY, double endX, double endY, 
	boolean proportional, CycleMethod cycleMethod, List<Stop> stops)

LinearGradient(double startX, double startY, double endX, double endY, 
	boolean proportional, CycleMethod cycleMethod, Stop... stops)
```

startX 和 startY 定义了梯度线的起点坐标，endX 和 endY 定义梯度线的终点坐标。

proportional 指定处理起点和终点坐标的方式。true 表示起点和终点的坐标是相对填充形状的比例值，范围在 0 到 1 之间；false 则表示起点和终点的坐标是局部坐标系中的绝对坐标值。

`cycleMethod` 定义填充渐变色边界之外的区域，渐变色区域由起点和终点定义。例如，如果 proportional=true，起点为 (0.0, 0.0)，终点为 (0.5, 0.0)，那么渐变色区域为左边的一半。那么，剩下右边的一半如何处理？`cycleMethod` 就是为了处理该情况，其允许值由 CycleMethod enum 定义：

- CycleMethod.NO_CYCLE
- CycleMethod.REFLECT
- CycleMethod.REPEAT

`NO_CYCLE` 用渐变线末端的颜色填充余下区域：

- 如果只定义了左半边，那么右边用中间的颜色填充
- 如果只定义了中间 50% 区域，那么左侧 25% 用渐变线起点颜色填充，右侧 25% 用渐变线终点颜色填充。

`REFLECT` 表示用颜色梯度的镜像来填充余下区域。

`REPEAT` 则是重复梯度颜色。

`stops` 参数定义梯度线上的 color-stop 点。color-stop 点由 Stop 类定义，其构造函数为：

```java
Stop(double offset, Color color)
```

offset 值从 0 到 1，表示 color-stop 和起点的相对距离，起点的 offset 为 0，终点为 1.0，中间的 offset 为 0.5

要实现渐变色，至少定义两个 color-stop，且它们的颜色不同。

`LinearGradient` 不限制 Stop 的个数。

## 3. 示例

了解完 `LinearGradient` 的构造函数，下面通过实例学习。例如，定义一个用线性渐变色填充的矩形：

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

![](Pasted%20image%2020230619223610.png)

如果将 proportional 设置为 false，就需要修改终点坐标：

```java
LinearGradient lg = new LinearGradient(0, 0, 200, 0, false, NO_CYCLE, stops);
```

- 没填充满的情况

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 0.5, 0, true, NO_CYCLE, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

![](Pasted%20image%2020230619224102.png)

这里 LinearGradient 的终点坐标为 (0.5, 0)，所以刚好填充到中间。

采用了 `NO_CYCLE` 模式，所以右边余下的一般使用线性渐变色最右边的颜色填充。

- 镜像模式

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 0.5, 0, true, REFLECT, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

![](Pasted%20image%2020230619224508.png)

这里采用了 `REFLECT` 模式填充右边一半。

- 更多 REFLECT 模式

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 0.1, 0, true, REFLECT, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

![](Pasted%20image%2020230619224610.png)

下一个渐变色梯度总是上一个的镜像。

- 50% 的 REPEAT 模式

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 0.5, 0, true, REPEAT, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

![](Pasted%20image%2020230619224751.png)

- 10% 的 REPEAT 模式

将终点的 x 设置为 0.1.

![](Pasted%20image%2020230619224826.png)

- 多个 stops

将渐变线分为四部分，定义 5 个颜色：

```java
Stop[] stops = new Stop[]{new Stop(0, Color.RED),
						  new Stop(0.25, Color.GREEN),
						  new Stop(0.50, Color.BLUE),
						  new Stop(0.75, Color.ORANGE),
						  new Stop(1, Color.YELLOW)};
LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

![](Pasted%20image%2020230619225023.png)

- 其它角度的渐变线

定义从左上角到右下角的渐变色。

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 1, 1, true, NO_CYCLE, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

- 定义 10% 的左上角到右下角的渐变色，模式设为 REPEAT

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), 
						  new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 0.1, 0.1, true, REPEAT, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

![](Pasted%20image%2020230619225315.png)

## 4. String 格式的线性渐变色

使用 LinearGradient 的 `valueOf(String colorString)` 可以解析字符串格式的线性渐变色。该格式一般在 CSS 文件中使用。语法为：

```css
linear-gradient([gradient-line], [cycle-method], color-stops-list)
```

其中 `[]` 中的参数为可选参数：

- `gradient-line` 的默认值为 "to bottom"
- `cycle-method` 的默认值为 `NO_CYCLE`

指定渐变线的方式有两种：

1. 使用两个点，即起点和终点
2. 使用 side 或 corner

使用数据点的语法：

```css
from point-1 to point-2
```

其中点的坐标为比例值或像素值。对一个 200px 宽 100px 高的矩形，水平渐变线的指定方式：

```css
from 0% 0% to 100% 0%
```

或者：

```css
from 0px 0px to 200px 0px
```

使用 side 或 corner 的语法：

```css
to side-or-corner
```

side-or-corner 值包括：top, left, bottom, right, top left, bottom left, bottom right, top right。

使用 side-or-corner 语法，只需要指定终点，起点会自动推断出来。例如：

- "to top" 表示从下到上
- "to bottom right" 表示从左上到右下
 
`cycle-method` 的值为 `repeat` 或 `reflect`。忽略 `cycle-method` 即表示默认值 NO_CYCLE，如果显式将 `cycle-method` 指定为 NO_CYCLE 会抛出异常。

`color-stops-list` 为 color-stop 列表。color-stop 包含 web 颜色名称以及距离起点的距离（可选）。例如：

- `white, black`
- `white 0%, black 100%`，百分比指定距离
- `white 0%, yellow 50%, blue 100%`
- `white 0px, yellow 100px, red 200px`，px 指定距离

如果不指定起点和终点 color-stop 的位置，起点默认为 0%，终点默认为 100%。因此 "white, black" 等价于  "white 0%, black 100%"。

对包含多个 color-stop 的情况，如果不指定位置，默认等距分配。例如，下面两个等价：

- `white, yellow, black, red, green`
- `white 0%, yellow 25%, black 50%, red 75%, green 100%`

也可以只指定一部分 color-stop 的位置。例如，如下两个等价：

- `white, yellow, black 60%, red, green`
- `white 0%, yellow 30%, black 60%, red 80%, green 100%`

如果后面 color-stop 的位置比前面的小，则被修正为和前一个 color-stop 相同的位置。因此：

```css
white, yellow 50%, black 10%, green
```

被修正为：

```css
white 0%, yellow 50%, black 50%, green 100%
```

- 创建一个从上到下，NO_CYCLE 的线性渐变色

```css
linear-gradient(white, black)
```

等价于：

```css
linear-gradient(to bottom, white, black)
```

- 在代码中使用字符串格式

```java
String value = "from 0px 0px to 100px 0px, repeat, white 0%, black 100%";
LinearGradient lg2 = LinearGradient.valueOf(value);
Rectangle r2 = new Rectangle(200, 100);
r2.setFill(lg2);
```

![](Pasted%20image%2020230619231811.png)
