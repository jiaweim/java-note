# Color

2023-06-19⭐
@author Jiawei Mao

****
## 概述

在 JavaFX 中，可以设置文本颜色、背景颜色。颜色有多种形式：单一纯色、渐变色，还可以使用图片填充背景。下图是 JavaFX 颜色相关的类，这些类都在 `javafx.scene.paint` 包中：

<img src="images/Pasted%20image%2020230619202936.png" style="zoom:50%;" />

`Paint` 是抽象类，是其它颜色相关类的基类。它只包含一个 `static` 方法，用于将字符串转换为合适的具体类：

 ```java
 public static Paint valueOf(String value)
 ```

返回的 `Paint` 实例，根据参数不同，可以是 `LinearGradient`, `RadicalGradient` 和 `Color` 中的一种。

很少直接使用 `valueOf`，一般用于将 CSS 文件中的颜色值转换为对应实例。如下：

 ```java
// redColor 是 Color 类型
Paint redColor = Paint.valueOf("red");

// aLinearGradientColor 是 LinearGradient 类型
Paint aLinearGradientColor = Paint.valueOf("linear-gradient(to bottom right, red, black)" );

// aRadialGradientColor 是 RadialGradient 类型
Paint aRadialGradientColor = Paint.valueOf("radial-gradient(radius 100%, red, blue, black)");
 ```

`Stop` 类和 `CycleMethod` enum 用于定义梯度颜色。

> [!TIP]
>
> 设置 `Node` 的 `color` 属性的方法一般以 `Paint` 类型为参数，这样就能使用四种颜色模式中的任意一种。

## Color 类

`Color` 类表示 RGB 颜色空间的一个颜色。

每种颜色都有一个 alpha 值，范围在 `[0.0, 1.0]` 或 `[0, 255]`，表示透明度。alpha=0.0 或 0 表示完全透明，alpha=1.0 或 255表示完全不透明。alpha 值默认为1.0，即完全不透明。

创建 `Color` 实例的方法有三种：

- 构造函数
- 工厂方法
- `Color` 类中定义的常量

`Color` 只有一个构造函数：

```java
public Color(double red, double green, double blue, double opacity)
```

例如，创建一个蓝色：

```java
Color blue = new Color(0.0, 0.0, 1.0, 1.0);
```

`Color` 提供如下的工厂方法：

- `Color color(double red, double green, double blue)`
- `Color color(double red, double green, double blue, double opacity)`
- `Color hsb(double hue, double saturation, double brightness)`
- `Color hsb(double hue, double saturation, double brightness, double opacity)`
- `Color rgb(int red, int green, int blue)`
- `Color rgb(int red, int green, int blue, double opacity)`

另外，还有 `valueOf()` 和 `web()` 可以从网络颜色字符串创建颜色。例如：

```java
Color blue = Color.valueOf("blue");
Color blue = Color.web("blue");
Color blue = Color.web("#0000FF");
Color blue = Color.web("0X0000FF");
Color blue = Color.web("rgb(0, 0, 255)");
Color blue = Color.web("rgba(0, 0, 255, 0.5)"); // 50% transparent blue
```

`Color` 定义了大约 140 种颜色常量，均为完全不透明颜色。

## ImagePattern

`ImagePattern` 使用图片填充形状。图片可以完全填充形状，也平铺起来填充。使用 `ImagePattern` 的步骤：

1. 使用 `Image` 载入图片文件
2. 定义一个矩形，为图片显示区域，称为锚点矩形

如果待填充的区域大于定义的矩形，则锚点矩形重复出现以填满形状。

`ImagePattern` 有两个构造函数：

- `ImagePattern(Image image)`：使用图片填满整个区域
- `ImagePattern(Image image, double x, double y, double width, double height, boolean proportional)`：如果 `proportional` 为 true，则定义的锚点矩形是相对填充区域的比值，否则，为本地坐标系统值。

例如，下面创建的两个 `ImagePattern` 等价：

```java
ImagePatterm ip1 = new ImagePattern(anImage);
ImagePatterm ip2 = new ImagePattern(anImage, 0.0, 0.0, 1.0, 1.0, true);
```

### 示例

下面是一个 37x25px 的蓝色圆角矩形：

![](blue_rounded_rectangle.png)

使用该图像创建 `ImagePattern`：

```java
Image img = create the image object...
ImagePattern p1 = new ImagePattern(img, 0, 0, 0.25, 0.25, true);
```

`ImagePattern` 会在要填充的形状的 (0,0) 位置创建一个锚点矩形，其宽度和高度均为要填充形状的 25%。因此，锚点矩形和水平和垂直方向均要重复 4 次。

- 填充矩形

```java
Rectangle r1 = new Rectangle(100, 50);
r1.setFill(p1);
```

![](Pasted%20image%2020230619213914.png)

- 填充三角形

```java
Polygon triangle = new Polygon(50, 0, 0, 50, 100, 50);
triangle.setFill(p1);
```

![](Pasted%20image%2020230619213918.png)

- 完整填充

用一张图像完整填充图形，需要将 `proportional` 设置为 true：

```java
ImagePatterm ip = new ImagePattern(yourImage, 0.0, 0.0, 1.0, 1.0, true)
```

- 完整示例

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ImagePatternApp extends Application {

    private Image img;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        // Create an Image object
        final String imgPath = getClass().getResource("/picture/blue_rounded_rectangle.png").toExternalForm();
        img = new Image(imgPath);
    }

    @Override
    public void start(Stage stage) {
        // 锚点矩形从 (0,0) 开始，宽和高均为待填充矩形的 25%
        ImagePattern p1 = new ImagePattern(img, 0, 0, 0.25, 0.25, true);
        Rectangle r1 = new Rectangle(100, 50);
        r1.setFill(p1);
        
        // 锚点矩形从 (0,0) 开始，宽和高为待填充矩形的 50%
        ImagePattern p2 = new ImagePattern(img, 0, 0, 0.5, 0.5, true);
        Rectangle r2 = new Rectangle(100, 50);
        r2.setFill(p2);

        // 锚点矩形使用绝对边框
        ImagePattern p3 = new ImagePattern(img, 40, 15, 20, 20, false);
        Rectangle r3 = new Rectangle(100, 50);
        r3.setFill(p3);

        // 填充圆形
        ImagePattern p4 = new ImagePattern(img, 0, 0, 0.1, 0.1, true);
        Circle c = new Circle(50, 50, 25);
        c.setFill(p4);

        HBox root = new HBox();
        root.getChildren().addAll(r1, r2, r3, c);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("Using Image Patterns");
        stage.show();
    }
}
```

![](Pasted%20image%2020230619213811.png)

## 线性渐变色

线性渐变色通过**渐变线**（gradient-line）定义：

- 渐变线上的每个点颜色不同，垂直于渐变线的直线上的所有点颜色相同
- 渐变线由起点和终点定义
- 渐变线上的一些点定义了渐变线的颜色，这些点称为 stop-color-points
- stop-color-points 之间的颜色通过插值计算

如下图所示：

<img src="images/Pasted%20image%2020230619215343.png" style="zoom:50%;" />

这是一个线性渐变颜色填充的矩形区域，渐变线从左到右定义。起点是白色，终点是黑色。在矩形左侧，所有点都是白色；在矩形右侧，所有点都是黑色。在矩形中间，颜色在白色到黑色之间变化。

### LinearGradient 类

`LinearGradient` 类实现了线性渐变色。该类提供了两个构造函数：

```java
LinearGradient(double startX, double startY, double endX, double endY, 
    boolean proportional, CycleMethod cycleMethod, List<Stop> stops)

LinearGradient(double startX, double startY, double endX, double endY, 
    boolean proportional, CycleMethod cycleMethod, Stop... stops)
```

startX 和 startY 定义了梯度线的起点坐标，endX 和 endY 定义梯度线的终点坐标。

proportional 指定处理起点和终点坐标的方式。true 表示起点和终点的坐标是相对填充形状的比例值，范围在 0 到 1 之间；false 则表示起点和终点的坐标是局部坐标系中的绝对坐标值。

`cycleMethod` 定义填充渐变色边界之外的区域，渐变色区域由起点和终点定义。例如，如果 proportional=true，起点为 (0.0, 0.0)，终点为 (0.5, 0.0)，那么渐变色区域为左边的一半。那么，剩下右边的一半如何处理？`cycleMethod` 就是为了处理该情况，其允许值由 `CycleMethod` enum 定义：

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

### 示例

了解完 `LinearGradient` 的构造函数，下面通过实例学习。例如，定义一个用线性渐变色填充的矩形：

```java
Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(1, Color.BLACK)};
LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, stops);
Rectangle r = new Rectangle(200, 100);
r.setFill(lg);
```

<img src="images/Pasted%20image%2020230619223610.png" style="zoom:67%;" />

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

### String 格式的线性渐变色

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

## 径向渐变色

径向渐变色从原点开始，然后以圆形或椭圆向外渐变。对圆形，该形状由原点和半径定义。颜色的起点也称为渐变的焦点（focus point of the gradient）。径向渐变色由三分部组成：

- 渐变形状（对圆形为原点和半径）
- 焦点：渐变线上的第一个点
- color-stops

渐变的焦点与渐变形状的原点可以不同。如下所示，焦点由两个参数确定：

- 角度（focus angle）：过原点水平线与焦点、原点连接线的角度
- 距离（focus distance）：原点和焦点的距离


![|500](Pasted%20image%2020230620085210.png)

焦点处定义 0% 位置的 color-stop。其它位置的颜色通过插值确定，即画一条穿过焦点的点，该点颜色根据渐变线上两侧 color-stop 的颜色进行插值。

### RadialGradient 类

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

### 椭圆填充

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

### String 格式

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

